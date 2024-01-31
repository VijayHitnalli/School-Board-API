package com.school.sba.responsedto;

import java.time.LocalDate;
import java.util.List;

import com.school.sba.enums.ProgramType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicProgramResponse {
	private int programId;
	private String programName;
	private ProgramType programType;
	private LocalDate beginsAt;
	private LocalDate endsAt;
	private boolean isDeleted;
	private List<String> subjects;
}
