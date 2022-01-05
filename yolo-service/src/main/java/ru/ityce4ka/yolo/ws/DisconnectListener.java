package ru.ityce4ka.yolo.ws;

import org.springframework.web.socket.WebSocketSession;

public interface DisconnectListener {
    void handle(WebSocketSession session);
}
