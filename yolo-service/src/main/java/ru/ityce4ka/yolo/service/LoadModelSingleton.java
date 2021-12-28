package ru.ityce4ka.yolo.service;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Pipeline;
import ai.djl.translate.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class LoadModelSingleton {

    private static LoadModelSingleton instance;
    public ZooModel<Image, DetectedObjects> model;

    private LoadModelSingleton() throws ModelNotFoundException, MalformedModelException, IOException {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(640, 640));
        pipeline.add(new ToTensor());
        Translator<Image, DetectedObjects> translator = YoloV5Translator.builder().setPipeline(pipeline).optThreshold(0.75f).optSynsetArtifactName("face_mask.yaml").build();
        Criteria<Image, DetectedObjects> criteria =
                Criteria.builder()
                        .setTypes(Image.class, DetectedObjects.class)
                        .optDevice(Device.cpu())
                        .optModelUrls("./yolo")
                        .optModelName("best.torchscript")
                        .optTranslator(translator)
                        .optEngine("PyTorch")
                        .build();
        this.model = ModelZoo.loadModel(criteria);
    }

    public static LoadModelSingleton getInstance() throws ModelNotFoundException, MalformedModelException, IOException {
        if (instance == null) {
            instance = new LoadModelSingleton();
        }
        return instance;
    }
}
