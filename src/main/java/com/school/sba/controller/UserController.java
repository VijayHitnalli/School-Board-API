package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.utility.ResponseStructure;

import jakarta.validation.Valid;

@RestController
@EnableMethodSecurity
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(@RequestBody  @Valid UserRequest userRequest){
		return userService.registerAdmin(userRequest);
	}
	
	
	@PostMapping("/users")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUsers(@RequestBody  @Valid UserRequest userRequest) {
		return userService.addOtherUsers(userRequest);
	}
	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> fetchUserById(@PathVariable int userId){
		return userService.fetchUserById(userId);
	}
	
	
	@DeleteMapping("/users/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(@PathVariable int userId){
		return userService.deleteUserById(userId);
	}

	@PutMapping("/academic-programs/{programId}/users/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<UserResponse>> addUserToAcademicProgram(@PathVariable int userId, @PathVariable int programId){
		return userService.addUserToAcademicProgram(userId,programId);
	}
}
