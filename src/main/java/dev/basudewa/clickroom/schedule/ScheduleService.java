package dev.basudewa.clickroom.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public boolean scheduleExists(Long requestedId, String lendee) {
        return scheduleRepository.findScheduleByIdAndLendee(requestedId, lendee) != null;
    }

    public boolean scheduleExists(Long requestedId) {
        return scheduleRepository.findScheduleById(requestedId) != null;
    }

    public boolean isNotCollidingWithOtherSchedule(Schedule newSchedule) {
        List<Schedule> collidingSchedules = scheduleRepository.findCollidingSchedule(newSchedule.getStartTime(), newSchedule.getEndTime(), newSchedule.getBorrowDate(), newSchedule.getRoom().getId());
        return collidingSchedules.size() == 0;
    }

    public Page<Schedule> getScheduleByLendee(Principal principal, Pageable pageable) {
        return scheduleRepository.findScheduleByLendee(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "borrowDate"))
                )
        );
    }

    public Schedule getScheduleByIdAndLendee(Long requestedId, String lendee) {
        return scheduleRepository.findScheduleByIdAndLendee(requestedId, lendee);
    }

    public Schedule getScheduleById(Long requestedId) {
        return scheduleRepository.findScheduleById(requestedId);
    }

    public URI createScheduleByAdmin(Schedule newSchedule, UriComponentsBuilder ucb, Principal principal) {
        Schedule modifiedSchedule = new Schedule(
                null,
                newSchedule.getBorrowDate(),
                newSchedule.getStartTime(),
                newSchedule.getEndTime(),
                principal.getName(),
                principal.getName(),
                newSchedule.getBorrowDetail(),
                newSchedule.getRoom()
        );
        Schedule savedSchedule = scheduleRepository.save(modifiedSchedule);

        return ucb
                .path("/schedule/{id}")
                .buildAndExpand(savedSchedule.getId())
                .toUri();
    }

    public void updateSchedule(Long requestedId, Schedule scheduleUpdate, Schedule oldSchedule) {
        Schedule modifiedSchedule = new Schedule(
                requestedId,
                (scheduleUpdate.getBorrowDate() != null) ? scheduleUpdate.getBorrowDate() : oldSchedule.getBorrowDate(),
                (scheduleUpdate.getStartTime() != null) ? scheduleUpdate.getStartTime() : oldSchedule.getStartTime(),
                (scheduleUpdate.getEndTime() != null) ? scheduleUpdate.getEndTime() : oldSchedule.getEndTime(),
                (scheduleUpdate.getLendee() != null) ? scheduleUpdate.getLendee() : oldSchedule.getLendee(),
                oldSchedule.getLender(),
                (scheduleUpdate.getBorrowDetail() != null) ? scheduleUpdate.getBorrowDetail() : oldSchedule.getBorrowDetail(),
                (scheduleUpdate.getRoom() != null) ? scheduleUpdate.getRoom() : oldSchedule.getRoom()
                );

        scheduleRepository.save(modifiedSchedule);
    }

    public void deleteSchedule(Long requestedId) {
        scheduleRepository.deleteById(requestedId);
    }
}
