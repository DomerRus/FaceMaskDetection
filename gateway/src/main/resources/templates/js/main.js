let base_url = "http://localhost:8081/"
var url = "wss://ws.1dnr-sevice.com/ws";
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
const scroll = document.querySelector(".scroll-bar")
const imageLoader = document.querySelector("#imageLoader")
const dropArea = document.querySelector(".drag-image"),
	dragText = dropArea.querySelector("h6"),
	button = dropArea.querySelector("button"),
	input = dropArea.querySelector("input");
let file;

button.onclick = ()=>{
	input.click();
}

input.addEventListener("change", function(){

	file = this.files[0];
	let formData = new FormData();
	formData.append("photo", file, "photo.jpeg" )
	sendPhoto(formData)
	dropArea.classList.add("active");
});

function sendPhoto(formData){
	$.ajax({
		type: "POST",
		url: base_url+"/detect/img",
		data: formData,
		processData: false,
		contentType: false,
		mimeType: "text/plain; charset=x-user-defined",
		success: function (result, textStatus, xhr) {
			if(result.length < 1){
				alert("The thumbnail doesn't exist");
				return
			}

			var binary = "";
			var responseText = xhr.responseText;
			var responseTextLen = responseText.length;

			for ( i = 0; i < responseTextLen; i++ ) {
				binary += String.fromCharCode(responseText.charCodeAt(i) & 255)
			}

			file = dataURLtoFile('data:image/jpeg;base64,' + base64Encode(result));
			viewfile();
			//$("#image").attr('src', 'data:image/jpeg;base64,' + base64Encode(result));
		},
		beforeSend: function(xhr) {
			xhr.setRequestHeader("Access-Control-Allow-Origin", "https://1dnr-sevice.com")
		}
	});
}

function dataURLtoFile(dataurl, filename) {

	var arr = dataurl.split(','),
		mime = arr[0].match(/:(.*?);/)[1],
		bstr = atob(arr[1]),
		n = bstr.length,
		u8arr = new Uint8Array(n);

	while(n--){
		u8arr[n] = bstr.charCodeAt(n);
	}

	return new File([u8arr], filename, {type:mime});
}

dropArea.addEventListener("dragover", (event)=>{
	event.preventDefault();
	dropArea.classList.add("active");
	dragText.textContent = "Release to Upload File";
});


dropArea.addEventListener("dragleave", ()=>{
	dropArea.classList.remove("active");
	dragText.textContent = "Drag & Drop to Upload File";
});

dropArea.addEventListener("drop", (event)=>{
	event.preventDefault();

	file = event.dataTransfer.files[0];
	let formData = new FormData();
	formData.append("photo", file, "photo.jpeg" )
	sendPhoto(formData)
});

function viewfile(){
	let fileType = file.type;
	let validExtensions = ["image/jpeg", "image/jpg", "image/png"];
	if(validExtensions.includes(fileType)){
		changeClass(file);
	}else{
		alert("This is not an Image File!");
		dropArea.classList.remove("active");
		dragText.textContent = "Drag & Drop to Upload File";
	}
}
var _URL = window.URL || window.webkitURL;

function changeClass(file){
	if(imageLoader.className === 'imageLoader-center'){
		document.querySelector("#imageLoader").setAttribute('class', 'imageLoader-left');
		document.querySelector("#imageVsbl").setAttribute('class', 'visible left');
		document.querySelector("#gallery").setAttribute('class', 'gallery');
	}
	var objectUrl = _URL.createObjectURL(file);
	var img = document.createElement('img');
	var div = document.createElement('div');
	img.setAttribute('width', '100%');
	img.setAttribute('height', '100%');
	img.setAttribute('alt', '');
	img.setAttribute('src', objectUrl);
	img.addEventListener('click', function (){
		$('#imageView').attr('src', this.src)
	});
	div.setAttribute('class', 'card')
	div.insertAdjacentElement('afterbegin', img)
	scroll.insertAdjacentElement('afterbegin', div)
	$('#imageView').attr('src', objectUrl)
}

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

function hideCamera(){
	$("#cameraVsbl").attr("class", "hidden")
	$("#imageVsbl").attr("class", "visible")
	video.pause()
	clearInterval(interval)
}
function hideImage(){
	$("#cameraVsbl").attr("class", "visible")
	$("#imageVsbl").attr("class", "hidden")
	try {
		if (video.srcObject == null) {
			navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
				video.srcObject = stream;
				video.play();
			}).catch(function (err) {

			});
		} else {
			video.play()
		}
	}catch (e){
		navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
			video.srcObject = stream;
			video.play();
		}).catch(function (err) {

		});
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


		
	
	


