package com.school.sba.responsedto;

import com.school.sba.enums.UserRole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class UserResponse {
		
	private int userID;
	private String userName;
	private String firstName;
	private String lastName;
	private long contactNo;
	private String email;
	private UserRole userRole;
}
