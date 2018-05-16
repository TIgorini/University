$(document).ready(function() {
    nextPicture(0)
    $("#play").show()
    $("#pause").hide()
    $(".art").mouseleave()
})


var picIndex = 0
var repeat = false


function nextPicture(step) {
    showPicture(picIndex += step, repeat)
}


function showPicture(n, repeat){
    var pictures = $("img")
    if (repeat){
        if (n > pictures.length - 1) {picIndex = 0}
        if (n < 0) {picIndex = pictures.length - 1}
    } else {
        if (n >= pictures.length - 1) {
            slideshow(false)
            picIndex = pictures.length - 1
        }
        if (n < 0) {picIndex = 0}
    }
    for (var i = 0; i < pictures.length; i++){
        pictures[i].style.display = "none"
    }
    pictures[picIndex].style.display = "block"
}


var slideshow = (function (){
    var inter
    return function (play){
        if (play) {
            inter = setInterval(nextPicture, $("#num-field").val(), 1)
            $("#play").hide()
            $("#pause").show()
        } else {
            clearInterval(inter)
            $("#play").show()
            $("#pause").hide()
        }
    }
})()


function setRepeat(){
    $("#repeat-icon").toggleClass("no-repeat-icon")
    $("#repeat-icon").toggleClass("repeat-icon")
    repeat = !repeat
}
