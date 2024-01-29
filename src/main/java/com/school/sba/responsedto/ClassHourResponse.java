package com.school.sba.responsedto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;

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
public class ClassHourResponse {
	private int classHourId;
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	private int roomNo;
	private ClassStatus classStatus;
}
