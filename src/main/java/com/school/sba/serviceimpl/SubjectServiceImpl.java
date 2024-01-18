package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utility.ResponseStructure;

@Service
public class SubjectServiceImpl implements SubjectService{

	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	
	@Autowired
	private SubjectRepository subjectRepository;

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(int programId,
			SubjectRequest subjectRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	}


