package com.school.sba.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.ClassStatus;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.ScheduleNotFoundBySchoolIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.utility.ResponseStructure;
@Service
public class ClassHourServiceImpl implements ClassHourService{
	
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private ResponseStructure<ClassHourResponse> responseStructure;
	
	private boolean isBreakTime(LocalDateTime currentTime , Schedule schedule)
	{
		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());
		
		return (currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd));

	}
	
	private boolean isLunchTime(LocalDateTime currentTime , Schedule schedule)
	{
		LocalTime lunchTimeStart = schedule.getLunchTime();
		LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());
		
		return (currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd));

	}
	
	
	@Override
	public ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHourToAcademicProgram(int programId,ClassHourRequest classHourRequest) {
		return academicProgramRepository.findById(programId).map(program->{
			School school = program.getSchool();
			Schedule schedule = school.getSchedule();
			if(schedule!=null) {
				int classHoursPerDay = schedule.getClassHoursPerDay();
				int classHourLength =(int) schedule.getClassHourLengthInMinutes().toMinutes();
				LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());
				LocalTime lunchTimeStart = schedule.getLunchTime();
				LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());
				LocalTime breakTimeStart = schedule.getBreakTime();
				LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());
				
				for(int day=1;day<=6;day++) {
					for(int hour=0;hour<classHoursPerDay+2;hour++) {
						ClassHour classHour=new ClassHour();
						if(!currentTime.toLocalTime().equals(lunchTimeStart)&& !isLunchTime(currentTime, schedule)) {
							if(!currentTime.toLocalTime().equals(breakTimeStart) && !isBreakTime(currentTime, schedule))
							{
								LocalDateTime beginsAt = currentTime;
								LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);
								
								classHour.setBeginsAt(beginsAt);
								classHour.setEndsAt(endsAt);
								classHour.setClassStatus(ClassStatus.NOTSCHEDULED);
								currentTime=endsAt;
							}else {
								classHour.setBeginsAt(currentTime);
								classHour.setEndsAt(LocalDateTime.now().with(breakTimeEnd));
								classHour.setClassStatus(ClassStatus.BREAKTIME);
								currentTime=currentTime.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());
							}
						}else {
							classHour.setBeginsAt(currentTime);
							classHour.setEndsAt(currentTime);
							classHour.setClassStatus(ClassStatus.LUNCHTIME);
							currentTime=currentTime.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());
							
						}
						classHour.setAcademicProgram(program);
						classHourRepository.save(classHour);
						
					}
					currentTime=currentTime.plusDays(1).with(schedule.getOpensAt());
				}
				
			}else {
				throw new ScheduleNotFoundBySchoolIdException("The school does not contain any schedule, please provide a schedule to the school");
			}
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("CREATED");
				return new ResponseEntity<ResponseStructure<ClassHourResponse>>(responseStructure,HttpStatus.CREATED);
				
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Invalid Program Id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ClassHourResponse>> updateClassHours(List<ClassHourRequest> classHourRequests) {
			
	return null;
	}

}
