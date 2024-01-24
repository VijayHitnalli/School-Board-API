package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.AccessDeniedException;
import com.school.sba.exception.InvalidUserRoleException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utility.ResponseStructure;

	@Service
	public class SubjectServiceImpl implements SubjectService {

	    @Autowired
	    private AcademicProgramRepository academicProgramRepository;

	    @Autowired
	    private SubjectRepository subjectRepository;

	    @Autowired
	    private ResponseStructure<SubjectResponse> sResponseStructure;
	    
	    @Autowired
	    private ResponseStructure<AcademicProgramResponse> responseStructure;

	    @Autowired
	    private AcademicProgramServiceImpl academicProgramServiceImpl;
	    
	    @Autowired
	    private UserRepository userRepository;
	    
	    @Autowired
	    private ResponseStructure<UserResponse> structure;
	    
	    @Autowired
	    private UserServiceImpl userServiceImpl;

 private SubjectResponse mapToSubjectResponse(Subject subject) {
	

	 return SubjectResponse.builder()
			 .subjectId(subject.getSubjectId())
			 .subjectName(subject.getSubjectName())
			 .build();
 }
	   
		@Override
		public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(int programId,SubjectRequest subjectRequest) {
				return academicProgramRepository.findById(programId).map(program->{
					List<Subject> subjects=(program.getSubjects()!=null)?program.getSubjects():new ArrayList<Subject>();
					
					//to add new subjects that are specified by the client
					subjectRequest.getSubjectsNames().forEach(name->{
						boolean isPresent=false;
						for(Subject subject:subjects) {
							isPresent=(name.equalsIgnoreCase(subject.getSubjectName()))? true:false;
							if(isPresent)break;
						}
						if(!isPresent)subjects.add(subjectRepository.findBySubjectName(name).orElseGet(()->subjectRepository
								.save(Subject.builder().subjectName(name).build())));
					});
					//to remove that subject are not specify by the client
					List<Subject> toRemoved=new ArrayList<Subject>();
					subjects.forEach(subject->{
						boolean isPresent=false;
						for(String name:subjectRequest.getSubjectsNames()) {
							isPresent=(subject.getSubjectName().equalsIgnoreCase(name))?true:false;
							if(!isPresent)break;
						}
						if(!isPresent)toRemoved.add(subject);
					});
					subjects.removeAll(toRemoved);
					
					program.setSubjects(subjects);//set subject list to the acadeic program
					academicProgramRepository.save(program);//saving u[pdated program top the database
					responseStructure.setStatus(HttpStatus.CREATED.value());
					responseStructure.setMessage("Updated the subject list to the database");
					responseStructure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(program));
					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,HttpStatus.CREATED);
				}).orElseThrow(()->new AcademicProgramNotFoundByIdException("Given program id not present in the database"));
			
		}
	    


	 
//
//		@Override
//		public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjects(int programId,SubjectRequest subjectRequest) {
//				return academicProgramRepository.findById(programId).map(program->{
//					List<Subject> existingSubjects=program.getSubjects();
//					
//					List<Subject> updatedSubjects=new ArrayList<Subject>();
//					
//					subjectRequest.getSubjectsNames().forEach(name->{
//						Subject subject = subjectRepository.findBySubjectName(name).map(s->s).orElseGet(()->{
//							Subject newSubject=new Subject();
//							newSubject.setSubjectName(name);
//							subjectRepository.save(newSubject);
//							return newSubject;
//						});
//						if(!existingSubjects.contains(subject)) {
//							updatedSubjects.add(subject);
//						}	
//					});
//					updatedSubjects.addAll(existingSubjects);
//					program.setSubjects(updatedSubjects);
//					academicProgramRepository.save(program);
//					responseStructure.setStatus(HttpStatus.OK.value());
//					responseStructure.setMessage("Updated Subject list for Academic-Program");
//					responseStructure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(program));
//					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,HttpStatus.OK);
//				}).orElseThrow(()-> new AcademicProgramNotFoundByIdException("given programId not found in the dadabase"));
//			
//			
//		}


		
		@Override
		public ResponseEntity<ResponseStructure<SubjectResponse>> findAllSubjects() {
		    List<Subject> allSubjects = subjectRepository.findAll();

		    List<SubjectResponse> subjectResponses = allSubjects.stream()
		            .map(subject -> mapToSubjectResponse(subject))
		            .collect(Collectors.toList());
		    

		    sResponseStructure.setStatus(HttpStatus.OK.value());
		    sResponseStructure.setMessage("Fetched all subjects from the database");
//		    sResponseStructure.setData(mapToSubjectResponse(subject));

		    return new ResponseEntity<ResponseStructure<SubjectResponse>>(sResponseStructure, HttpStatus.OK);
		}
		
		
		@Override
		public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToUser(int subjectId, int userId) {
			return userRepository.findById(userId).map(user->{
				if(user.getRole()!=UserRole.ADMIN&& user.getRole()!=UserRole.STUDENT) {
					Subject subject= subjectRepository.findById(subjectId).orElseThrow(()-> new SubjectNotFoundByIdException("given subjectId not found in the dcatabase"));
					if(!subject.getUsers().contains(user)) {
						subject.getUsers().add(user);
						userRepository.save(user);
						
						structure.setStatus(HttpStatus.CREATED.value());
						structure.setMessage("Subject added to TEACHER");
						structure.setData(userServiceImpl.mapToUserResponse(user));
						return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);
					}
				}
				throw new InvalidUserRoleException("Admins are not allowed to be added to Subjects");
			}).orElseThrow(()->new UserNotFoundByIdException("Given userId not in the database"));
			
		}
		
		


	
	}


	
	


