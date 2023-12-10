package dev.basudewa.clickroom.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ScheduleListResponse> findScheduleByLendee(Principal principal, Pageable pageable) {
        ScheduleListResponse response = scheduleService.getScheduleByLendee(principal, pageable);

        if(response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Schedule> findScheduleById(@PathVariable Long requestedId, Principal principal) {
        Schedule requestedSchedule = scheduleService.getScheduleByIdAndLendee(requestedId, principal.getName());
        if(requestedSchedule != null) {
            return ResponseEntity.ok(requestedSchedule);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<URI> createSchedule(@RequestBody Schedule newSchedule, Principal principal, UriComponentsBuilder ucb) {
        if(scheduleService.isNotCollidingWithOtherSchedule(newSchedule)) {
            URI locationOfNewSchedule = scheduleService.createScheduleByAdmin(newSchedule, ucb, principal);
            return ResponseEntity.created(locationOfNewSchedule).body(locationOfNewSchedule);
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/admin/{requestedId}")
    public ResponseEntity<Void> updateSchedule(@PathVariable Long requestedId, @RequestBody Schedule scheduleUpdate, Principal principal) {
        Schedule targetedSchedule = scheduleService.getScheduleById(requestedId);
        if(targetedSchedule == null) {
            return ResponseEntity.notFound().build();
        }

        if(scheduleService.isNotCollidingWithOtherSchedule(scheduleUpdate)) {
            scheduleService.updateSchedule(requestedId, scheduleUpdate, targetedSchedule);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().build();
    }


    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long requestedId, Principal principal) {
        if(scheduleService.scheduleExists(requestedId, principal.getName())) {
            scheduleService.deleteSchedule(requestedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/admin/{requestedId}")
    public ResponseEntity<Void> deleteScheduleAdmin(@PathVariable Long requestedId) {
        if(scheduleService.scheduleExists(requestedId)) {
            scheduleService.deleteSchedule(requestedId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
