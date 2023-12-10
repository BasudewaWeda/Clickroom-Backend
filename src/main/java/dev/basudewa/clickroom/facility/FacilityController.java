package dev.basudewa.clickroom.facility;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/facility")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @GetMapping("/{requestedId}")
    public ResponseEntity<Facility> findFacilityById(@PathVariable Long requestedId) {
        Facility requestedFacility = facilityService.getFacilityById(requestedId);
        if(requestedFacility != null) {
            return ResponseEntity.ok(requestedFacility);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/admin")
    public ResponseEntity<URI> createFacility(@RequestBody Facility newFacility, UriComponentsBuilder ucb) {
        URI locationOfNewFacility = facilityService.createFacility(newFacility, ucb);
        return ResponseEntity.created(locationOfNewFacility).body(locationOfNewFacility);
    }

    @PutMapping("/admin/{requestedId}")
    public ResponseEntity<Void> updateFacility(@PathVariable Long requestedId, @RequestBody Facility facilityUpdate) {
        Facility facility = facilityService.getFacilityById(requestedId);
        if(facility != null) {
            facilityService.updateFacility(requestedId, facilityUpdate, facility);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/admin/{requestedId}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Long requestedId) {
        if(facilityService.facilityExists(requestedId)) {
            facilityService.deleteFacility(requestedId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
