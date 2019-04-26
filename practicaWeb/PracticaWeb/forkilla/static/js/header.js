$.global = new Object();
$.global.item = 1;
$.global.total = 0;

$(document).ready(function() {
    $(function () {
        var isopen_username = false;
        
        /*
         * Open and close username event
         */
         $("header .login-section .user_ui .user_menu_toggle").on("click", function(){
             if (!isopen_username) {
                 
                 //Show menu
                 $("header .user_menu").show();
                 
                 //change arrow
                 $("header .simple_arrow").removeClass("fa-chevron-down").addClass("fa-chevron-up");
                 
                 isopen_username = true;
                 
             } else {
                 //Close nenu
                 $("header .user_menu").hide();
                 
                 //change arrow
                 $("header .simple_arrow").removeClass("fa-chevron-up").addClass("fa-chevron-down");
                 
                 isopen_username = false;
             }
         });
    });
    
    $(function() {
        var windowWidth = $(window).width();
        var slideCount = $('.slides li').length;
        var slidesWidth = windowWidth * slideCount;
        
        $('.slides li')
        
        $.global.item = 0;
        var slideCount
    });
});