package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.utility.ResponseStructure;

public interface UserService {

	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest);

	public ResponseEntity<ResponseStructure<UserResponse>> fetchUserById(int userId);

	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId);

}
