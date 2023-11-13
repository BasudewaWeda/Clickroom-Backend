package dev.basudewa.clickroom.entity;

import org.springframework.data.annotation.Id;

import java.util.Date;

public record Schedule(@Id Long id, String borrowDate, String startTime, String endTime, String lendee, String lender, Long roomId) {
}
