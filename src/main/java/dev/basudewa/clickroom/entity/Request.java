package dev.basudewa.clickroom.entity;

import org.springframework.data.annotation.Id;

public record Request(@Id Long id, String borrowDate, String startTime, String endTime, String lendee, String detail, String status, Long roomId) {
}
