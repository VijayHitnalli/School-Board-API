package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.utility.ResponseStructure;

public interface ScheduleService {


	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(ScheduleRequest scheduleRequest,int schoolId);

	public ResponseEntity<ResponseStructure<ScheduleResponse>> getSchedule(int schoolId);

	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId,ScheduleRequest scheduleRequest);
			
			

}
