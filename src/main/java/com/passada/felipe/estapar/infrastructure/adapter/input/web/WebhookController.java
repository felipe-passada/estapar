package com.passada.felipe.estapar.infrastructure.adapter.input.web;

import com.passada.felipe.estapar.application.usecases.ProcessEntryUseCase;
import com.passada.felipe.estapar.application.usecases.ProcessExitUseCase;
import com.passada.felipe.estapar.application.usecases.ProcessParkedUseCase;
import com.passada.felipe.estapar.infrastructure.adapter.input.web.dto.WebhookEventRequest;
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
public class WebhookController {

    private final ProcessEntryUseCase processEntryUseCase;
    private final ProcessParkedUseCase processParkedUseCase;
    private final ProcessExitUseCase processExitUseCase;

    @PostMapping
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
