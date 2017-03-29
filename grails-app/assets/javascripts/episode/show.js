$(document).ready(function() {
    var video = document.getElementsByTagName("video")[0];
    video.addEventListener("playing", playingVideo, true);
    video.addEventListener("pause", notPlayingVideo, true);
    video.addEventListener("ended", notPlayingVideo, true);
    console.log('events set');
});

var substract5SecsFunction;

/**
 * Make a request to subtract 5 secs on the user balance every 5 secs
 */
function playingVideo (){
    substract5SecsFunction = setTimeout(function(){
        console.log('subtract 5 secs');
        $.ajax({
            url: $('#spendPurchasedTimeURL').val(),
            method: "GET"
        });

    }, 5000)
}

/**
 * Cancel the request of subtract seconds
 */
function notPlayingVideo (){
    console.log('subtract 5 secs CANCELED');
    clearTimeout(substract5SecsFunction);
}