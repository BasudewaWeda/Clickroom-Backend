package dev.basudewa.clickroom.controller;

import dev.basudewa.clickroom.entity.Schedule;
import dev.basudewa.clickroom.repository.ScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleRepository scheduleRepository;

    public ScheduleController(ScheduleRepository scheduleRepository) { this.scheduleRepository = scheduleRepository; }

    @GetMapping("/room/{requestedId}")
    public ResponseEntity<List<Schedule>> findScheduleByRoomId(@PathVariable Long requestedId, Pageable pageable) {
        Page<Schedule> page = scheduleRepository.findScheduleByRoomId(requestedId,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "borrow_date"))
                )
        );

        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> findScheduleByLendee(Principal principal, Pageable pageable) {
        Page<Schedule> page = scheduleRepository.findScheduleByLendee(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.DESC, "borrow_date"))
                )
        );

        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Schedule> findScheduleById(@PathVariable Long requestedId, Principal principal) {
        Schedule requestedSchedule = scheduleRepository.findScheduleByIdAndLendee(requestedId, principal.getName());
        if(requestedSchedule != null) {
            return ResponseEntity.ok(requestedSchedule);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> createSchedule(@RequestBody Schedule newSchedule, Principal principal, UriComponentsBuilder ucb) {
        List<Schedule> collidingSchedules = scheduleRepository.findCollidingSchedule(newSchedule.startTime(), newSchedule.endTime(), newSchedule.borrowDate(), newSchedule.roomId());
        if(collidingSchedules.size() == 0) {
            Schedule modifiedSchedule = new Schedule(
                    null,
                    newSchedule.borrowDate(),
                    newSchedule.startTime(),
                    newSchedule.endTime(),
                    principal.getName(),
                    principal.getName(),
                    newSchedule.roomId()
            );

            Schedule savedSchedule = scheduleRepository.save(modifiedSchedule);
            URI locationOfNewSchedule = ucb
                    .path("/schedule/{id}")
                    .buildAndExpand(savedSchedule.id())
                    .toUri();

            return ResponseEntity.created(locationOfNewSchedule).build();
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long requestedId, Principal principal) {
        Schedule requestedSchedule = scheduleRepository.findScheduleByIdAndLendee(requestedId, principal.getName());
        if(requestedSchedule != null) {
            scheduleRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/admin/{requestedId}")
    public ResponseEntity<Void> deleteScheduleAdmin(@PathVariable Long requestedId) {
        Schedule requestedSchedule = scheduleRepository.findScheduleById(requestedId);
        if(requestedSchedule != null) {
            scheduleRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
