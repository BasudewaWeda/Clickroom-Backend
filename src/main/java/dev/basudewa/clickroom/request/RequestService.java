package dev.basudewa.clickroom.request;

import dev.basudewa.clickroom.schedule.Schedule;
import dev.basudewa.clickroom.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final ScheduleRepository scheduleRepository;

    public boolean isNotCollidingWithOtherSchedule(Request newRequest) {
        List<Schedule> collidingSchedules = scheduleRepository.findCollidingSchedule(newRequest.getStartTime(), newRequest.getEndTime(), newRequest.getBorrowDate(), newRequest.getRoom().getId());
        return collidingSchedules.size() == 0;
    }

    public boolean requestExists(Long requestedId, String lendee) {
        return requestRepository.findRequestByIdAndLendee(requestedId, lendee) != null;
    }

    public RequestResponse buildResponse(Request request) {
        return RequestResponse
                .builder()
                .id(request.getId())
                .borrowDate(request.getBorrowDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .requestStatus(request.getRequestStatus())
                .lendee(request.getLendee())
                .borrowDetail(request.getBorrowDetail())
                .roomId(request.getRoom().getId())
                .roomCapacity(request.getRoom().getRoomCapacity())
                .roomLocation(request.getRoom().getRoomLocation())
                .roomName(request.getRoom().getRoomName())
                .build();
    }

    public RequestListResponse buildResponseList(List<Request> requestList) {
        List<RequestResponse> requests = new ArrayList<>();
        for(Request request : requestList) {
            requests.add(buildResponse(request));
        }

        return RequestListResponse
                .builder()
                .requestList(requests)
                .build();
    }

    public RequestListResponse getRequestByLendee(Principal principal, Pageable pageable) {
        Page<Request> requestedRequests = requestRepository.findRequestByLendee(principal.getName(), PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "borrowDate", "startTime"))
        ));

        if(!requestedRequests.hasContent()) {
            return null;
        }

        return buildResponseList(requestedRequests.getContent());
    }

    public RequestListResponse getAllRequest(Pageable pageable) {
        Page<Request> requestedRequests = requestRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "borrowDate", "startTime"))
        ));

        if(!requestedRequests.hasContent()) {
            return null;
        }

        return buildResponseList(requestedRequests.getContent());
    }

    public Request getRequestByIdAndLendeeNoResponseObject(Long requestedId, String lendee) {
        return requestRepository.findRequestByIdAndLendee(requestedId, lendee);
    }

    public RequestResponse getRequestByIdAndLendee(Long requestedId, String lendee) {
        Request requestedRequest = requestRepository.findRequestByIdAndLendee(requestedId, lendee);
        if(requestedRequest == null) {
            return null;
        }
        return buildResponse(requestedRequest);
    }

    public Request getRequestById(Long requestedId) {
        return requestRepository.findRequestById(requestedId);
    }

    public URI createRequest(Request newRequest, UriComponentsBuilder ucb, Principal principal) {
        Request modifiedRequest = new Request(
                null,
                newRequest.getBorrowDate(),
                newRequest.getStartTime(),
                newRequest.getEndTime(),
                principal.getName(),
                newRequest.getBorrowDetail(),
                RequestStatus.PENDING,
                newRequest.getRoom()
        );

        Request savedRequest = requestRepository.save(modifiedRequest);

        return ucb
                .path("/request/{id}")
                .buildAndExpand(savedRequest.getId())
                .toUri();
    }

    public void updateRequest(Long requestedId, Request requestUpdate, Request oldRequest) {
        Request modifiedRequest = new Request(
                requestedId,
                (requestUpdate.getBorrowDate() != null) ? requestUpdate.getBorrowDate() : oldRequest.getBorrowDate(),
                (requestUpdate.getStartTime() != null) ? requestUpdate.getStartTime() : oldRequest.getStartTime(),
                (requestUpdate.getEndTime() != null) ? requestUpdate.getEndTime() : oldRequest.getEndTime(),
                oldRequest.getLendee(),
                (requestUpdate.getBorrowDetail() != null) ? requestUpdate.getBorrowDetail() : oldRequest.getBorrowDetail(),
                oldRequest.getRequestStatus(),
                (requestUpdate.getRoom() != null) ? requestUpdate.getRoom() : oldRequest.getRoom()
        );

        requestRepository.save(modifiedRequest);
    }

    public URI acceptRequestByAdmin(Long requestedId, Principal principal, UriComponentsBuilder ucb, Request targetedRequest) {
        if(scheduleRepository.findCollidingSchedule(targetedRequest.getStartTime(), targetedRequest.getEndTime(), targetedRequest.getBorrowDate(), targetedRequest.getRoom().getId()).size() != 0) {
            return null;
        }

        Request modifiedRequest = new Request(
                requestedId,
                targetedRequest.getBorrowDate(),
                targetedRequest.getStartTime(),
                targetedRequest.getEndTime(),
                targetedRequest.getLendee(),
                targetedRequest.getBorrowDetail(),
                RequestStatus.ACCEPTED,
                targetedRequest.getRoom()
        );
        requestRepository.save(modifiedRequest);

        Schedule newSchedule = new Schedule(
                null,
                modifiedRequest.getBorrowDate(),
                modifiedRequest.getStartTime(),
                modifiedRequest.getEndTime(),
                modifiedRequest.getLendee(),
                principal.getName(),
                modifiedRequest.getBorrowDetail(),
                modifiedRequest.getRoom()
        );
        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        return ucb
                .path("/schedule/{id}")
                .buildAndExpand(savedSchedule.getId())
                .toUri();
    }

    public void declineRequestByAdmin(Long requestedId, Request targetedRequest) {
        Request modifiedRequest = new Request(
                requestedId,
                targetedRequest.getBorrowDate(),
                targetedRequest.getStartTime(),
                targetedRequest.getEndTime(),
                targetedRequest.getLendee(),
                targetedRequest.getBorrowDetail(),
                RequestStatus.DECLINED,
                targetedRequest.getRoom()
        );
        requestRepository.save(modifiedRequest);
    }

    public void deleteRequest(Long requestedId) {
        requestRepository.deleteById(requestedId);
    }
}
