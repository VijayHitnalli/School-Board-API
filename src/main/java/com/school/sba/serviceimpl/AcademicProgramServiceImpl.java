package com.school.sba.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.utility.ResponseStructure;
@Service
public class AcademicProgramServiceImpl implements AcademicProgramService{
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	
	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;
	
	private AcademicProgram mapToAcademicProgramRequest(AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder()
				.beginsAt(academicProgramRequest.getBeginsAt())
				.endsAt(academicProgramRequest.getEndsAt())
				.programName(academicProgramRequest.getProgramName())
				.programType(academicProgramRequest.getProgramType())
				.build();
	}
	
	private AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academicProgram) {
		return AcademicProgramResponse.builder()
				.programId(academicProgram.getProgramId())
				.programName(academicProgram.getProgramName())
				.programType(academicProgram.getProgramType())
				.beginsAt(academicProgram.getBeginsAt())
				.endsAt(academicProgram.getEndsAt())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(int schoolId,AcademicProgramRequest academicProgramRequest) {
			return schoolRepository.findById(schoolId).map(a->{
				AcademicProgram academicProgram = mapToAcademicProgramRequest(academicProgramRequest);
				academicProgram=academicProgramRepository.save(academicProgram);
				a.getAcademicPrograms().add(academicProgram);
				AcademicProgramResponse response = mapToAcademicProgramResponse(academicProgram);
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("Academic-Program Created Successfully...");
				responseStructure.setData(response);
				return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,HttpStatus.CREATED);
			}).orElseThrow(()->new SchoolNotFoundByIdException("Given SchoolId not present in the database"));
		
	}




}
