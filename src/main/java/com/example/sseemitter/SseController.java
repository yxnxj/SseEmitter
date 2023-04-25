package com.example.sseemitter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequiredArgsConstructor
public class SseController {
    private final SseEmitters sseEmitters;

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping(value = "/connect/{userId}/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter();
        sseEmitters.add(emitter);
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            try {
                for (int i = 0; true; i++) {
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            .name("connect")
                            .data("SSE MVC - " + LocalTime.now().toString())
                            .id(String.valueOf(userId))
                            .name("sse event - mvc");
                    emitter.send(event);
                    Thread.sleep(1000);
                }
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
//        try {
//            emitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("connected!" + userId));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return ResponseEntity.ok(emitter);
    }
}
