package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.utility.ResponseStructure;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class ScheduleController {
		
		@Autowired
		private ScheduleService scheduleService;
		
		@PreAuthorize("hasAuthority('ADMIN')")
		@PostMapping("/schools/{schoolId}/schedules")
		public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(@PathVariable int schoolId, @RequestBody ScheduleRequest scheduleRequest){
			return scheduleService.createSchedule(scheduleRequest,schoolId);
		}
		@GetMapping("/schools/{schoolId}/schedules")
		public ResponseEntity<ResponseStructure<ScheduleResponse>> getSchedule(@PathVariable int schoolId){
			return scheduleService.getSchedule(schoolId);
		}
		@PreAuthorize("hasAuthority('ADMIN')")
		@PutMapping("/schedules/{scheduleId}")
		public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(@PathVariable int scheduleId,@RequestBody ScheduleRequest scheduleRequest){
			return scheduleService.updateSchedule(scheduleId,scheduleRequest);
		}
}
