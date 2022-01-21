
# FaceMaskDetection

<img src="https://user-images.githubusercontent.com/47446020/148439722-4796ca96-5fd7-4acc-bae9-82c3d36f2b2a.jpg" width="500">

[source dataset](https://www.kaggle.com/andrewmvd/face-mask-detection)

# REST API

The REST API to the example app is described below.

## Search masks on image (return image)

### Request

`POST /detect/img/`

    curl -i -H 'Accept: application/json' http://localhost:8080/yolo-service/detect/img/

### Response

    HTTP/1.1 200 OK
    Date: Sun, 2 Jun 2022 12:36:30 GMT
    Status: 200 OK
    Connection: close
    Content-Type: image/jpeg
    Content-Length: 2

    [image]


## Search masks on image (return bbox coordinates)

### Request

`POST /detect/`

    curl -i -H 'Accept: application/json' http://localhost:8080/yolo-service/detect/

### Response

    HTTP/1.1 200 OK
    Date: Sun, 2 Jun 2022 12:36:30 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json
    Content-Length: 2

    [{
        "bounds": [
            107.24603271484375,
            328.1503601074219,
            132.0777587890625,
            114.7342529296875
        ],
        "className": "without_mask",
        "probability": 0.8773182034492493
    },...]


# Train

### face_mask.yaml

```
train: ../../dataset/set/images/train 
val: ../../dataset/set/images/val
test: ../../dataset/set/images/test

nc: 3

names: ["without_mask", "with_mask", "mask_weared_incorrect"]
```



### GPU Training requirements

```
py -m pip install torch==1.10.1+cu113 torchvision==0.11.2+cu113 -f https://download.pytorch.org/whl/torch_stable.html
```
```
https://developer.nvidia.com/cuda-toolkit-archive
```
### Train model

use `--device [0,1, ... or cpu]` for choose GPU 

```
python train.py --img 640 --cfg yolov5s.yaml --hyp hyp.scratch.yaml --batch 16 --epochs 100 --data face_mask.yaml --workers 12 --name <your project name>
```
### export model
```
python export.py --weights ./runs/train/<your project name>/weights/best.pt --include torchscript
```
