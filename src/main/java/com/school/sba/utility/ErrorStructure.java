package com.school.sba.utility;

import org.springframework.stereotype.Component;

@Component
public class ErrorStructure<T> {
	
		private int status;
		private String messege;
		private T rootCause;
		
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getMessege() {
			return messege;
		}
		public void setMessege(String messege) {
			this.messege = messege;
		}
		public T getRootCause() {
			return rootCause;
		}
		public void setRootCause(T rootCause) {
			this.rootCause = rootCause;
		}
		
		
}
