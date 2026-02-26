package com.mutzin.wallpaperworldserver.controller;

import com.mutzin.wallpaperworldserver.config.WPWWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class CommandController {
    private final WPWWebSocketHandler handler;

    @PostMapping("/command")
    public String sendCommand(@RequestParam String imageUrl) throws IOException {
        handler.broadcast(imageUrl);
        return "complete command";
    }
}