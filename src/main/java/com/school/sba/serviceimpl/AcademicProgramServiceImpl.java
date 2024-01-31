package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.DataNotFoundException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.utility.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService {

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AcademicProgram academicProgram;
	@Autowired
	private ClassHourRepository classHourRepository;

	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;

	private AcademicProgram mapToAcademicProgramRequest(AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder().beginsAt(academicProgramRequest.getBeginsAt())
				.endsAt(academicProgramRequest.getEndsAt()).programName(academicProgramRequest.getProgramName())
				.programType(academicProgramRequest.getProgramType()).build();
	}

	public AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academicProgram) {
		List<String> subjectNames = new ArrayList<String>();
		if (academicProgram.getSubjects() != null) {
			academicProgram.getSubjects().forEach(subject -> {
				subjectNames.add(subject.getSubjectName());
			});
		}
		return AcademicProgramResponse.builder().programId(academicProgram.getProgramId())
				.programType(academicProgram.getProgramType()).programName(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt()).endsAt(academicProgram.getEndsAt()).subjects(subjectNames)
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(int schoolId,
			AcademicProgramRequest academicProgramRequest) {
		return schoolRepository.findById(schoolId).map(school -> {
			AcademicProgram academicProgram = mapToAcademicProgramRequest(academicProgramRequest);
			academicProgram.setSchool(school);
			academicProgram = academicProgramRepository.save(academicProgram);
			school.getAcademicPrograms().add(academicProgram);
			schoolRepository.save(school);
			AcademicProgramResponse response = mapToAcademicProgramResponse(academicProgram);
			responseStructure.setStatus(HttpStatus.CREATED.value());
			responseStructure.setMessage("Academic-Program Created Successfully...");
			responseStructure.setData(response);
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,
					HttpStatus.CREATED);
		}).orElseThrow(() -> new SchoolNotFoundByIdException("Given SchoolId not present in the database"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicPrograms(int schoolId) {
		return schoolRepository.findById(schoolId).map(school -> {

			List<AcademicProgram> programs = academicProgramRepository.findAll();

			if (!programs.isEmpty()) {
				List<AcademicProgramResponse> list = new ArrayList<>();

				for (AcademicProgram program : programs) {
					AcademicProgramResponse response = mapToAcademicProgramResponse(program);
					list.add(response);
				}
				ResponseStructure<List<AcademicProgramResponse>> structure = new ResponseStructure<>();
				structure.setStatus(HttpStatus.FOUND.value());
				structure.setMessage("Here is the list Academic-Progrms of our SCHOOL");
				structure.setData(list);
				return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(structure,
						HttpStatus.FOUND);
			} else
				throw new DataNotFoundException("No Academic Programs present for given school");

		}).orElseThrow(() -> new SchoolNotFoundByIdException("School Not Present for given school id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deleteAcademicProgramById(int programId) {
		return academicProgramRepository.findById(programId).map(program->{
			program.setIsDeleted(true);
			AcademicProgram program2 = academicProgramRepository.save(program);
			AcademicProgramResponse response = mapToAcademicProgramResponse(program2);
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("Data Deleted Successfully...!");
			responseStructure.setData(response);
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure, HttpStatus.OK);
			
		}).orElseThrow(()-> new AcademicProgramNotFoundByIdException("Academic Program not found by given ID"));
		
	}
	
	public void autoDeleteAcademicProgram() {
		List<AcademicProgram> programs = academicProgramRepository.findByIsDeleted(true);
		for(AcademicProgram program:programs) {
			classHourRepository.deleteAll(academicProgram.getClassHours());
		}
			academicProgramRepository.deleteAll(programs);
		}
	
	public boolean hasSoftDeletedData() {
	    List<AcademicProgram> programs = academicProgramRepository.findByIsDeleted(true);
	    for (AcademicProgram deletedAcademicProgram : programs) {
	        academicProgramRepository.delete(deletedAcademicProgram);
	    }
	    return !programs.isEmpty();
	}
	

}
