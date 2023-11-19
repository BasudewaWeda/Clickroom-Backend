package dev.basudewa.clickroom.entity;

import org.springframework.data.annotation.Id;

public record Schedule(@Id Long id, String borrowDate, String startTime, String endTime, String lendee, String lender, String detail, Long roomId) {
}
