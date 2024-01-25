package com.school.sba.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.AccessDeniedException;
import com.school.sba.exception.InvalidUserRoleException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SchoolRepository;
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

	private User mapToUserRequest(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName()).contactNo(userRequest.getContactNo())
				.password(passwordEncoder.encode(userRequest.getPassword())).email(userRequest.getEmail())
				.role(userRequest.getRole()).build();
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
				 
				 if(userRequest.getRole().equals(UserRole.TEACHER)|| userRequest.getRole().equals(UserRole.STUDENT))
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
			 }).orElseThrow(()-> new SchoolNotFoundByIdException("School not present for the ADMIN"));
			 
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
	public ResponseEntity<ResponseStructure<UserResponse>> addUserToAcademicProgram(int userId,int programId) {
	    String adminName = SecurityContextHolder.getContext().getAuthentication().getName();

	    return userRepository.findByUserName(adminName).map(admin -> {
	        if (admin.getRole() == UserRole.ADMIN) {
	            AcademicProgram program = academicProgramRepository.findById(programId)
	                    .orElseThrow(() -> new AcademicProgramNotFoundByIdException(
	                            "Program not found for given ID: " + programId));

	            return userRepository.findById(userId).map(user -> {
	                if (user.getRole() == UserRole.STUDENT || user.getRole() == UserRole.TEACHER) {
	                    if (!program.getUsers().contains(user)) {
	                        program.getUsers().add(user);
	                        user.getAcademicPrograms().add(program);
	                        userRepository.save(user);

	                        UserResponse response = mapToUserResponse(user);

	                        responseStructure.setStatus(HttpStatus.CREATED.value());
	                        responseStructure.setMessage("User added to the academic program");
	                        responseStructure.setData(response);

	                        return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
	                    } else {
	                        responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
	                        responseStructure.setMessage("User is already associated with the academic program");
	                        return new ResponseEntity<>(responseStructure, HttpStatus.BAD_REQUEST);
	                    }
	                } else {
	                    throw new InvalidUserRoleException("Invalid user role: " + user.getRole());
	                }
	            }).orElseThrow(() -> new UserNotFoundByIdException("Given userId not found in the database"));

	        } else {
	            throw new AccessDeniedException("Only admins are allowed to add academic programs.");
	        }
	    }).orElseThrow(()-> new UserNotFoundByIdException("UserId not Found"));
	}


}
