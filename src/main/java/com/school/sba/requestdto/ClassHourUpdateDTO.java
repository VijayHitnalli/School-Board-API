package com.school.sba.requestdto;

import java.time.LocalDateTime;

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
public class ClassHourUpdateDTO {
	private int classHourID;
	private int userID;
	private int subjectId;
	private int roomNo;
}
