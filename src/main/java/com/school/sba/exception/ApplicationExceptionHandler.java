package com.school.sba.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.utility.ErrorStructure;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler{
		
		
		
		private ResponseEntity<Object> structure(HttpStatus status,String messege,Object rootCause){
			return new ResponseEntity<Object> (Map.of(
					"status",status.value(),
					"messege",messege,
					"rootCause",rootCause
					),status);
		}
		@Override
		protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
				HttpHeaders headers, HttpStatusCode status, WebRequest request) {
			List<ObjectError> allErrors = ex.getAllErrors();
			Map<String, String> errors=new HashMap<String,String>();
			allErrors.forEach(error->{
				FieldError fieldError =(FieldError) error;
				errors.put(fieldError.getField(), fieldError.getDefaultMessage());
			});
			return structure(HttpStatus.BAD_REQUEST,"Failed To save the data...!", errors);
		}
				
		@ExceptionHandler(UserNotFoundByIdException.class)
		public ResponseEntity<Object> handleUserNotFoundById(UserNotFoundByIdException exception){
			return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "User Not found with given Id");
		}
		@ExceptionHandler(RuntimeException.class)
		public ResponseEntity<Object> handleRuntime(RuntimeException exception){
			return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Illegal Argument");
		}
		@ExceptionHandler(DataNotFoundException.class)
		public ResponseEntity<Object> handleRuntime(DataNotFoundException exception){
			return structure(HttpStatus.BAD_REQUEST, exception.getMessage(), "Illegal Argument");
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
		
		
}
