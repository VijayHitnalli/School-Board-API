package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.utility.ResponseStructure;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class AcademicProgramController {
	
	@Autowired
	private AcademicProgramService academicProgramService;
	
	@PostMapping("/schools/{schoolId}/academic-programs")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(@PathVariable int schoolId, @RequestBody AcademicProgramRequest academicProgramRequest ){
		return academicProgramService.addAcademicProgram(schoolId,academicProgramRequest);
	}
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicPrograms(@PathVariable int schoolId){
		return academicProgramService.findAllAcademicPrograms(schoolId);
	}
	@DeleteMapping("academic-programs/{programId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deleteAcademicProgramById(@PathVariable int programId){
		return academicProgramService.deleteAcademicProgramById(programId);
	}
}
