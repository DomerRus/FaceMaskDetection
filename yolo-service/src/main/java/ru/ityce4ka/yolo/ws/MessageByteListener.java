package ru.ityce4ka.yolo.ws;

import org.springframework.web.socket.WebSocketSession;

public interface MessageByteListener {
    void handle(WebSocketSession session, byte[] img);
}
