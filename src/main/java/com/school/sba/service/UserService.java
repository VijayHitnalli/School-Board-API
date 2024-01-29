package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.utility.ResponseStructure;

public interface UserService {
	
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest);

	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUsers(UserRequest userRequest);

	public ResponseEntity<ResponseStructure<UserResponse>> fetchUserById(int userId);

	public ResponseEntity<ResponseStructure<UserResponse>> deleteUserById(int userId);

	public ResponseEntity<ResponseStructure<UserResponse>> addUserToAcademicProgram(int userId, int programId);

	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int subjectId, int userId);

}
