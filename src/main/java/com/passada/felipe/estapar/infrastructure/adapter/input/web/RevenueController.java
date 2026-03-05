package com.passada.felipe.estapar.infrastructure.adapter.input.web;

import com.passada.felipe.estapar.application.usecases.GetRevenueUseCase;
import com.passada.felipe.estapar.infrastructure.adapter.input.web.dto.RevenueRequest;
import com.passada.felipe.estapar.infrastructure.adapter.input.web.dto.RevenueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/revenue")
@RequiredArgsConstructor
@Tag(name = "Revenue", description = "Revenue inquiry by sector and date")
public class RevenueController {

    private final GetRevenueUseCase getRevenueUseCase;

    @GetMapping
    @Operation(summary = "Get revenue", description = "Returns the total revenue for a given sector on a specific date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Revenue retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Sector not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<RevenueResponse> getRevenue(
            @RequestBody RevenueRequest request
    ) {
        BigDecimal amount = getRevenueUseCase.execute(request.sector(), request.date());

        log.info("Revenue for sector '{}' on date {}: {}", request.sector(), request.date(), amount);
        RevenueResponse response = new RevenueResponse(
                amount,
                "BRL",
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }
}
