package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.utility.ResponseStructure;

@RestController
public class ScheduleController {
		
		@Autowired
		private ScheduleService scheduleService;
		
		
}
