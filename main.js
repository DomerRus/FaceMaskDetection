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
        url: "http://192.168.0.100:8081/detect/img",
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
let fileReader = new FileReader();

fileReader.onload = ()=>{
let fileURL = fileReader.result;
let imgTag = `<img src="${fileURL}" alt="image">`;
dropArea.innerHTML = imgTag;
}
fileReader.readAsDataURL(file);
}else{
alert("This is not an Image File!");
dropArea.classList.remove("active");
dragText.textContent = "Drag & Drop to Upload File";
}
}
