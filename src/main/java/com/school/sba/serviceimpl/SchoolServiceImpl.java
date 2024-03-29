package com.school.sba.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.ScheduleRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.utility.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService {

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ResponseStructure<SchoolResponse> responseStructure;
	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private UserServiceImpl userServiceImpl;

	private School mapToSchoolRequest(SchoolRequest schoolRequest) {
		return School.builder().schoolName(schoolRequest.getSchoolName()).emailId(schoolRequest.getEmailId())
				.address(schoolRequest.getAddress()).contactNo(schoolRequest.getContactNo()).build();
	}

	private SchoolResponse mapToSchoolResponse(School school) {
		return SchoolResponse.builder().schoolId(school.getSchoolId()).schoolName(school.getSchoolName())
				.emailId(school.getEmailId()).address(school.getAddress()).contactNo(school.getContactNo()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> registerSchool(SchoolRequest schoolRequest) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();

		return userRepository.findByUserName(name).map(u -> {
			if (u.getRole().equals(UserRole.ADMIN)) {
				if (u.getSchool() == null) {
					School school = mapToSchoolRequest(schoolRequest);
					school = schoolRepository.save(school);
					SchoolResponse response = mapToSchoolResponse(school);
					u.setSchool(school);
					userRepository.save(u);
					responseStructure.setStatus(HttpStatus.CREATED.value());
					responseStructure.setMessage("School Created By ADMIN...!");
					responseStructure.setData(response);
					return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure, HttpStatus.CREATED);
				} else {
					throw new IllegalArgumentException("School already created By ADMIN");
				}
			} else {
				responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
				responseStructure.setMessage("Only ADMIN can create School...!");
				return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure, HttpStatus.BAD_REQUEST);
			}
		}).orElseThrow(() -> new UserNotFoundByIdException("Given UserId not found in the database"));
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> deleteSchoolById(int schoolId) {
		return schoolRepository.findById(schoolId).map(school -> {

			school.setIsDeleted(true);
			School save = schoolRepository.save(school);
			SchoolResponse response = mapToSchoolResponse(save);
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("Data Deleted Successfully...!");
			responseStructure.setData(response);
			return new ResponseEntity<ResponseStructure<SchoolResponse>>(responseStructure, HttpStatus.OK);

		}).orElseThrow(() -> new SchoolNotFoundByIdException("School not found by given ID"));

	}

	public void autoDeleteSchool() {
	    List<School> schools = schoolRepository.findByIsDeleted(true);

	    for (School school : schools) {
	    	List<User> users = userRepository.findBySchool(school);
	    	userRepository.deleteAll(users);
	        
	            List<AcademicProgram> academicPrograms = school.getAcademicPrograms(); 
	            for(AcademicProgram program:academicPrograms) {
	            	
	              classHourRepository.deleteAll(program.getClassHours());
	             
	            }
	            academicProgramRepository.deleteAll();
	            
	        }
	   
	    scheduleRepository.deleteAll();
	    }
	
	
//	public void autoDeleteSchool() {
//		List<School> schoolsToBeDeleted = schoolRepository.findByIsDeleted(true);
//
//		if(!schoolsToBeDeleted.isEmpty())
//		{
//			schoolsToBeDeleted.forEach(school->{
//
//				academicProgramRepository.deleteAll(school.getAcademicPrograms());
//				List<User> userList = userRepository.findByRoleNot(UserRole.ADMIN);
//				userRepository.deleteAll(userList);
//
//				userRepository.findByRole(UserRole.ADMIN).forEach(user->{
//
//					if(user.getSchool().getSchoolId()==school.getSchoolId())
//					{
//						user.setSchool(null);
//						userRepository.save(user);
//					}
//				});
//
//			});
//			schoolRepository.deleteAll();
//			System.out.println("School Deleted");
//		}
//		else
//			System.out.println("Nothing to be deleted");
//	}
	

	public boolean hasSoftDeletedData() {
		List<School> deletedSchools = schoolRepository.findByIsDeleted(true);
		for (School deletedSchool : deletedSchools) {
			schoolRepository.delete(deletedSchool);
		}
		return !deletedSchools.isEmpty();
	}

}
