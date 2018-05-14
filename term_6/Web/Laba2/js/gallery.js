var picIndex = 0
window.onload = function() {showPicture(picIndex)}


function nextPicture(n) {
    showPicture(picIndex += n);
}


function showPicture(n){
    var pictures = document.getElementsByTagName("img")

    if (n > pictures.length - 1) {picIndex = 0}
    if (n < 0) {picIndex = pictures.length - 1}
    for (var i = 0; i < pictures.length; i++) {
        pictures[i].style.display = "none"
    }
    pictures[picIndex].style.display = "block"
}


function play(){
    var pictures = document.getElementsByTagName("img")
    for (var i = 0; i < pictures.length; i++) {
        pictures[i].style.display = "none"
    }
    picIndex++
    if (picIndex > pictures.length - 1) {slideIndex = 0}
    pictures[picIndex].style.display = "block"
    setTimeout(play, 2000) 
}


function pause(){

}
