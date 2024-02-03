package com.school.sba.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateDTO;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.utility.ResponseStructure;


public interface ClassHourService {

	ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHourToAcademicProgram(int programId,
			ClassHourRequest classHourRequest);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHours(List<ClassHourUpdateDTO> classHourRequests);


	ResponseEntity<ResponseStructure<String>> xlSheetGeneration(int programId, ExcelRequestDto excelRequestDto) throws IOException, IOException;

	ResponseEntity<?> writeToExcelSheet(int programId, LocalDate fromDate, LocalDate toDate, MultipartFile file) throws IOException;

}
