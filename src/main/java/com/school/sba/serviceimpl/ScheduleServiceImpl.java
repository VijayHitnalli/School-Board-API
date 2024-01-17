package com.school.sba.serviceimpl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.ScheduleRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.utility.ResponseStructure;
@Service
public class ScheduleServiceImpl implements ScheduleService{
	@Autowired
	private ScheduleRepository scheduleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ResponseStructure<ScheduleResponse> responseStructure;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	
	
	private Schedule mapToScheduleRequest(ScheduleRequest scheduleRequest) {
	    return Schedule.builder()
	            .opensAt(scheduleRequest.getOpensAt())
	            .closeAt(scheduleRequest.getCloseAt())
	            .classHoursPerDay(scheduleRequest.getClassHoursPerDay())
	            .classHourLengthInMinutes(Duration.ofMinutes(scheduleRequest.getClassHourLengthInMinutes()))
	            .breakTime(scheduleRequest.getBreakTime())
	            .breakLengthInMinutes(Duration.ofMinutes(scheduleRequest.getBreakLengthInMinutes()))
	            .lunchTime(scheduleRequest.getLunchTime())
	            .lunchLengthInMinutes(Duration.ofMinutes(scheduleRequest.getLunchLengthInMinutes()))
	            .build();
	}

	private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
	    return ScheduleResponse.builder()
	            .scheduleId(schedule.getScheduleId())
	            .opensAt(schedule.getOpensAt())
	            .closeAt(schedule.getCloseAt())
	            .classHoursPerDay(schedule.getClassHoursPerDay())
	            .classHourLengthInMinutes(schedule.getClassHourLengthInMinutes().toMinutesPart())
	            .breakTime(schedule.getBreakTime())
	            .breakLengthInMinutes(schedule.getBreakLengthInMinutes().toMinutesPart())
	            .lunchTime(schedule.getLunchTime())
	            .lunchLengthInMinutes(schedule.getLunchLengthInMinutes().toMinutesPart())
	            .build();
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(ScheduleRequest scheduleRequest,int schoolId) {
		return	schoolRepository.findById(schoolId).map(s->{
			if(s.getSchedule()==null) {
				Schedule schedule = mapToScheduleRequest(scheduleRequest);
				schedule=scheduleRepository.save(schedule);
				s.setSchedule(schedule);
				schoolRepository.save(s);
				ScheduleResponse response = mapToScheduleResponse(schedule);
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("Schedule created to School");
				responseStructure.setData(response);
				return  new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure,HttpStatus.CREATED);
			} else {
				responseStructure.setStatus(HttpStatus.BAD_REQUEST.value());
				responseStructure.setMessage("Schedule already created to School");
				return  new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure,HttpStatus.BAD_REQUEST);
				}
			}).orElseThrow(()->new SchoolNotFoundByIdException("School Id not present in the database"));
			
		
	}
	

}
