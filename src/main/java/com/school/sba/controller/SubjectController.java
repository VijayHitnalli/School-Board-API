package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utility.ResponseStructure;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class SubjectController {
	@Autowired
	private SubjectService subjectService;
	
	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;
	
	@PostMapping("/academic-programs/{programId}/subjects")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(@PathVariable int programId, @RequestBody SubjectRequest subjectRequest){
		return subjectService.addSubjects(programId,subjectRequest);
	}
}
