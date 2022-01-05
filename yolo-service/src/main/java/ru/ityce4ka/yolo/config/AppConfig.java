package ru.ityce4ka.yolo.config;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public HeadlessApplication getApplication(WebSocketComponent gameLoop) {
        return new HeadlessApplication(gameLoop);
    }
}
