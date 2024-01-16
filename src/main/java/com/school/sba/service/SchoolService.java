package com.school.sba.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.utility.ResponseStructure;

@Service
public class SchoolService {
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private ResponseStructure<School> responseStructure;
		
	
	public ResponseEntity<ResponseStructure<School>> saveSchool(School school) {
		
		School save = schoolRepository.save(school);
		
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("School Saved...!");
		responseStructure.setData(save);
		return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.CREATED);
	}

	public ResponseEntity<ResponseStructure<School>> updateSchool(int schoolId, School updatedSchool) {
		Optional<School> optional = schoolRepository.findById(schoolId);
		if(optional.isPresent()) {
		School oldSchool = optional.get();
			
		oldSchool=mapToCart(updatedSchool,oldSchool);
			schoolRepository.save(oldSchool);
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage(null);
			responseStructure.setData(oldSchool);
	
		}
		
		return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.OK);
	}

		private School mapToCart(School updatedSchool, School oldSchool) {
		oldSchool.setSchoolName(updatedSchool.getSchoolName());
		oldSchool.setAddress(updatedSchool.getAddress());
		oldSchool.setEmailId(updatedSchool.getEmailId());
		oldSchool.setContactNo(updatedSchool.getContactNo());
		return oldSchool;
	
		
	}
		
		

		public ResponseEntity<ResponseStructure<School>> getSchoolById(int schoolId) {
		Optional<School> optional = schoolRepository.findById(schoolId);
		if(optional.isPresent()) {
			responseStructure.setStatus(HttpStatus.FOUND.value());
			responseStructure.setMessage(null);
			responseStructure.setData(optional.get());
			return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.FOUND);
		}else {
			throw new SchoolNotFoundByIdException("Failed to find the School");
		}
	}
	
		public ResponseEntity<ResponseStructure<School>> deleteSchool(int schoolId) {
		Optional<School> optional = schoolRepository.findById(schoolId);
		if(optional.isPresent()) {
			School id = optional.get();
			
		
			schoolRepository.delete(id);
			
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage(null);
			responseStructure.setData(id);
	
		}
		
		return new ResponseEntity<ResponseStructure<School>>(responseStructure,HttpStatus.OK);
	}
}
