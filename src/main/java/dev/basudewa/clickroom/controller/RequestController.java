package dev.basudewa.clickroom.controller;

import dev.basudewa.clickroom.repository.RequestRepository;
import dev.basudewa.clickroom.repository.ScheduleRepository;
import dev.basudewa.clickroom.entity.Request;
import dev.basudewa.clickroom.entity.Schedule;
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
@RequestMapping("/request")
public class RequestController {
    private RequestRepository requestRepository;
    private ScheduleRepository scheduleRepository;

    private boolean isNotCollidingWithOtherSchedule(Request newRequest) {
        List<Schedule> collidingSchedules = scheduleRepository.findCollidingSchedule(newRequest.startTime(), newRequest.endTime(), newRequest.borrowDate(), newRequest.roomId());
        return collidingSchedules.size() == 0;
    }

    public RequestController(RequestRepository requestRepository, ScheduleRepository scheduleRepository) {
        this.requestRepository = requestRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @GetMapping
    public ResponseEntity<List<Request>> findRequestByLendee(Principal principal, Pageable pageable) {
        Page<Request> page = requestRepository.findRequestByLendee(principal.getName(),
            PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSortOr(Sort.by(Sort.Direction.ASC, "borrow_date", "start_time"))
            )
        );

        if(page.hasContent()) return ResponseEntity.ok(page.getContent());
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Request> findRequestById(@PathVariable Long requestedId) {
        Request requestedRequest = requestRepository.findRequestById(requestedId);
        if(requestedRequest != null) {
            return ResponseEntity.ok(requestedRequest);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Void> createRequest(@RequestBody Request newRequest, Principal principal, UriComponentsBuilder ucb) {
        if(isNotCollidingWithOtherSchedule(newRequest)) {
            Request modifiedRequest = new Request(
                    null,
                    newRequest.borrowDate(),
                    newRequest.startTime(),
                    newRequest.endTime(),
                    principal.getName(),
                    newRequest.detail(),
                    "Pending",
                    newRequest.roomId()
            );

            Request savedRequest = requestRepository.save(modifiedRequest);
            URI locationOfNewRequest = ucb
                    .path("/request/{id}")
                    .buildAndExpand(savedRequest.id())
                    .toUri();

            return ResponseEntity.created(locationOfNewRequest).build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateRequest(@PathVariable Long requestedId, Principal principal, @RequestBody Request requestUpdate) {
        Request targetedRequest = requestRepository.findRequestByIdAndLendee(requestedId, principal.getName());
        if(targetedRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if(isNotCollidingWithOtherSchedule(requestUpdate)) {
            Request modifiedRequest = new Request(
                    requestedId,
                    requestUpdate.borrowDate(),
                    requestUpdate.startTime(),
                    requestUpdate.endTime(),
                    targetedRequest.lendee(),
                    requestUpdate.detail(),
                    targetedRequest.status(),
                    requestUpdate.roomId()
            );

            requestRepository.save(modifiedRequest);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/admin/{requestedId}")
    public ResponseEntity<Void> acceptRequest(@RequestParam("status") String status, @PathVariable Long requestedId, Principal principal, UriComponentsBuilder ucb) {
        Request targetedRequest = requestRepository.findRequestById(requestedId);
        if(targetedRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if(status.equalsIgnoreCase("accept")) {
            Request modifiedRequest = new Request(
                    requestedId,
                    targetedRequest.borrowDate(),
                    targetedRequest.startTime(),
                    targetedRequest.endTime(),
                    targetedRequest.lendee(),
                    targetedRequest.detail(),
                    "Accepted",
                    targetedRequest.roomId()
            );

            requestRepository.save(modifiedRequest);

            Schedule newSchedule = new Schedule(
                    null,
                    modifiedRequest.borrowDate(),
                    modifiedRequest.startTime(),
                    modifiedRequest.endTime(),
                    modifiedRequest.lendee(),
                    principal.getName(),
                    modifiedRequest.detail(),
                    modifiedRequest.roomId()
            );

            Schedule savedSchedule = scheduleRepository.save(newSchedule);
            URI locationOfNewSchedule = ucb
                    .path("/schedule/{id}")
                    .buildAndExpand(savedSchedule.id())
                    .toUri();

            return ResponseEntity.created(locationOfNewSchedule).build();
        }
        else if(status.equalsIgnoreCase("decline")) {
            Request modifiedRequest = new Request(
                    requestedId,
                    targetedRequest.borrowDate(),
                    targetedRequest.startTime(),
                    targetedRequest.endTime(),
                    targetedRequest.lendee(),
                    targetedRequest.detail(),
                    "Declined",
                    targetedRequest.roomId()
            );

            requestRepository.save(modifiedRequest);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestedId, Principal principal) {
        Request requestedRequest = requestRepository.findRequestByIdAndLendee(requestedId, principal.getName());
        if(requestedRequest != null) {
            requestRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
