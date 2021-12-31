# YoloSpringJava

# REST API

The REST API to the example app is described below.

## Get list of Things

### Request

`POST /detect/img/`

    curl -i -H 'Accept: application/json' http://localhost:8081//detect/img/

### Response

    HTTP/1.1 200 OK
    Date: Thu, 24 Feb 2011 12:36:30 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json
    Content-Length: 2

    []
