package dev.basudewa.clickroom.entity;

import org.springframework.data.annotation.Id;

public record Facility(@Id Long id, String facilityName, int amount, Long roomId) {
}
