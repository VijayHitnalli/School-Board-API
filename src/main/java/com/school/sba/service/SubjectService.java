package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.utility.ResponseStructure;

public interface SubjectService {

	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(int programId,SubjectRequest subjectRequest);

//	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjects(int programId,
//			SubjectRequest subjectRequest);

	public ResponseEntity<ResponseStructure<SubjectResponse>> findAllSubjects();

	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToUser(int subjectId, int userId);
			

}
