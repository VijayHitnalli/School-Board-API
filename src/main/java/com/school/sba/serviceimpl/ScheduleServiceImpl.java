package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.school.sba.repository.ScheduleRepository;
import com.school.sba.repository.UserRepository;
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
}
