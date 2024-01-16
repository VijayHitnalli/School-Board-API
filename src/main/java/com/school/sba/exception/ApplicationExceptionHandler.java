package com.school.sba.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.utility.ErrorStructure;

@RestController
public class ApplicationExceptionHandler {
		
		@Autowired
		private ErrorStructure<String> errorStructure;
		
		@ExceptionHandler(SchoolNotFoundByIdException.class)
		public ResponseEntity<ErrorStructure<String>> schoolNotFoundById(SchoolNotFoundByIdException ex){
			errorStructure.setStatus(HttpStatus.NOT_FOUND.value());
			errorStructure.setMessege(ex.getMessage());
			errorStructure.setRootCause("The Request user given with Id not found...!");
			return new ResponseEntity<ErrorStructure<String>>(errorStructure,HttpStatus.NOT_FOUND);
		}
}
