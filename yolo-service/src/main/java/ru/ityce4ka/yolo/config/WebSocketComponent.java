package ru.ityce4ka.yolo.config;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.Array;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.ityce4ka.yolo.service.YoloService;
import ru.ityce4ka.yolo.ws.WebSocketHandler;

import java.io.IOException;


@Component
public class WebSocketComponent extends ApplicationAdapter {
    private final WebSocketHandler socketHandler;
    private final Array<byte[]> frames = new Array<>();
    private final Array<String> events = new Array<>();
    private final YoloService yoloService;

    public WebSocketComponent(WebSocketHandler socketHandler, YoloService yoloService) {
        this.socketHandler = socketHandler;
        this.yoloService = yoloService;
    }

    @Override
    public void create() {
        socketHandler.setConnectListener(session -> {
            events.add(session.getId() + " just joined");
            System.out.println(session.getId() + " just joined");
        });
        socketHandler.setDisconnectListener(session -> {
            events.add(session.getId() + " just disconnected");
            System.out.println(session.getId() + " just disconnected");
        });
        socketHandler.setMessageByteListener(((session, message) -> {
            frames.add(
                    yoloService.maskDetectWS(message)
            );
        }));
    }

    @Override
    public void render() {
        for (WebSocketSession session : socketHandler.getSessions()) {
            try {
                for (byte[] event : frames) {
                    session.sendMessage(new BinaryMessage(event));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        frames.clear();
        events.clear();
    }
}
