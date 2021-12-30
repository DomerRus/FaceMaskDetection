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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_UNCHANGED;

@Slf4j
@Service
public class YoloService {

        static {
           // File dir = new File("/lib"); //path указывает на директорию
           // File[] arrFiles = dir.listFiles();
           // List<File> lst = Arrays.asList(arrFiles);
            //log.info(lst.toString());
            System.load(System.getProperty("java.library.path")+System.mapLibraryName(Core.NATIVE_LIBRARY_NAME));
            //System.loadLibrary("libopencv_core.so.4.5.4");
        }

        public ByteArrayOutputStream maskDetect(InputStream is) {
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


        void detect(Mat frame) throws IOException, ModelNotFoundException, MalformedModelException, TranslateException {
            Rect rect = new Rect();
            Map<String, Scalar> colorPicker = new HashMap<>();
            colorPicker.put("without_mask", new Scalar(0,0,255));
            colorPicker.put("with_mask", new Scalar(0,255,0));
            colorPicker.put("mask_weared_incorrect", new Scalar(255,0,0));
            Image img = mat2Image(frame);
            Double coefWidth = img.getWidth()/640.0;
            Double coefHeight = img.getHeight()/640.0;
            long startTime = System.currentTimeMillis();
            try (Predictor<Image, DetectedObjects> predictor = LoadModelSingleton.getInstance().model.newPredictor()) {

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
            System.out.println(String.format("%.2f", 1000.0 / (System.currentTimeMillis() - startTime)));
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