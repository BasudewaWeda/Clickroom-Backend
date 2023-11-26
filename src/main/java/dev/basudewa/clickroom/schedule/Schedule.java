package dev.basudewa.clickroom.schedule;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.basudewa.clickroom.room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.sql.Time;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
