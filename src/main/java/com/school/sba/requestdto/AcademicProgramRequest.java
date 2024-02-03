package com.school.sba.requestdto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AcademicProgramRequest {
	private String programName;
	private ProgramType programType;
	@JsonFormat(pattern = "HH:mm:ss")
	private LocalDate beginsAt;
	@JsonFormat(pattern = "HH:mm:ss")
	private LocalDate endsAt;
}
