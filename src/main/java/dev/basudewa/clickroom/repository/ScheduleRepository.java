package dev.basudewa.clickroom.repository;

import dev.basudewa.clickroom.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule, Long>, PagingAndSortingRepository<Schedule, Long> {
    public Page<Schedule> findScheduleByRoomId(Long roomId, Pageable pageable);
    public Page<Schedule> findScheduleByLendee(String lendee, Pageable pageable);
    public Schedule findScheduleByIdAndLendee(Long id, String lendee);
    public Schedule findScheduleById(Long id);

    @Query("SELECT * FROM schedule WHERE " +
            "((start_time > :startTime AND start_time < :endTime) " +
            "OR (start_time < :startTime AND end_time > :endTime) " +
            "OR (end_time > :startTime AND end_time < :endTime) " +
            "OR (start_time = :startTime AND end_time = :endTime)) " +
            "AND borrow_date = :borrowDate " +
            "AND room_id = :roomId")
    public List<Schedule> findCollidingSchedule(String startTime, String endTime, String borrowDate, Long roomId);
}
