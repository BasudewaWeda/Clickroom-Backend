package dev.basudewa.clickroom.entity;

import org.springframework.data.annotation.Id;

public record Room(@Id Long id, String name, Integer capacity, String location) {

}
