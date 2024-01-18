package com.school.sba.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int subjectId;
	private List<String> subjects;
	@ManyToMany(mappedBy = "subjects")
	private List<AcademicProgram> academicPrograms;
	
}
