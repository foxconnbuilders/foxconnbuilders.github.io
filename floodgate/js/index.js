$(document).ready( function() {
    $('#main-barousel').barousel({
        manualCarousel: 0
    });
    $('.image').mouseenter(function(){
        $(this).find('.initial-overlay').fadeOut("slow");
        $(this).find('.content-txt').fadeIn("slow");
        $(this).find('.visit-button-div').fadeIn("slow");
        $(this).find('.overlay').fadeIn("slow");
    }).mouseleave(function(){
        $(this).find('.content-txt').fadeOut("slow");
        $(this).find('.visit-button-div').fadeOut("slow");
        $(this).find('.overlay').fadeOut("slow");
        $(this).find('.initial-overlay').fadeIn("slow");
    });
    $('.main-article').mouseenter(function(){
        $('.barousel_image').removeClass('out-blur').addClass('on-blur');
    }).mouseleave(function(){
        $('.barousel_image').removeClass('on-blur').addClass('out-blur');
    });
});

$(window).on('load',function(){
    var bHeight = $('.barousel').find('.default').css('height');
    bHeight = parseInt(bHeight);
    $('.barousel').css('min-height', bHeight+"px");

});