package com.school.sba.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolRequest {
	private String schoolName;
	private long contactNo;
	private String emailId;
	private String address;
}
