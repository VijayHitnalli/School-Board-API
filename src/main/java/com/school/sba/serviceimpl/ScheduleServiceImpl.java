package com.school.sba.serviceimpl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.exception.DataAlreadyExistException;
import com.school.sba.exception.SchoolNotFoundByIdException;
import com.school.sba.repository.ScheduleRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.utility.ResponseStructure;

@Service

public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ResponseStructure<ScheduleResponse> responseStructure;

	@Autowired
	private SchoolRepository schoolRepository;

//	private Schedule mapToScheduleRequest(ScheduleRequest scheduleRequest) {
////		Duration fixedClassHourLength = Duration.ofMinutes(60);
////		Duration fixedBreakLength = Duration.ofMinutes(15);
////		Duration fixedLunchLength = Duration.ofMinutes(45);
//		return Schedule.builder().opensAt(scheduleRequest.getOpensAt()).closeAt(scheduleRequest.getCloseAt())
//				.classHoursPerDay(scheduleRequest.getClassHoursPerDay()).classHourLengthInMinutes(scheduleRequest.getClassHourLengthInMinutes())
//				.breakTime(scheduleRequest.getBreakTime()).breakLengthInMinutes(fixedBreakLength)
//				.lunchTime(scheduleRequest.getLunchTime()).lunchLengthInMinutes(fixedLunchLength).build();
//	}
	
	private Schedule mapToSchedule(ScheduleRequest scheduleRequest) {
		return Schedule.builder()
				.opensAt(scheduleRequest.getOpensAt())
				.closeAt(scheduleRequest.getCloseAt())
				.classHoursPerDay(scheduleRequest.getClassHoursPerDay())
				.classHourLengthInMinutes(Duration.ofMinutes(scheduleRequest.getClassHourLengthInMinutes()))
				.breakTime(scheduleRequest.getBreakTime())
				.breakLengthInMinutes(Duration.ofMinutes(scheduleRequest.getBreakLengthInMinutes()))
				.lunchTime(scheduleRequest.getLunchTime())
				.lunchLengthInMinutes(Duration.ofMinutes(scheduleRequest.getBreakLengthInMinutes()))
				.build();
	}

	private ScheduleResponse mapToScheduleResponse(Schedule schedule) {
		return ScheduleResponse.builder().scheduleId(schedule.getScheduleId()).opensAt(schedule.getOpensAt())
				.classHourLengthInMinutes((int) schedule.getClassHourLengthInMinutes().toMinutes())
				.closeAt(schedule.getCloseAt()).classHoursPerDay(schedule.getClassHoursPerDay())
				.breakTime(schedule.getBreakTime())
				.breakLengthInMinutes((int) schedule.getBreakLengthInMinutes().toMinutes())
				.lunchTime(schedule.getLunchTime())
				.lunchLengthInMinutes((int) schedule.getLunchLengthInMinutes().toMinutes()).build();
	}

	private Schedule deleteSchedule(Schedule schedule) {
		scheduleRepository.delete(schedule);
		return schedule;
	}

