package com.school.sba.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.utility.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService{
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ResponseStructure<SchoolResponse> responseStructure;

	private School mapToSchoolRequest(SchoolRequest schoolRequest) {
		return School.builder().schoolName(schoolRequest.getSchoolName())
				.emailId(schoolRequest.getEmailId()).address(schoolRequest.getAddress())
				.contactNo(schoolRequest.getContactNo())
				.build();
	}

	private SchoolResponse mapToSchoolResponse(School school) {
		return SchoolResponse.builder().schoolId(school.getSchoolId())
				.schoolName(school.getSchoolName())
				.emailId(school.getEmailId()).address(school.getAddress())
				.contactNo(school.getContactNo())
				.build();
	}
	


	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> registerSchool(SchoolRequest schoolRequest) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		
		
		   return userRepository.findByUserName(name).map(u->{
			if(u.getRole().equals(UserRole.ADMIN)) {
				if(u.getSchool()==null) {
					School school = mapToSchoolRequest(schoolRequest);
					school= schoolRepository.save(school);
					SchoolResponse response = mapToSchoolResponse(school);
					u.setSchool(school);
					userRepository.save(u);
					responseStructure.setStatus(HttpStatus.CREATED.value());
					responseStructure.setMessage("School Created By ADMIN...!");
					responseStructure.setData(response);
					return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure,HttpStatus.CREATED);
				}else {
					throw new IllegalArgumentException("School already created By ADMIN");
				}
			}else {
				responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
				responseStructure.setMessage("Only ADMIN can create School...!");
				return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure,HttpStatus.BAD_REQUEST);
			}
		}).orElseThrow(()-> new UserNotFoundByIdException("Given UserId not found in the database"));
	}
}
