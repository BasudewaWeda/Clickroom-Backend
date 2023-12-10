package dev.basudewa.clickroom.request;

import dev.basudewa.clickroom.room.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.sql.Time;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestResponse {
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date borrowDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time startTime;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time endTime;

    private String lendee;
    private String borrowDetail;
    private RequestStatus requestStatus;

    private Long roomId;
    private Integer roomCapacity;
    private String roomLocation;
    private String roomName;
}
