package com.school.sba.serviceimpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundByIdException;
import com.school.sba.exception.ClassHourNotFoundException;
import com.school.sba.exception.DataAlreadyExistException;
import com.school.sba.exception.ScheduleNotFoundBySchoolIdException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateDTO;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.utility.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private AcademicProgramRepository academicProgramRepository;
	@Autowired
	private ClassHourRepository classHourRepository;
	@Autowired
	private ResponseStructure<ClassHourResponse> responseStructure;
	@Autowired
	private ResponseStructure<ClassHourResponse> responseStructure2;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SubjectRepository subjectRepository;

	private ClassHourResponse mapToResponse(ClassHour classHour) {
		return ClassHourResponse.builder().classHourId(classHour.getClassHourId()).beginsAt(classHour.getBeginsAt())
				.endsAt(classHour.getEndsAt()).roomNo(classHour.getRoomNo()).classStatus(classHour.getClassStatus())
				.build();
	}

	private ClassHour mapToNewClassHour(ClassHour existingClassHour) {
		return ClassHour.builder().user(existingClassHour.getUser())
				.academicProgram(existingClassHour.getAcademicProgram()).roomNo(existingClassHour.getRoomNo())
				.beginsAt(existingClassHour.getBeginsAt().plusDays(7)).endsAt(existingClassHour.getEndsAt().plusDays(7))
				.classStatus(existingClassHour.getClassStatus()).subject(existingClassHour.getSubject()).build();
	}

	private boolean isBreakTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthInMinutes().toMinutes());

		return (currentTime.toLocalTime().isAfter(breakTimeStart) && currentTime.toLocalTime().isBefore(breakTimeEnd));

	}

	private boolean isLunchTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime lunchTimeStart = schedule.getLunchTime();
		LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthInMinutes().toMinutes());

		return (currentTime.toLocalTime().isAfter(lunchTimeStart) && currentTime.toLocalTime().isBefore(lunchTimeEnd));

	}

	private ClassHour deleteClassHour(ClassHour classHour) {
		classHourRepository.delete(classHour);
		return classHour;
	}

	private LocalDateTime dateToDateTime(LocalDate date, LocalTime time) {
		return LocalDateTime.of(date, time);
	}

	@Override
	public ResponseEntity<ResponseStructure<ClassHourResponse>> addClassHourToAcademicProgram(int programId,
			ClassHourRequest request) {
		return academicProgramRepository.findById(programId).map(program -> {
			Schedule schedule = program.getSchool().getSchedule();

			if (schedule == null) {
				throw new ScheduleNotFoundBySchoolIdException("Failed to GENERATE Class Hour");
			}
			if (program.getClassHours() == null || program.getClassHours().isEmpty()) {
				List<ClassHour> perDayClasshour = new ArrayList<ClassHour>();
				LocalDate date = program.getBeginsAt();
				int end = 6;

				DayOfWeek dayOfWeek = date.getDayOfWeek();

				if (!dayOfWeek.equals(DayOfWeek.MONDAY))
					end = end + (7 - dayOfWeek.getValue());

				// for generating day
				for (int day = 1; day <= end; day++) {

					if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
						date = date.plusDays(1);

					LocalTime currentTime = schedule.getOpensAt();
					LocalDateTime lasthour = null;

					// for generating class hours per day
					for (int entry = 1; entry <= schedule.getClassHoursPerDay(); entry++) {
						ClassHour classhour = new ClassHour();

						if (currentTime.equals(schedule.getOpensAt())) { // first class hour of the day
							classhour.setBeginsAt(dateToDateTime(date, currentTime));
						} else if (currentTime.equals(schedule.getBreakTime())) { // after break time
							lasthour = lasthour.plus(schedule.getBreakLengthInMinutes());
							classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
						} else if (currentTime.equals(schedule.getLunchTime())) { // after lunch time
							lasthour = lasthour.plus(schedule.getLunchLengthInMinutes());
							classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
						} else { // rest class hours of that day
							classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
						}
						classhour.setEndsAt(classhour.getBeginsAt().plus(schedule.getClassHourLengthInMinutes()));
						classhour.setClassStatus(ClassStatus.NOTSCHEDULED);
						classhour.setAcademicProgram(program);

						perDayClasshour.add(classHourRepository.save(classhour));

						lasthour = perDayClasshour.get(entry - 1).getEndsAt();

						currentTime = lasthour.toLocalTime();

						if (currentTime.equals(schedule.getCloseAt())) // school closing time
							break;
					}
					date = date.plusDays(1);
				}
				program.setClassHours(perDayClasshour);
				academicProgramRepository.save(program);

				responseStructure.setStatus(HttpStatus.CREATED.value());
				responseStructure.setMessage("Classhour GENERATED for Program: " + program.getProgramName());
				responseStructure.setData(null);

				return new ResponseEntity<ResponseStructure<ClassHourResponse>>(responseStructure, HttpStatus.CREATED);
			} else
				throw new IllegalArgumentException("Classhours Already Generated for :: " + program.getProgramName()
						+ " of ID: " + program.getProgramId());

		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Failed to GENERATE Class Hour"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHours(
			List<ClassHourUpdateDTO> classHourRequestList) {

		List<ClassHourResponse> updatedClassHourResponses = new ArrayList<>();
		ResponseStructure<List<ClassHourResponse>> structure = new ResponseStructure<>();

		for (ClassHourUpdateDTO req : classHourRequestList) {
			User user = userRepository.findById(req.getUserID())
					.orElseThrow(() -> new UserNotFoundByIdException("User not found"));

			ClassHour classHour = classHourRepository.findById(req.getClassHourID())
					.orElseThrow(() -> new ClassHourNotFoundException("Class hour not present"));

			Subject subject = subjectRepository.findById(req.getSubjectId())
					.orElseThrow(() -> new SubjectNotFoundByIdException("Subject not found"));

			if (user.getRole().equals(UserRole.TEACHER) && user.getSubject().equals(subject)) {
				boolean isPresent = classHourRepository.existsByBeginsAtBetweenAndRoomNo(classHour.getBeginsAt(),
						classHour.getEndsAt(), req.getRoomNo());

				if (isPresent) {
					throw new DataAlreadyExistException("Classroom already assigned");
				} else {
					classHour.setSubject(subject);
					classHour.setUser(user);
					classHour.setRoomNo(req.getRoomNo());

					classHourRepository.save(classHour);

					updatedClassHourResponses.add(mapToResponse(classHour));

					structure.setStatus(HttpStatus.OK.value());
					structure.setMessage("Updated");
					structure.setData(updatedClassHourResponses);

					return new ResponseEntity<>(structure, HttpStatus.OK);
				}
			} else {
				throw new IllegalArgumentException("Unable to update teacher with the given subject");
			}
		}

		return null;
	}

	public void generateWeeklyClassHours() {

		List<AcademicProgram> programsToBeAutoRepeated = academicProgramRepository.findByisAutoRepeatSchedule(true);
		{

			if (!programsToBeAutoRepeated.isEmpty()) {
				programsToBeAutoRepeated.forEach(program -> {

					int n = program.getSchool().getSchedule().getClassHoursPerDay() * 6;
					// getting last week class hour
					List<ClassHour> lastWeekClassHours = classHourRepository.findLastNRecordsByAcademicProgram(program,
							n);

					if (!lastWeekClassHours.isEmpty()) {
						for (int i = lastWeekClassHours.size() - 1; i >= 0; i--) {
							ClassHour existClassHour = lastWeekClassHours.get(i);
							classHourRepository.save(mapToNewClassHour(existClassHour));

						}
						System.out.println("this week data generated as per last week data");
					}
					System.out.println("No Last week data present");
				});
				System.out.println("Schedule Successfully Auto Repeated for the Upcoming WEEK.");
			} else
				System.out.println("Auto Repeat Schedule : OFF");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> xlSheetGeneration(int programId, ExcelRequestDto excelRequestDto) throws IOException, IOException {
		String path = "C:\\Users\\vijay\\OneDrive\\Desktop\\Project\\demo";

		String filePath = path + "demo.xlsx";

		Optional<AcademicProgram> optional = academicProgramRepository.findById(programId);
		AcademicProgram program = optional.get();

		LocalDateTime from = excelRequestDto.getFromDate().atTime(LocalTime.MIDNIGHT);
		LocalDateTime to = excelRequestDto.getToDate().atTime(LocalTime.MIDNIGHT).plusDays(1);

		List<ClassHour> classHours = classHourRepository.findAllByAcademicProgramAndBeginsAtBetween(program, from, to);

		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();

		int rowNumber = 0;
		Row header = sheet.createRow(rowNumber);
		header.createCell(0).setCellValue("Date");
		header.createCell(1).setCellValue("Begins At");
		header.createCell(2).setCellValue("Ends At");
		header.createCell(3).setCellValue("Subject");
		header.createCell(1).setCellValue("Teacher");
		header.createCell(4).setCellValue("Room Number");

		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

		for (ClassHour classHour : classHours) {
			Row row = sheet.createRow(++rowNumber);
			row.createCell(0).setCellValue(dateFormatter.format(classHour.getBeginsAt()));
			row.createCell(1).setCellValue(timeFormatter.format(classHour.getBeginsAt()));
			row.createCell(2).setCellValue(timeFormatter.format(classHour.getEndsAt()));
			if(classHour.getSubject().getSubjectName()==null) {
				row.createCell(3).setCellValue("");
			}else {
				row.createCell(3).setCellValue(classHour.getSubject().getSubjectName());
			}
			if(classHour.getUser().getUserName()==null) {
				row.createCell(4).setCellValue("");

			}else {
				row.createCell(4).setCellValue(classHour.getUser().getUserName());
			}
			row.createCell(5).setCellValue(classHour.getRoomNo());
		}

		workbook.write(new FileOutputStream(filePath));
		
		ResponseStructure<String> responseStructure=new ResponseStructure<String>();
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMessage("Excel Sheet Created");
		
		return new ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.CREATED);
	}

}
