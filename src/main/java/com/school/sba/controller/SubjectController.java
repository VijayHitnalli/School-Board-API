package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utility.ResponseStructure;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@EnableMethodSecurity
public class SubjectController {
	@Autowired
	private SubjectService subjectService;
	
	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/academic-programs/{programId}/subjects")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(@PathVariable int programId, @RequestBody SubjectRequest subjectRequest){
		return subjectService.addSubjects(programId,subjectRequest);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjects(@PathVariable int programId,@RequestBody SubjectRequest subjectRequest){
		return subjectService.addSubjects(programId, subjectRequest);
	}
	@GetMapping("/subjects")
	public ResponseEntity<ResponseStructure<SubjectResponse>> findAllSubjects(){
		return subjectService.findAllSubjects();
	}
	
	@PutMapping("/subjects/{subjectId}/users")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToUser(@PathVariable int subjectId){
		return subjectService.addSubjectToUser(subjectId);
	}
}
