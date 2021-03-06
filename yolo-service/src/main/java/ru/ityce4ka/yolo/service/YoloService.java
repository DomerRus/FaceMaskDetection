package ru.ityce4ka.yolo.service;

import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import ru.ityce4ka.yolo.model.DetectResponseDto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED;

@Slf4j
@Service
public class YoloService {

        private final LoadModelService loadModelService;
        YoloService(LoadModelService loadModelService){
            System.load(System.getProperty("java.library.path")+System.mapLibraryName(Core.NATIVE_LIBRARY_NAME));
            this.loadModelService = loadModelService;
        }

        public ByteArrayOutputStream maskDetectImage(InputStream is) {
            try{
                byte[] bytes = is.readAllBytes();
                Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), IMREAD_UNCHANGED);
                detect(mat);
                MatOfByte bytemat = new MatOfByte();
                Imgcodecs.imencode(".jpg", mat, bytemat);
                bytes = bytemat.toArray();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
                baos.write(bytes, 0, bytes.length);
                return baos;
            } catch (RuntimeException | ModelException | TranslateException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public byte[] maskDetectWS(byte[] ba) {
            try{
                Mat mat = Imgcodecs.imdecode(new MatOfByte(ba), IMREAD_UNCHANGED);
                detect(mat);
                MatOfByte bytemat = new MatOfByte();
                Imgcodecs.imencode(".jpg", mat, bytemat);
                return bytemat.toArray();
            } catch (RuntimeException | ModelException | TranslateException | IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public List<DetectResponseDto> maskDetect(InputStream is) {
            List<DetectResponseDto> detected = new ArrayList<>();
            try{
                byte[] bytes = is.readAllBytes();
                Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), IMREAD_UNCHANGED);
                Image img = mat2Image(mat);
                Double coefWidth = img.getWidth()/640.0;
                Double coefHeight = img.getHeight()/640.0;
                try (Predictor<Image, DetectedObjects> predictor = loadModelService.model.newPredictor()) {

                    DetectedObjects results = predictor.predict(img);
                    for (DetectedObject obj : results.<DetectedObject>items()) {

                        BoundingBox bbox = obj.getBoundingBox();
                        Rectangle rectangle = bbox.getBounds();

                        DetectResponseDto dto = DetectResponseDto.builder()
                                .className(obj.getClassName())
                                .probability(obj.getProbability())
                                .bounds(new ArrayList<>())
                                .build();

                        dto.getBounds().add(rectangle.getX()*coefWidth);
                        dto.getBounds().add(rectangle.getY()*coefHeight);
                        dto.getBounds().add(rectangle.getWidth()*coefWidth);
                        dto.getBounds().add(rectangle.getHeight()*coefHeight);
                        detected.add(dto);
                    }
                }
            } catch (RuntimeException | TranslateException | IOException e) {
                log.error(e.toString());
                log.error(Arrays.toString(e.getStackTrace()));
            }
            return detected;
        }

        void detect(Mat frame) throws IOException, ModelNotFoundException, MalformedModelException, TranslateException {
            Rect rect = new Rect();
            Map<String, Scalar> colorPicker = new HashMap<>();
            colorPicker.put("without_mask", new Scalar(0,0,255));
            colorPicker.put("with_mask", new Scalar(0,255,0));
            colorPicker.put("mask_weared_incorrect", new Scalar(255,0,0));
            Image img = mat2Image(frame);
            log.info("Image height: {}, width: {}",img.getHeight(), img.getWidth());
            Double coefWidth = img.getWidth()/640.0;
            Double coefHeight = img.getHeight()/640.0;
            try (Predictor<Image, DetectedObjects> predictor = loadModelService.model.newPredictor()) {

                DetectedObjects results = predictor.predict(img);
                for (DetectedObject obj : results.<DetectedObject>items()) {

                    BoundingBox bbox = obj.getBoundingBox();
                    Rectangle rectangle = bbox.getBounds();
                    String showText = String.format("%s: %.2f", obj.getClassName(), obj.getProbability());
                    rect.x = (int) (rectangle.getX()*coefWidth);
                    rect.y = (int) (rectangle.getY()*coefHeight);
                    rect.width = (int) (rectangle.getWidth()*coefWidth);
                    rect.height = (int) (rectangle.getHeight()*coefHeight);
                    Imgproc.rectangle(frame, rect, colorPicker.get(obj.getClassName()), 2);
                    Imgproc.putText(frame, showText,
                            new Point(rect.x, rect.y-20),
                            Imgproc.FONT_HERSHEY_COMPLEX,
                            rectangle.getWidth() / 125,
                            colorPicker.get(obj.getClassName()));
                }
            }
        }
        private static BufferedImage mat2bufferedImage(Mat image) {
            MatOfByte bytemat = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, bytemat);
            byte[] bytes = bytemat.toArray();
            InputStream in = new ByteArrayInputStream(bytes);
            BufferedImage img = null;
            try {
                img = ImageIO.read(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return img;
        }
        public Image mat2Image(Mat mat){
            return ImageFactory.getInstance().fromImage(mat2bufferedImage(mat));
        }

}