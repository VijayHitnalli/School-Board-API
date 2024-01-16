package com.school.sba.exception;

public class SchoolNotFoundByIdException extends RuntimeException{
		
	private String messege;

	
	public SchoolNotFoundByIdException(String messege) {
		super();
		this.messege = messege;
	}

	@Override
	public String getMessage() {
		return messege;
	}
	
}
