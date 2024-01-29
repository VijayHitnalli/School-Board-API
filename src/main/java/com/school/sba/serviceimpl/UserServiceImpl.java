package com.school.sba.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.AccessDeniedException;
import com.school.sba.exception.DataNotFoundException;
import com.school.sba.exception.InvalidUserRoleException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.utility.ResponseStructure;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ResponseStructure<UserResponse> responseStructure;
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	private SchoolRepository schoolRepository;
	@Autowired
	private SubjectRepository subjectRepository;

	private User mapToUserRequest(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName()).contactNo(userRequest.getContactNo())
				.password(passwordEncoder.encode(userRequest.getPassword())).email(userRequest.getEmail())
				.role(UserRole.valueOf(userRequest.getRole())).build();
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userID(user.getUserID()).userName(user.getUserName())
				.firstName(user.getFirstName()).lastName(user.getLastName()).contactNo(user.getContactNo())
				.email(user.getEmail()).userRole(user.getRole()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest) {
		User user = mapToUserRequest(userRequest);
		user.setIsDeleted(false);

		if (user.getRole() == UserRole.ADMIN) {
			if (userRepository.existsByRole(UserRole.ADMIN)) {
				responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
				responseStructure.setMessage("There should be only one ADMIN for an application");
				return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
			}

			user = userRepository.save(user);
			UserResponse response = mapToUserResponse(user);

			responseStructure.setStatus(HttpStatus.CREATED.value());
			responseStructure.setMessage("User data saved successfully");
			responseStructure.setData(response);

			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
		} else {
			responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
			responseStructure.setMessage("User role must be ADMIN");
			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUsers(UserRequest userRequest) {
		
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		
		 return userRepository.findByUserName(name).map(adminuser->{
			 
		 return	 schoolRepository.findById(adminuser.getSchool().getSchoolId()).map(school->{
				 
				 if(userRequest.getRole()== UserRole.TEACHER.name()|| userRequest.getRole()==UserRole.STUDENT.name())
					{
						User user = mapToUserRequest(userRequest);
						user.setSchool(school);
						userRepository.save(user);
						UserResponse userResponse = mapToUserResponse(user);

						responseStructure.setStatus(HttpStatus.CREATED.value());
						responseStructure.setMessage("User registerd Successfully");
						responseStructure.setData(userResponse);

						return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure,HttpStatus.CREATED);
					}
					else
						throw new InvalidUserRoleException("Unable to save user, Invalid user role");
				 
				 
			 }).orElseThrow(()->new SchoolNotFoundByIdException("School Not Present for a Admin"));
			 
		 }).orElseThrow(()->new UserNotFoundByIdException("User Not Authorised"));	

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> fetchUserById(int userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("Given userId->" + userId + " Not Found"));

		UserResponse response = mapToUserResponse(user);

		responseStructure.setStatus(HttpStatus.FOUND.value());
		responseStructure.setMessage("Data fetch Successfully...!");
		responseStructure.setData(response);

		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("Given userId->" + userId + " Not Found"));

		user.setIsDeleted(true);

		userRepository.save(user);
		UserResponse response = mapToUserResponse(user);

		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage("Data Deleted Successfully...!");
		responseStructure.setData(response);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addUserToAcademicProgram(int userId, int programId) {
		String adminName = SecurityContextHolder.getContext().getAuthentication().getName();

		return userRepository.findByUserName(adminName).map(admin -> {
			if (admin.getRole() == UserRole.ADMIN) {
				
				AcademicProgram program = academicProgramRepository.findById(programId).orElseThrow(
						() -> new AcademicProgramNotFoundByIdException("Program not found for given ID: " + programId));
				
				List<Subject> subjects = program.getSubjects();

				return userRepository.findById(userId).map(user -> {
					if (user.getRole() != UserRole.TEACHER) {
						if (!program.getUsers().contains(user)) {
							program.getUsers().add(user);
							program.getSubjects().add((Subject) subjects);
							
							user.getAcademicPrograms().add(program);
							userRepository.save(user);

							UserResponse response = mapToUserResponse(user);

							responseStructure.setStatus(HttpStatus.CREATED.value());
							responseStructure.setMessage("User added to the academic program");
							responseStructure.setData(response);

							return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
						} else {
							responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
							responseStructure.setMessage("User is already associated with the academic program");
							return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
						}
					} else {
						throw new InvalidUserRoleException("Invalid user role: " + user.getRole());
					}
				}).orElseThrow(() -> new UserNotFoundByIdException("Given userId not found in the database"));

			} else {
				throw new AccessDeniedException("Only admins are allowed to add academic programs.");
			}
		}).orElseThrow(() -> new UserNotFoundByIdException("UserId not Found"));
	}
	
	
//	return userRepo.findById(userId)
//			.map(user -> {
//				AcademicProgram pro = null;
//				if(user.getUserRole().equals(UserRole.ADMIN))
//					throw new IllegalRequestException("Failed to SET user to THIS PROGRAM");
//				else{
//					pro = academicRepo.findById(programId)
//						.map(program -> {
//							
//							if(user.getUserRole().equals(UserRole.TEACHER)) {
//								
//								if(user.getSubject()==null){ 
//									throw new IllegalRequestException("Teacher should assigned to a SUBJECT");}
//								
//								if(program.getSubjectList()==null || program.getSubjectList().isEmpty()){ 
//									throw new IllegalRequestException("Program should assigned with SUBJECTS to Add TEACHER");}
//								
//								if(!program.getSubjectList().contains(user.getSubject())){
//									throw new IllegalRequestException("Irrelevant TEACHER to the Academic Program");
//								}
//							}
//							
//							user.getAcademicprograms().add(program);
//							userRepo.save(user);
//							program.getUsers().add(user);
//							program = academicRepo.save(program);
//							return program;
//						}
//						)
//						.orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to SET user to THIS PROGRAM"));
//					}
//				structure.setStatusCode(HttpStatus.OK.value());
//				structure.setMessage(user.getUserRole()+" assigned with the Program "+pro.getProgramName());
//				structure.setData(mapToUserResponse(user));
//				
//				return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.OK); 
//			})
//			.orElseThrow(()-> new UserNotFoundByIdException("Failed to SET user to THIS PROGRAM"));

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int subjectId, int userId) {

		return userRepository.findById(userId).map(user -> {
			if (user.getRole().equals(UserRole.TEACHER)) {
				subjectRepository.findById(subjectId).map(subject -> {

					user.setSubject(subject);
					return userRepository.save(user);

				}).orElseThrow(() -> new DataNotFoundException("Subject Not Found for given subject id"));

				responseStructure.setStatus(HttpStatus.OK.value());
				responseStructure.setMessage("added subject to teacher");
				responseStructure.setData(mapToUserResponse(user));

				return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
			} else
				throw new UserNotFoundByIdException("Invalid User, we cant add");
		}).orElseThrow(() -> new UserNotFoundByIdException("User Not Present for given user id"));
	}

}
