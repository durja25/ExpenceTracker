package org.development.ExpenceTracker.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.development.ExpenceTracker.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData =
                dashboardService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }
}