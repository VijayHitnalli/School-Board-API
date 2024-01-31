package com.school.sba.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Integer> {
	public boolean existsByRole(UserRole role);

	public Optional<User> findByUserName(String userName);

	public List<User> findByRoleAndAcademicPrograms(UserRole role, AcademicProgram academicProgram);

	public List<User> findByIsDeleted(boolean isDeleted);
}
