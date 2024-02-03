package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.utility.ResponseStructure;

public interface AcademicProgramService {

	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(int schoolId,
			AcademicProgramRequest academicProgramRequest);

	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicPrograms(int schoolId);

	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deleteAcademicProgramById(int programId);

	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> autoRepeatScheduleON(int programId,
			boolean autoRepeateScheduled);

	

}
