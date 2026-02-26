package com.mutzin.wallpaperworldserver.controller;

import com.mutzin.wallpaperworldserver.config.WPWWebSocketHandler;
import com.mutzin.wallpaperworldserver.utils.PortListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequiredArgsConstructor
public class UploadController {
    private final WPWWebSocketHandler handler;
    private final PortListener portListener;
    @Value("${app.upload-dir:${user.home}/.wallpaper-world-server/uploads}")
    private String uploadDir;

    @GetMapping("/")
    public String index()
    {
        return "index";
    }
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file)
        throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        File dir = uploadPath.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String original = file.getOriginalFilename();
        String cleanName = original.replaceAll("[^a-zA-Z0-9.]", "");
        String fileName = System.currentTimeMillis() + "_" + cleanName;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "http://127.0.0.1:" + portListener.getPort() + "/files/" + fileName;
        handler.broadcast(fileUrl);

        return "redirect:/";
    }

}
