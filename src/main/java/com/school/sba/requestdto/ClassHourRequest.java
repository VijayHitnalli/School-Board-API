package com.school.sba.requestdto;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class ClassHourRequest {

	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	private int roomNo;
	private String classStatus;
	
}
