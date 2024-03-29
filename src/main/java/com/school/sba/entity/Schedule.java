package com.school.sba.entity;

import java.time.Duration;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scheduleId;
	private LocalTime opensAt;
	private LocalTime closeAt;
	private int classHoursPerDay;
	private Duration classHourLengthInMinutes;
	private LocalTime breakTime;
	private Duration breakLengthInMinutes;
	private LocalTime lunchTime;
	private Duration lunchLengthInMinutes;
	private LocalTime classEndTime;
	private LocalTime breakEndTime;
	private LocalTime lunchEndTime;
		
}
