package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.School;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.utility.ResponseStructure;

@RestController
@EnableMethodSecurity
public class SchoolController {
	
	@Autowired
	private SchoolService schoolService;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/schools")
	public ResponseEntity<ResponseStructure<SchoolResponse>> registerSchool(@RequestBody SchoolRequest schoolRequest){
		return schoolService.registerSchool(schoolRequest);
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@PutMapping("/update/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> updateSchool(@PathVariable int schoolId, @RequestBody School updatedSchool){
//		return schoolService.updateSchool(schoolId,updatedSchool);
//	}
//	
//	@GetMapping("/get/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> getSchoolById(@PathVariable int schoolId){
////		logger.info("getShoppingCartById() method invoked");  // for one Method
//		return schoolService.getSchoolById(schoolId);
//	}
//		
//	@PostMapping("/delete/{schoolId}")
//	public ResponseEntity<ResponseStructure<School>> deleteSchool(@PathVariable int schoolId){
//		return schoolService.deleteSchool(schoolId);
//	}
	
	
}
