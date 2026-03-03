package com.passada.felipe.estapar.infrastructure.adapter.input.web;

import com.passada.felipe.estapar.application.usecases.GetRevenueUseCase;
import com.passada.felipe.estapar.infrastructure.adapter.input.web.dto.RevenueRequest;
import com.passada.felipe.estapar.infrastructure.adapter.input.web.dto.RevenueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final GetRevenueUseCase getRevenueUseCase;

    @GetMapping
    public ResponseEntity<RevenueResponse> getRevenue(
            @RequestBody RevenueRequest request
    ) {
        BigDecimal amount = getRevenueUseCase.execute(request.sector(), request.date());
        RevenueResponse response = new RevenueResponse(
                amount,
                "BRL",
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }
}
