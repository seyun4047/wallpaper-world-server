package com.mutzin.wallpaperworldserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class WallpaperWorldServerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WallpaperWorldServerApplication.class);
        app.setHeadless(false);
        app.run(args);
    }

    @Bean
    public ApplicationRunner openBrowser() {
        return args -> {
            try {
                Thread.sleep(2000);

                if (Desktop.isDesktopSupported()) {
//                    test port!
                    Desktop.getDesktop().browse(new URI("http://localhost:21020"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
