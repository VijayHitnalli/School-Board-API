package com.school.sba.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.UserNotFoundByIdException;
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

	private User mapToUserRequest(UserRequest userRequest) {
		return User.builder().userName(userRequest.getUserName()).firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName()).contactNo(userRequest.getContactNo())
				.password(userRequest.getPassword()).email(userRequest.getEmail())
				.role(userRequest.getRole())
				.build();
	}

	private UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userID(user.getUserID()).userName(user.getUserName())
				.firstName(user.getFirstName()).lastName(user.getLastName()).contactNo(user.getContactNo())
				.email(user.getEmail()).userRole(user.getRole())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest) {
		User user = mapToUserRequest(userRequest);
		user.setIsDeleted(false);
		if (user.getRole() == UserRole.ADMIN) {

			if (userRepository.existsByRole(UserRole.ADMIN)) {
				responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
				responseStructure.setMessage("There Should be only one ADMIN for an Application");
				return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
			}
		}
		user = userRepository.save(user);
		UserResponse response = mapToUserResponse(user);
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("User data Saved Successfully...!");
		responseStructure.setData(response);
		return new ResponseEntity<ResponseStructure<UserResponse>>(responseStructure, HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> fetchUserById(int userId) {
	    User user = userRepository.findById(userId).orElseThrow(
	            () -> new UserNotFoundByIdException("Given userId->" + userId + " Not Found"));

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

	
}
