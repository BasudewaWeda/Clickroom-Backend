package dev.basudewa.clickroom.schedule;

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
public class ScheduleResponse {
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date borrowDate;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time startTime;

    @DateTimeFormat(pattern = "HH:mm:ss")
    private Time endTime;

    private String lendee;
    private String lender;
    private String borrowDetail;

    private Long roomId;
    private Integer roomCapacity;
    private String roomLocation;
    private String roomName;
}
