package com.school.sba.utility;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorStructure<T> {
	
		private int status;
		private String messege;
		private T rootCause;
		
	
		
		
}
