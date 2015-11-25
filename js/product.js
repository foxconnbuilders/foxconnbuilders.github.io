var foxconn = foxconn || {};
foxconn.shutterFunction = function() {
    foxconn.videoButton();
};

foxconn.videoButton = function(){
    var videoTopButton = $('#video-top-button');
    var videoTopContainer = $('#video-top-section .video-container');
    var videoTopMenuContainer = $('#video-top-section .video-container #video-menu-container');

    var videoBottomButton = $('#video-bottom-button');
    var videoBottomContainer = $('#video-bottom-section .video-container');
    var videoBottomMenuContainer = $('#video-bottom-section .video-container #video-menu-container');

    videoTopButton.on('click', function(){
        videoTopContainer.addClass('video-on-show');
        videoTopMenuContainer.css('display','none');
        videoTopContainer.append('<iframe src=https://player.vimeo.com/video/146864107?autoplay=1" width="100%" height="500px" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe> ');
    });

    videoBottomButton.on('click', function(){
        videoBottomContainer.addClass('video-on-show');
        videoBottomMenuContainer.css('display','none');
        videoBottomContainer.append('<iframe src="https://player.vimeo.com/video/142647996?autoplay=1" width="500" height="281" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>');
    });
};

$(function(){
    foxconn.shutterFunction();
})
