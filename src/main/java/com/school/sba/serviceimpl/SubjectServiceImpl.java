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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.AccessDeniedException;
import com.school.sba.exception.DataNotFoundException;
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
		private AcademicProgramRepository programRepo;
		@Autowired
		private SubjectRepository subjectRepo;
		
		@Autowired
		private AcademicProgramServiceImpl academicProgramServiceImpl;
		
		@Autowired
		private ResponseStructure<AcademicProgramResponse> structure;
		
		private SubjectResponse mapToSubjectResponse(Subject subject) {
			return new SubjectResponse().builder()
					.subjectId(subject.getSubjectId())
					.subjectName(subject.getSubjectName())
					.build();
		}
		
		@Override
		public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubjects(int programId,
				SubjectRequest subjectRequest) {
		
			return programRepo.findById(programId).map(program -> {  // found academic program
				List<Subject> subjects=new ArrayList<Subject>();
				subjectRequest.getSubjectsNames().forEach(name -> {    // iterating over each subject name
					
					Subject subject=subjectRepo.findBySubjectName(name).map(s -> s).orElseGet(()->{ // is not found
						Subject subject2=new Subject();
						subject2.setSubjectName(name);
						subjectRepo.save(subject2);
						
						return subject2;
					});
					subjects.add(subject);
				
			});
				program.setSubjects(subjects);  // set subjects list to the Academic Program
				programRepo.save(program);  // saving updated program to the database
				
				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMessage("Update the subject list to Academic Program");
				structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(program));
				
				return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,HttpStatus.CREATED);
			
			}).orElseThrow(()->new AcademicProgramNotFoundByIdException("Program not present for given id"));
			}       



		@Override
		public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjects(int programId,
				SubjectRequest subjectRequest)
		{
			    Optional<AcademicProgram> optional = programRepo.findById(programId);

			    if (optional.isPresent()) {
			        AcademicProgram program = optional.get();

			        List<Subject> existingSubjects = subjectRepo.findAll();
			        List<String> oldSubjectNames = new ArrayList<>();

			        // Populate oldSubjectNames with existing subject names
			        for (Subject s : existingSubjects) {
			            oldSubjectNames.add(s.getSubjectName());
			        }

			        List<String> newSubjectNames = subjectRequest.getSubjectsNames();

			        // Add new subjects
			        for (String sub : newSubjectNames) {
			            if (!oldSubjectNames.contains(sub)) {
			                Subject newSubject = new Subject();
			                newSubject.setSubjectName(sub);
			                subjectRepo.save(newSubject);
			                existingSubjects.add(newSubject);
			            }
			        }

			        // Update existing subjects
//			        for (String sub : newSubjectNames) {
//			            if (oldSubjectNames.contains(sub)) {
//			                int index = oldSubjectNames.indexOf(sub);
////			                Subject existingSubject = existingSubjects.get(index);
//			                
//			                oldSubjectNames.set(index, sub);
//			                
//			                // Perform any additional updates if needed
//			            }
//			        }

			        // Remove subjects not present in the new list
			        existingSubjects.removeIf(sub -> !newSubjectNames.contains(sub.getSubjectName()));

			        // Update the academic program with the new subjects
			        program.setSubjects(existingSubjects);
			        programRepo.save(program);
				
				structure.setStatus(HttpStatus.OK.value());
				structure.setMessage("Update the subject list to Academic Program");
				structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(optional.get()));
				
				return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,HttpStatus.OK);
				
			}
			
			else 
				throw new AcademicProgramNotFoundByIdException("program not present for given id for updating subjects");
			
		}		
		
		@Override
		public ResponseEntity<ResponseStructure<AcademicProgramResponse>> insertAndUpdateSubjectsIntoAcademicProgram(int academicProgramId,
				SubjectRequest subjectRequest) {
			
				return programRepo.findById(academicProgramId).map(academicProgram -> {
				List<Subject> subjects = (academicProgram.getSubjects()!=null) ? academicProgram.getSubjects() : new ArrayList<>();
				
				subjectRequest.getSubjectsNames().forEach(name -> {
					boolean isPresent = false;
					for(Subject subject : subjects) {
						isPresent = (name.equalsIgnoreCase(subject.getSubjectName()))? true : false;
						if(isPresent)break;
					}
					if(!isPresent) {
						subjects.add(subjectRepo.findBySubjectName(name)
								.orElseGet(() -> subjectRepo.save(Subject.builder().subjectName(name).build())));
					}
				});
				List<Subject> toBeRemoved = new ArrayList<>();
				subjects.forEach(subject -> {
					boolean isPresent = false;
					for(String name : subjectRequest.getSubjectsNames()) {
						isPresent = (subject.getSubjectName().equalsIgnoreCase(name))? true : false;
						if(!isPresent)break;
					}
					if(!isPresent)toBeRemoved.add(subject);
				});
				subjects.removeAll(toBeRemoved);

				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMessage("Updated the Subject list to Academic Program");
				structure.setData(academicProgramServiceImpl.mapToAcademicProgramResponse(academicProgram));

				return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,
						HttpStatus.CREATED);
			}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("AcademicProgram Data Not Found By Id"));
		}



		@Override
		public ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubjects() {
			
			List<Subject> subjects = subjectRepo.findAll();
			if(!subjects.isEmpty())
			{
				List<SubjectResponse> list=new ArrayList<>();
				
				for(Subject subject : subjects)
				{
					list.add(mapToSubjectResponse(subject));
				}
				
				ResponseStructure<List<SubjectResponse>> structure=new ResponseStructure<>();
				
				structure.setStatus(HttpStatus.FOUND.value());
				structure.setMessage("Subjects Found");
				structure.setData(list);
				
				return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(structure,HttpStatus.FOUND);
				
			}
			else 
				throw new DataNotFoundException("No Subjects Present");
				
		}


	
	}


	
	


