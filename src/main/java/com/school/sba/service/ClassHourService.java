package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateDTO;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.utility.ResponseStructure;


public interface ClassHourService {

	ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHourToAcademicProgram(int programId,
			ClassHourRequest classHourRequest);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHours(List<ClassHourUpdateDTO> classHourRequests);

}
