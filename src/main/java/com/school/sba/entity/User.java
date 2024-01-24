package com.school.sba.entity;

import java.util.List;

import com.school.sba.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private int userID;
		@Column(unique = true)
		private String userName;
		private String firstName;
		private String lastName;
		private long contactNo;
		@Column(unique = true)
		private String email;
		private String password;
		private UserRole role;
		private Boolean isDeleted=false;
		@ManyToOne
		private School school;
		@ManyToMany(mappedBy = "users")
		private List<AcademicProgram> academicPrograms;
		@ManyToOne
		private Subject subject;
}
