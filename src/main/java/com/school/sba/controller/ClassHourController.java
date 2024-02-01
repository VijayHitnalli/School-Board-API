package com.school.sba.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.exception.DuplicateClassHourException;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateDTO;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.utility.ResponseStructure;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@EnableMethodSecurity
public class ClassHourController {
	
	@Autowired
	private ClassHourService classHourService;
	
	@Autowired
	private ClassHourRepository classHourRepository;
	
		@PostMapping("/academic-program/{programId}/class-hours")
		public ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHourToAcademicProgram(@PathVariable int programId,@RequestBody ClassHourRequest classHourRequest){
			return classHourService.addClassHourToAcademicProgram(programId,classHourRequest);
		}
		@PutMapping("/class-hours")
		public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHours(List<ClassHourUpdateDTO> classHourRequests){
			return classHourService.updateClassHours(classHourRequests);
		}
		@PutMapping("")
		public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHourWeekly()
}
