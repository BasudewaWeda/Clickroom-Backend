package dev.basudewa.clickroom.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<Request>> findRequestByLendee(Principal principal, Pageable pageable) {
        Page<Request> page = requestService.getRequestByLendee(principal, pageable);

        if(page.hasContent()) return ResponseEntity.ok(page.getContent());
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Request> findRequestById(@PathVariable Long requestedId, Principal principal) {
        Request requestedRequest = requestService.getRequestByIdAndLendee(requestedId, principal.getName());
        if(requestedRequest != null) {
            return ResponseEntity.ok(requestedRequest);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Void> createRequest(@RequestBody Request newRequest, Principal principal, UriComponentsBuilder ucb) {
        if(requestService.isNotCollidingWithOtherSchedule(newRequest)) {
            URI locationOfNewRequest = requestService.createRequest(newRequest, ucb, principal);

            return ResponseEntity.created(locationOfNewRequest).build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateRequest(@PathVariable Long requestedId, Principal principal, @RequestBody Request requestUpdate) {
        Request targetedRequest = requestService.getRequestByIdAndLendee(requestedId, principal.getName());
        if(targetedRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if(requestService.isNotCollidingWithOtherSchedule(requestUpdate)) {
            requestService.updateRequest(requestedId, requestUpdate, targetedRequest);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/admin/{requestedId}")
    public ResponseEntity<Void> acceptRequest(@RequestParam("status") String status, @PathVariable Long requestedId, Principal principal, UriComponentsBuilder ucb) {
        Request targetedRequest = requestService.getRequestById(requestedId);
        if(targetedRequest == null) {
            return ResponseEntity.notFound().build();
        }

        if(status.equalsIgnoreCase("accept")) {
            URI locationOfNewSchedule = requestService.acceptRequestByAdmin(requestedId, principal, ucb, targetedRequest);

            return ResponseEntity.created(locationOfNewSchedule).build();
        }
        else if(status.equalsIgnoreCase("decline")) {
            requestService.declineRequestByAdmin(requestedId, targetedRequest);

            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long requestedId, Principal principal) {
        if(requestService.requestExists(requestedId)) {
            requestService.deleteRequest(requestedId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
