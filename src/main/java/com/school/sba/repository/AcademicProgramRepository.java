package com.school.sba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.User;

public interface AcademicProgramRepository extends JpaRepository<AcademicProgram, Integer>{
	public List<AcademicProgram> findByIsDeleted(boolean isDeleted);
	public List<AcademicProgram> findByisAutoRepeatSchedule(boolean isAutoRepeatSchedule);

}
