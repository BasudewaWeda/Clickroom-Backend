package dev.basudewa.clickroom.controller;

import dev.basudewa.clickroom.entity.Facility;
import dev.basudewa.clickroom.repository.FacilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/facility")
public class FacilityController {
    private final FacilityRepository facilityRepository;

    public FacilityController(FacilityRepository facilityRepository) { this.facilityRepository = facilityRepository; }

    @GetMapping("/room/{requestedId}")
    public ResponseEntity<List<Facility>> findFacilityByRomId(@PathVariable Long requestedId, Pageable pageable) {
        Page<Facility> page = facilityRepository.findFacilityByRoomId(requestedId,
                PageRequest.of(
                       pageable.getPageNumber(),
                       pageable.getPageSize(),
                       pageable.getSortOr(Sort.by(Sort.Direction.ASC, "facility_name"))
                )
        );

        if(page.hasContent()) return ResponseEntity.ok(page.getContent());
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Facility> findFacilityById(@PathVariable Long requestedId) {
        Facility requestedFacility = facilityRepository.findFacilityById(requestedId);
        if(requestedFacility != null) {
            return ResponseEntity.ok(requestedFacility);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<Void> createFacility(@RequestBody Facility newFacility, UriComponentsBuilder ucb) {
        Facility savedFacility = facilityRepository.save(newFacility);
        URI locationOfNewFacility = ucb
                .path("/facility/{id}")
                .buildAndExpand(savedFacility.id())
                .toUri();

        return ResponseEntity.created(locationOfNewFacility).build();
    }
}
