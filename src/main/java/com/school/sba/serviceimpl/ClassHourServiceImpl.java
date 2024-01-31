package com.school.sba.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.ClassHourNotFoundException;
import com.school.sba.exception.DataAlreadyExistException;
import com.school.sba.exception.ScheduleNotFoundBySchoolIdException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateDTO;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.utility.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private ResponseStructure<List<ClassHourResponse>> responseStructure;
	@Autowired
	private ResponseStructure<ClassHourResponse> responseStructure2;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SubjectRepository subjectRepository;

	private ClassHourResponse mapToResponse(ClassHour classHour) {
		return ClassHourResponse.builder().classHourId(classHour.getClassHourId()).beginsAt(classHour.getBeginsAt())
				.endsAt(classHour.getEndsAt()).roomNo(classHour.getRoomNo()).classStatus(classHour.getClassStatus())
				.build();
	}
	
	private ClassHour deleteClassHour(ClassHour classHour) {
		classHourRepository.delete(classHour);
		return classHour;
	}

	private boolean isBreakTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());

		return (currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd));

	}
	

	private boolean isLunchTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime lunchTimeStart = schedule.getLunchTime();
		LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());

		return (currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd));

	}

	@Override
	public ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHourToAcademicProgram(int programId,
			ClassHourRequest classHourRequest) {
		return academicProgramRepository.findById(programId).map(program -> {
			School school = program.getSchool();
			Schedule schedule = school.getSchedule();
			if (schedule != null) {
				int classHoursPerDay = schedule.getClassHoursPerDay();
				int classHourLength = (int) schedule.getClassHourLengthInMinutes().toMinutes();
				LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());
				LocalTime lunchTimeStart = schedule.getLunchTime();
				LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());
				LocalTime breakTimeStart = schedule.getBreakTime();
				LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());

				for (int day = 1; day <= 6; day++) {
					for (int hour = 0; hour < classHoursPerDay + 2; hour++) {
						ClassHour classHour = new ClassHour();
						if (!currentTime.toLocalTime().equals(lunchTimeStart) && !isLunchTime(currentTime, schedule)) {
							if (!currentTime.toLocalTime().equals(breakTimeStart)
									&& !isBreakTime(currentTime, schedule)) {
								LocalDateTime beginsAt = currentTime;
								LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);

								classHour.setBeginsAt(beginsAt);
								classHour.setEndsAt(endsAt);
								classHour.setClassStatus(ClassStatus.NOTSCHEDULED);
								currentTime = endsAt;
							} else {
								classHour.setBeginsAt(currentTime);
								classHour.setEndsAt(LocalDateTime.now().with(breakTimeEnd));
								classHour.setClassStatus(ClassStatus.BREAKTIME);
								currentTime = currentTime.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());
							}
						} else {
							classHour.setBeginsAt(currentTime);
							classHour.setEndsAt(currentTime);
							classHour.setClassStatus(ClassStatus.LUNCHTIME);
							currentTime = currentTime.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());

						}
						classHour.setAcademicProgram(program);
						classHourRepository.save(classHour);

					}
					currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
				}

			} else {
				throw new ScheduleNotFoundBySchoolIdException(
						"The school does not contain any schedule, please provide a schedule to the school");
			}
			responseStructure2.setStatus(HttpStatus.CREATED.value());
			responseStructure2.setMessage("CREATED");
			return new ResponseEntity<ResponseStructure<ClassHourResponse>>(responseStructure2, HttpStatus.CREATED);

		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Invalid Program Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHours(List<ClassHourUpdateDTO> classhourequestlist) {
			


		List<ClassHourResponse> updatedClassHourResponses=new ArrayList<>();
		ResponseStructure<List<ClassHourResponse>> structure=new ResponseStructure<>();


		for(ClassHourUpdateDTO req : classhourequestlist)
		{
			return	userRepository.findById(req.getUserID()).map(user ->{

				return classHourRepository.findById(req.getClassHourID()).map(classHour->{

					return subjectRepository.findById(req.getSubjectId()).map(subject->{

						if(user.getRole().equals(UserRole.TEACHER) && user.getSubject().equals(subject))
						{

							boolean isPresent=classHourRepository.existsByBeginsAtBetweenAndRoomNo(classHour.getBeginsAt(), classHour.getEndsAt(), req.getRoomNo());

							if(isPresent)
							{
								throw new DataAlreadyExistException("class room already assigned");
							}

							else 
							{
								classHour.setSubject(subject);
								classHour.setUser(user);
								classHour.setRoomNo(req.getRoomNo());
								
								classHourRepository.save(classHour);

								updatedClassHourResponses.add(mapToResponse(classHour));
								
								
								structure.setStatus(HttpStatus.OK.value());
								structure.setMessage("Updated");
								structure.setData(updatedClassHourResponses);

								return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structure,HttpStatus.OK);
							}
						}

						else 
							throw new IllegalArgumentException("Unable to update teacher with given subject");


					}).orElseThrow(()->new SubjectNotFoundByIdException("subject not found "));

				}).orElseThrow(()->new ClassHourNotFoundException("Class houn not present"));

			}).orElseThrow(()->new UserNotFoundByIdException("user not found"));
		}
		return null;

	}



	

	
}



