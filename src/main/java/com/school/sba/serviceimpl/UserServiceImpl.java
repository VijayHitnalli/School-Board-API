package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
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
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
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
	@Autowired
	private AcademicProgram academicProgram;
	@Autowired
	private ClassHour classHour;
	@Autowired
	private ClassHourRepository classHourRepository;
	
	

	private User mapToUserRequest(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName()).contactNo(userRequest.getContactNo())
				.password(passwordEncoder.encode(userRequest.getPassword())).email(userRequest.getEmail())
				.role(UserRole.valueOf(userRequest.getRole())).build();
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userID(user.getUserID()).userName(user.getUserName())
				.firstName(user.getFirstName()).lastName(user.getLastName()).contactNo(user.getContactNo())
				.email(user.getEmail()).userRole(user.getRole()).academicPrograms(user.getAcademicPrograms()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest) {
		User user = mapToUserRequest(userRequest);
		

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

		return userRepository.findByUserName(name).map(adminuser -> {

			return schoolRepository.findById(adminuser.getSchool().getSchoolId()).map(school -> {

				if (!userRequest.getRole().equals(UserRole.TEACHER)) {
						 
					User user = mapToUserRequest(userRequest);
					user.setSchool(school);
					userRepository.save(user);
					UserResponse userResponse = mapToUserResponse(user);

					responseStructure.setStatus(HttpStatus.CREATED.value());
					responseStructure.setMessage("User registerd Successfully");
					responseStructure.setData(userResponse);

					return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
				} else
					throw new InvalidUserRoleException("Unable to save user, Invalid user role");

			}).orElseThrow(() -> new SchoolNotFoundByIdException("School Not Present for a Admin"));

		}).orElseThrow(() -> new UserNotFoundByIdException("User Not Authorised"));

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
	
	
	public void autoDeleteUser() {
	    List<User> deletedUsers = userRepository.findByIsDeleted(true);
	   
	    for (User deletedUser : deletedUsers) {
	       
	       List<AcademicProgram> academicPrograms = deletedUser.getAcademicPrograms();
	        
	      for(AcademicProgram programs:academicPrograms) {
	    	  
		    List<ClassHour> classHours = academicProgram.getClassHours();
		        
		        for (ClassHour classHour : classHours) {
		            classHour.setAcademicProgram(null);
		        }
	      }
	        academicProgram.getUsers().remove(deletedUser);
	       
	        userRepository.delete(deletedUser);
	    }
	}

	
	public boolean hasSoftDeletedData() {
	    List<User> deletedUsers = userRepository.findByIsDeleted(true);
	    for (User deletedUser : deletedUsers) {
	        userRepository.delete(deletedUser);
	    }
	    return !deletedUsers.isEmpty();
	}
	

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundByIdException("Given userId->" + userId + " Not Found"));
		if(user.getRole().equals(UserRole.ADMIN)) {
			throw new IllegalArgumentException("ADMIN cannot Be Deleted");
		}
		
		user.setIsDeleted(true);

		User user2 = userRepository.save(user);
		UserResponse response = mapToUserResponse(user2);

		responseStructure.setStatus(HttpStatus.OK.value());
		responseStructure.setMessage("Data Deleted Successfully...!");
		responseStructure.setData(response);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.OK);
	}

	

	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addUserToAcademicProgram(int userId, int programId) {
		User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundByIdException("User Not Present for given user id"));

		AcademicProgram program = academicProgramRepository.findById(programId).orElseThrow(()-> new AcademicProgramNotFoundByIdException("Program Not present for given  program id"));

		List<Subject> subjects = program.getSubjects();

		if(user.getSubject()!=null && user.getRole().equals(UserRole.TEACHER) &&  subjects.contains(user.getSubject()))
		{
			user.getAcademicPrograms().add(program);
			userRepository.save(user);
			program.getUsers().add(user);
			academicProgramRepository.save(program);

			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("User added to Academic Programs");
			responseStructure.setData(mapToUserResponse(user));

			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure,HttpStatus.OK);
		}

		else if(user.getRole().equals(UserRole.STUDENT))
		{
			user.getAcademicPrograms().add(program);
			userRepository.save(user);
			program.getUsers().add(user);
			academicProgramRepository.save(program);

			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("User added to Academic Programs");
			responseStructure.setData(mapToUserResponse(user));

			return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure,HttpStatus.OK);
		}
		else

			throw new InvalidUserRoleException("Admin cannot be assigned to any Academic Programs");
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int subjectId, int userId) {

		return userRepository.findById(userId).map(user -> {
			if (user.getRole().equals(UserRole.TEACHER)&& user.getSubject()==null) {
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


	@Override
	public ResponseEntity<ResponseStructure<List<UserResponse>>> fetchUsersByRole(UserRole role,int programId) {
		
		if(role.equals(UserRole.ADMIN)) {
			throw new InvalidUserRoleException("Cannot fetch users for ADMIN role");
		}
		return academicProgramRepository.findById(programId).map(program->{
		    List<User> users = userRepository.findByRoleAndAcademicPrograms(role, program);
		    
			List<UserResponse> responses=new ArrayList<>();
			
			for(User user:users) {
				UserResponse response=mapToUserResponse(user);
				responses.add(response);
			}
			ResponseStructure<List<UserResponse>> structure = new ResponseStructure<>();
			structure.setStatus(HttpStatus.FOUND.value());
			structure.setMessage("Data found Successfully...!");
			structure.setData(responses);
			return new ResponseEntity<ResponseStructure<List<UserResponse>>>(structure,HttpStatus.FOUND);
			
		}).orElseThrow(()-> new AcademicProgramNotFoundByIdException("Not found"));
			
	}

	


}
