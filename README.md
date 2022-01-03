# YoloSpringJava

# REST API

The REST API to the example app is described below.

## Search masks on image

### Request

`POST /detect/img/`

    curl -i -H 'Accept: application/json' http://localhost:8081/detect/img/

### Response

    HTTP/1.1 200 OK
    Date: Thu, 24 Feb 2011 12:36:30 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json
    Content-Length: 2

    []


## Search masks on image

### Request

`POST /detect/`

    curl -i -H 'Accept: application/json' http://localhost:8081/detect/

### Response

    HTTP/1.1 200 OK
    Date: Thu, 24 Feb 2011 12:36:30 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json
    Content-Length: 2

    []

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
python train.py --img 640 --cfg yolov5s.yaml --hyp hyp.scratch.yaml --batch 16 --epochs 100 --data face_mask.yaml --workers 12 --name yolo_faсe_mask
```
### export model
```
python export.py --weights ./runs/train/yolo_face_mask18/weights/best.pt --include torchscript
```
