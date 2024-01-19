package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utility.ResponseStructure;

	@Service
	public class SubjectServiceImpl implements SubjectService {

	    @Autowired
	    private AcademicProgramRepository academicProgramRepository;

	    @Autowired
	    private SubjectRepository subjectRepository;

	    @Autowired
	    private ResponseStructure<AcademicProgramResponse> responseStructure;

	    @Autowired
	    private AcademicProgramServiceImpl academicProgramServiceImpl;

	    @Override
	    public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(int programId,SubjectRequest subjectRequest) {
	          
	        return academicProgramRepository.findById(programId).map(program -> {
	            List<Subject> subjects = new ArrayList<Subject>();
	            subjectRequest.getSubjectsNames().forEach(name -> {
	                Subject subject = subjectRepository.findBySubjectName(name).map(s->s).orElseGet(() -> {
	                    Subject newSubject = new Subject();
	                    newSubject.setSubjectName(name);
	                    subjectRepository.save(newSubject);
	                    return newSubject;
	                });
	                subjects.add(subject);
	            });
	            program.setSubjects(subjects);
	            academicProgramRepository.save(program);

	            responseStructure.setStatus(HttpStatus.CREATED.value());
	            responseStructure.setMessage("Added subject list to Academic-Program");
	            responseStructure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(program));
	            return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,
	                    HttpStatus.CREATED);
	        }).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Given programId not present in the database"));
	    }
	}


	
	


