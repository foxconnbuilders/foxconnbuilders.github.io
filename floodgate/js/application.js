var $height = $(window).height();
var $width = $(window).width();

$(document).ready( function() {
    var navbarBrand = $('#nav-bar .brand img');
    $('#barousel-foxconn').barousel({
        navType: 2
    });
    $('.barousel_nav').hide();

    $('#carousel-bottom').css('height',($height-80)+"px");
    $(document).on('click','#learn-button',function(){
        event.preventDefault();
        $('html,body').animate({scrollTop: $('.product').offset().top - 80},'slow');
    });

    navbarBrand.on('click', function(){
        window.location = '/';
    });
});

$(window).on('load',function(){
    var bHeight = $('.barousel_image').find('.default').css('height');
    bHeight = parseInt(bHeight);
    $('.barousel').css('min-height', bHeight+"px");
    $('.barousel_nav').css('top',0).fadeIn();

    $('.product').fadeIn();
    $('.project').fadeIn();
    $('.contact').fadeIn();
    $('.subMenu').smint({
        'scrollSpeed' : 1000,
        'mySelector': '#nav-bar'
    });
    $('#nav-bar').fadeIn();
    $('#learn').fadeIn();
});

$(function() {
    $('a[href*=#]:not([href=#carousel-bottom])').click(function() {
        if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
            if (target.length) {
                $('html,body').animate({
                    scrollTop: target.offset().top-70
                }, 1000);
                return false;
            }
        }
    });
});