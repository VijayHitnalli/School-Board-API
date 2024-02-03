package com.school.sba.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateDTO;
import com.school.sba.requestdto.ExcelRequestDto;
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
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	
		@PostMapping("/academic-program/{programId}/class-hours")
		public ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHourToAcademicProgram(@PathVariable int programId,@RequestBody ClassHourRequest classHourRequest){
			return classHourService.addClassHourToAcademicProgram(programId,classHourRequest);
		}
		@PutMapping("/class-hours")
		public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHours(List<ClassHourUpdateDTO> classHourRequests){
			return classHourService.updateClassHours(classHourRequests);
		}
		
		@PostMapping("/academic-program/{programId}/class-hours/write-excel") //standalone Applicatipons
		public ResponseEntity<ResponseStructure<String>> xlSheetGeneration(@PathVariable int programId,@RequestBody ExcelRequestDto excelRequestDto) throws IOException{
			return classHourService.xlSheetGeneration(programId,excelRequestDto);
		}
		
		@PostMapping("/academic-program/{programId}/class-hours/from/{fromDate}/to/{toDate}/write-excel")
		public ResponseEntity<?> writeToExcelSheet(@PathVariable int programId, @PathVariable LocalDate fromDate,@PathVariable LocalDate toDate,  @RequestParam MultipartFile file) throws IOException{
			return classHourService.writeToExcelSheet(programId,fromDate,toDate,file);

		}
}
