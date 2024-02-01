package com.school.sba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.sba.entity.School;
import com.school.sba.entity.User;


public interface SchoolRepository extends JpaRepository<School, Integer>{
	public List<School> findByIsDeleted(boolean isDeleted);

}
