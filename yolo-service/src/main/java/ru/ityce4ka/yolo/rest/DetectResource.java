package ru.ityce4ka.yolo.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ityce4ka.yolo.model.DetectResponseDto;
import ru.ityce4ka.yolo.service.YoloService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/detect")
@RequiredArgsConstructor
public class DetectResource {

    private final YoloService service;

    @PostMapping("/img")
    ResponseEntity<String> detectMasksImage(HttpServletResponse response,
                                    @RequestPart("photo") MultipartFile photo) throws IOException {
        final ByteArrayOutputStream image = service.maskDetectImage(photo.getInputStream());
        try(OutputStream os = response.getOutputStream()){
            response.setHeader("Content-Type", "image/jpeg");
            response.setHeader("Content-Disposition","attachment; result.jpg");
            response.setHeader("Content-Length", String.valueOf((image).size()));
            (image).writeTo(os);
            response.flushBuffer();
            return ResponseEntity.ok().build();
        }catch (Exception ex) {
            log.error(ex.toString());
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping
    List<DetectResponseDto> detectMasks(HttpServletResponse response,
                                        @RequestPart("photo") MultipartFile photo) throws IOException {
         return service.maskDetect(photo.getInputStream());
    }

}
