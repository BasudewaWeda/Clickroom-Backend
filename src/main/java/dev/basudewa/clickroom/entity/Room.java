package dev.basudewa.clickroom.entity;

import org.springframework.data.annotation.Id;

public record Room(@Id Long id, int capacity, String location) {

}
