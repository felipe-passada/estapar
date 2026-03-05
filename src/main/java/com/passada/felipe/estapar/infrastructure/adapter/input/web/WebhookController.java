package com.passada.felipe.estapar.infrastructure.adapter.input.web;

import com.passada.felipe.estapar.application.usecases.ProcessEntryUseCase;
import com.passada.felipe.estapar.application.usecases.ProcessExitUseCase;
import com.passada.felipe.estapar.application.usecases.ProcessParkedUseCase;
import com.passada.felipe.estapar.infrastructure.adapter.input.web.dto.WebhookEventRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Tag(name = "Webhook", description = "Receives vehicle events from the garage simulator (ENTRY, PARKED, EXIT)")
public class WebhookController {

    private final ProcessEntryUseCase processEntryUseCase;
    private final ProcessParkedUseCase processParkedUseCase;
    private final ProcessExitUseCase processExitUseCase;

    @PostMapping
    @Operation(summary = "Process simulator event", description = "Receives and processes ENTRY, PARKED and EXIT events from the garage simulator")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event processed successfully"),
            @ApiResponse(responseCode = "422", description = "Business rule violation (e.g. parking lot is full)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> receiveEvent(@RequestBody WebhookEventRequest request) {
        log.info("Incoming webhook event: type={}, licensePlate={}", request.type(), request.plate());

        switch(request.type()) {
            case ENTRY -> processEntryUseCase.execute(request.plate(), request.entryTime());
            case PARKED -> processParkedUseCase.execute(request.plate(), request.lat(), request.lng());
            case EXIT -> processExitUseCase.execute(request.plate(), request.exitTime());
            default -> log.warn("Unknown event type received: {}", request.type());
        }

        return ResponseEntity.ok().build();
    }
}
