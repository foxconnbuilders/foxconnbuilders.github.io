var fs = fs || {};
fs.fsMainFunction = function() {
    fs.iconAnimation();
};

fs.iconAnimation = function() {
    $('#icon-transition').on('click', function () {
        $(this).toggleClass('open');
    });
}

fs.scrollRevealItems = function(){
    window.sr = new scrollReveal();
}



$(function(){
    fs.fsMainFunction();
});