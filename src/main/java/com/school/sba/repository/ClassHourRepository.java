package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer>{
	boolean existsByBeginsAtBetweenAndRoomNo(LocalDateTime startsAt, LocalDateTime endsAt,int roomNo);
	public List<ClassHour> findByIsDeleted(boolean isDeleted);

}
