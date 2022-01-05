let base_url = "http://localhost:8081"
var url = "ws://localhost:8081/ws";
var socket = new WebSocket(url);
var video = document.querySelector('video');
var constraints={
	video: {
		width: 1280,
		height: 720
	},
	audio:false
};
let interval = null;
function on_click(){
	var formData = new FormData($("form[name='uploader']")[0])
	$.ajax({
		type: "POST",
		url: base_url + "/detect/img",
		data: formData,
		processData: false,
		contentType: false,
		mimeType: "text/plain; charset=x-user-defined",
		success: function (result, textStatus, jqXHR) {
			if(result.length < 1){
				alert("The thumbnail doesn't exist");
				return
			}

			var binary = "";
			var responseText = jqXHR.responseText;
			var responseTextLen = responseText.length;

			for ( i = 0; i < responseTextLen; i++ ) {
				binary += String.fromCharCode(responseText.charCodeAt(i) & 255)
			}
			$("#image").attr('src', 'data:image/jpeg;base64,' + base64Encode(result));
		}
	});

	function base64Encode(str) {
		var CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		var out = "", i = 0, len = str.length, c1, c2, c3;
		while (i < len) {
			c1 = str.charCodeAt(i++) & 0xff;
			if (i == len) {
				out += CHARS.charAt(c1 >> 2);
				out += CHARS.charAt((c1 & 0x3) << 4);
				out += "==";
				break;
			}
			c2 = str.charCodeAt(i++);
			if (i == len) {
				out += CHARS.charAt(c1 >> 2);
				out += CHARS.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
				out += CHARS.charAt((c2 & 0xF) << 2);
				out += "=";
				break;
			}
			c3 = str.charCodeAt(i++);
			out += CHARS.charAt(c1 >> 2);
			out += CHARS.charAt(((c1 & 0x3) << 4) | ((c2 & 0xF0) >> 4));
			out += CHARS.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >> 6));
			out += CHARS.charAt(c3 & 0x3F);
		}
		return out;
	}
}

function hideCamera(){
	$("#cameraVsbl").attr("class", "hidden")
	$("#imageVsbl").attr("class", "visible")
	video.pause()
	clearInterval(interval)
}
function hideImage(){
	$("#cameraVsbl").attr("class", "visible")
	$("#imageVsbl").attr("class", "hidden")
	if (video.srcObject == null ) {
		navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
			video.srcObject = stream;
			video.play();
		}).catch(function (err) {

		});
	}else {
		video.play()
	}
	interval = setInterval(main ,1500);
}

var canvas = document.querySelector('canvas');
var img = document.querySelector('#camera');
var context=canvas.getContext('2d');

socket.onopen=onOpen;
function onOpen(event){
	//alert("[open] Соединение установлено");
}







function main(){
	console.log(socket.readyState)
	if (socket.readyState == socket.OPEN) {
		drawCanvas();
		readCanvas();
	}
}

function drawCanvas(){

	context.drawImage(video,0,0,canvas.width, canvas.height);
}


socket.onclose = function(event) {
	if (event.wasClean) {
		//alert(`[close] Соединение закрыто чисто, код=${event.code} причина=${event.reason}`);
	} else {
		// например, сервер убил процесс или сеть недоступна
		// обычно в этом случае event.code 1006
		alert('[close] Соединение прервано');
	}
	socket = new WebSocket(url);
};

function readCanvas(){
	var canvasData = canvas.toDataURL('image/jpeg',1.0);
	var decodeAstring = atob(canvasData.split(',')[1]);

	var charArray =[];

	for(var i=0; i<decodeAstring.length;i++){

		charArray.push(decodeAstring.charCodeAt(i));
	}

   socket.send( new Blob([new Uint8Array(charArray)],{
	   type:'image/jpeg'
   }));

	socket.addEventListener('message',function(event){
		img.src=window.URL.createObjectURL(event.data);
	});

}


		
	
	


