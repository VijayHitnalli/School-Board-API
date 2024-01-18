package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.utility.ResponseStructure;

public interface SubjectService {

	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(int programId,SubjectRequest subjectRequest);
			

}
