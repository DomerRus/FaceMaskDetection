package ru.ityce4ka.yolo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectResponseDto {
    List<Double> bounds;
    String className;
    Double probability;
}
