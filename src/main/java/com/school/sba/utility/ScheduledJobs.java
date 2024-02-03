package com.school.sba.utility;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.serviceimpl.AcademicProgramServiceImpl;
import com.school.sba.serviceimpl.ClassHourServiceImpl;
import com.school.sba.serviceimpl.SchoolServiceImpl;
import com.school.sba.serviceimpl.UserServiceImpl;

@Component
public class ScheduledJobs {
//	@Scheduled(fixedDelay = 1000l*60)
//	public void test() {
//		System.out.println("Scheduled job");
//	}
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	private AcademicProgramServiceImpl academicProgramServiceImpl;
	@Autowired
	private SchoolServiceImpl schoolServiceImpl;
	@Autowired
	private ClassHourServiceImpl classHourServiceImpl;
	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	
	@Scheduled(cron = "0 0 0 * * 1")
	public void autoDelete() {
		
		if(userServiceImpl.hasSoftDeletedData()) {
			userServiceImpl.autoDeleteUser();
			
		}
		else {
			System.out.println("No soft-deleted data found. Skipping auto deletion.");
		}
	}
	
	@Scheduled(cron = "0 0 0 * * 1")
	public void autoDeleteAcademicProgram() {
		
		if(academicProgramServiceImpl.hasSoftDeletedData()) {
			academicProgramServiceImpl.autoDeleteAcademicProgram();
		}
		else {
			System.out.println("No soft-deleted data found. Skipping auto deletion.");
		}
	}
	@Scheduled(cron = "0 0 0 * * 1")
	public void autoDeleteSchool() {
		
		if(schoolServiceImpl.hasSoftDeletedData()) {
		schoolServiceImpl.autoDeleteSchool();
		}
		else {
			System.out.println("No soft-deleted data found. Skipping auto deletion.");
		}
	}
	
	@Scheduled(cron = "0 0 0 * * 1")
	public void autoUpdateClassHour() {
		classHourServiceImpl.generateWeeklyClassHours();
		System.out.println("Updated");
	}
	
	
	
	
}
