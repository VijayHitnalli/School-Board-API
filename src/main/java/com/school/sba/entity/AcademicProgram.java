package com.school.sba.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.school.sba.enums.ProgramType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Component
public class AcademicProgram {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int programId;
	private String programName;
	@Enumerated(EnumType.STRING)
	private ProgramType programType;
	private LocalDate beginsAt;
	private LocalDate endsAt;
	private Boolean isDeleted;
	@ManyToOne
	private School school;
	@ManyToMany
	private List<Subject> subjects;
	@ManyToMany
	private List<User> users;
	@OneToMany(mappedBy = "academicProgram")
	private List<ClassHour> classHours;
	
}
