package com.school.sba.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.school.sba.enums.ClassStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassHour {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private int classHourId;
		private LocalDateTime beginsAt;
		private LocalDateTime endsAt;
		private int roomNo;
		private ClassStatus classStatus;
		
		@ManyToOne
		private AcademicProgram academicProgram;
		
		@ManyToOne
		private Schedule schedule;
		
		@ManyToOne
		private User user;
		
		@ManyToOne
		private Subject subject;
}