//	private Schedule createFixedSchedule(ScheduleRequest scheduleRequest) {
//		Schedule schedule = mapToScheduleRequest(scheduleRequest);
//		schedule.setClassEndTime(schedule.getOpensAt().plus(schedule.getClassHourLengthInMinutes()));
//		schedule.setBreakEndTime(schedule.getBreakTime().plus(schedule.getBreakLengthInMinutes()));
//		schedule.setLunchEndTime(schedule.getLunchTime().plus(schedule.getLunchLengthInMinutes()));
//
//		return schedule;
//	}
	
	
//	@Override
//	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(ScheduleRequest scheduleRequest, int schoolId) {
//	    return schoolRepository.findById(schoolId).map(school -> {
//
//	        if (school.getSchedule() == null) {
//	            Schedule schedule = mapToSchedule(scheduleRequest);
//
//	            // Add your validation logic here
//	            LocalTime opensAt = schedule.getOpensAt();
//	            LocalTime closeAt = schedule.getCloseAt();
//	            
//	            if (opensAt != null && closeAt != null) {
//	                long totalDuration = (schedule.getClassHoursPerDay() * schedule.getClassHourLengthInMinutes().toMinutes()) +
//	                        (schedule.getBreakLengthInMinutes().toMinutes() + schedule.getLunchLengthInMinutes().toMinutes());
//
//	                if (totalDuration == (schedule.getOpensAt().getMinute()+schedule.getCloseAt().getMinute())) {
//	                    // Validation successful, proceed with saving the schedule
//	                    schedule = scheduleRepository.save(schedule);
//	                    school.setSchedule(schedule);
//	                    schoolRepository.save(school);
//
//	                    ScheduleResponse scheduleResponse = mapToScheduleResponse(schedule);
//
//	                    responseStructure.setStatus(HttpStatus.CREATED.value());
//	                    responseStructure.setMessage("Schedule Created for School");
//	                    responseStructure.setData(scheduleResponse);
//
//	                    return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
//	                } else {
//	                    // Handle validation failure, you might want to throw an exception or handle it accordingly
//	                    throw new IllegalArgumentException("Invalid schedule duration");
//	                }
//	            } else {
//	                // Handle the case where opensAt or closeAt is null
//	                throw new IllegalArgumentException("opensAt or closeAt cannot be null");
//	            }
//	        } else {
//	            // If the school already has a schedule, throw an exception
//	            throw new IllegalArgumentException("Schedule of a school already exists");
//	        }
//
//	    }).orElseThrow(() -> new SchoolNotFoundByIdException("School Not Present for the given school id"));
//	}
	
	
	
	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(ScheduleRequest scheduleRequest,int schoolId) 
	{
		return schoolRepository.findById(schoolId).map(school->{
			
			if(school.getSchedule()==null)
			{
				Schedule schedule = mapToSchedule(scheduleRequest);
				System.out.println(scheduleRequest.getBreakTime());
				System.out.println(schedule.getBreakEndTime());
				schedule = scheduleRepository.save(schedule);
				school.setSchedule(schedule);
				schoolRepository.save(school);
				
				ScheduleResponse scheduleResponse = mapToScheduleResponse(schedule);
				
				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("Schedule Created for School");
				responseStructure.setData(scheduleResponse);
				
				return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure,HttpStatus.CREATED);
			}
			else 
				throw new DataAlreadyExistException("Schedule of a school already exist");
			
		}).orElseThrow(()->new SchoolNotFoundByIdException("School Not Present for given school id"));
		
	}


	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> getSchedule(int schoolId) {
		return schoolRepository.findById(schoolId).map(school -> {

			if (school.getSchedule() != null) {
				responseStructure.setStatus(HttpStatus.FOUND.value());
				responseStructure.setMessage("Schedule data found for given school");
				responseStructure.setData(mapToScheduleResponse(school.getSchedule()));

				return new ResponseEntity<ResponseStructure<ScheduleResponse>>(responseStructure, HttpStatus.FOUND);
			} else {
				throw new IllegalArgumentException("Schedule not created for given school");
			}

		}).orElseThrow(() -> new SchoolNotFoundByIdException("School not present for given school id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId,
			ScheduleRequest scheduleRequest) {
		return scheduleRepository.findById(scheduleId).map(schedule -> {
			schedule.setOpensAt(scheduleRequest.getOpensAt());
			schedule.setCloseAt(scheduleRequest.getCloseAt());
			schedule.setClassHoursPerDay(scheduleRequest.getClassHoursPerDay());
			schedule.setClassHourLengthInMinutes(Duration.ofMinutes(scheduleRequest.getClassHourLengthInMinutes()));
			schedule.setBreakTime(scheduleRequest.getBreakTime());
			schedule.setBreakLengthInMinutes(Duration.ofMinutes(scheduleRequest.getBreakLengthInMinutes()));
			schedule.setLunchTime(scheduleRequest.getLunchTime());
			schedule.setLunchLengthInMinutes(Duration.ofMinutes(scheduleRequest.getLunchLengthInMinutes()));
			scheduleRepository.save(schedule);
			ScheduleResponse response = mapToScheduleResponse(schedule);
			responseStructure.setStatus(HttpStatus.OK.value());
			responseStructure.setMessage("Schedule updated successfully");
			responseStructure.setData(response);
			return new ResponseEntity<>(responseStructure, HttpStatus.OK);
		}).orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + scheduleId));
	}



}
