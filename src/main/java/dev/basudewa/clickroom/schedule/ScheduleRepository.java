package dev.basudewa.clickroom.schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, PagingAndSortingRepository<Schedule, Long> {
    public Page<Schedule> findScheduleByLendee(String lendee, Pageable pageable);
    public Schedule findScheduleByIdAndLendee(Long id, String lendee);
    public Schedule findScheduleById(Long id);

    @Query(value = "SELECT * FROM schedule WHERE " +
            "((start_time > :startTime AND start_time < :endTime) " +
            "OR (start_time < :startTime AND end_time > :endTime) " +
            "OR (end_time > :startTime AND end_time < :endTime) " +
            "OR (start_time = :startTime AND end_time = :endTime)" +
            "OR (start_time = :startTime)" +
            "OR (end_time = :endTime)) " +
            "AND borrow_date = :borrowDate " +
            "AND room_id = :roomId " +
            "AND id != :id", nativeQuery = true)
    public List<Schedule> findCollidingSchedule(Time startTime, Time endTime, Date borrowDate, Long roomId, Long id);

    @Query(value = "SELECT * FROM schedule WHERE " +
            "((start_time > :startTime AND start_time < :endTime) " +
            "OR (start_time < :startTime AND end_time > :endTime) " +
            "OR (end_time > :startTime AND end_time < :endTime) " +
            "OR (start_time = :startTime AND end_time = :endTime)" +
            "OR (start_time = :startTime)" +
            "OR (end_time = :endTime)) " +
            "AND borrow_date = :borrowDate " +
            "AND room_id = :roomId", nativeQuery = true)
    public List<Schedule> findCollidingSchedule(Time startTime, Time endTime, Date borrowDate, Long roomId);
}