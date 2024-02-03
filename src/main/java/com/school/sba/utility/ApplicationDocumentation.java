package com.school.sba.utility;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
@Configuration
@OpenAPIDefinition
public class ApplicationDocumentation {	
			Info info() {
				return new Info().title("User-Management-System-API")
						.version("1.0v")
						.description("School-Board is RESTful API Bulit using"
						+"Spring-Boot and MySQL database")
						.contact(contact());
			}
			@Bean
			OpenAPI openApi() {
				return new OpenAPI().info(info());
			}
			
			 private Contact contact() {
			        return new Contact()
			                .name("Vijay")
			                .url("https://www.linkedin.com/in/vijayhitnalli ")
			                .email("vijayHitnalli35@gmail.com");
			                
			    }
}
