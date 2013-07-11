$(function() {

    var $el, leftPos, newWidth,
        $searchNav = $("#search_nav");

    $searchNav.append("<li id='activeLine'></li>");
    var $activeLine = $("#activeLine");

    if ($(".active")) {
    $activeLine
        .width($(".active").width())
        .css("left", $(".active").find("a").position().left)
        .data("origLeft", $activeLine.position().left)
        .data("origWidth", $activeLine.width());
    }
    else {
    $activeLine
        .data("origLeft", $activeLine.position().left)
        .data("origWidth", $activeLine.width());
    }
    
    $("#search_nav li").find("a").hover(function() {
        $el = $(this);
        leftPos = $el.position().left;
        newWidth = $el.parent().width();
        $activeLine.stop().animate({
            left: leftPos,
            width: newWidth
        });
        
    $("#search_nav li").find("a").click(function() {
    	$(this).parent().siblings().removeClass("active");
    	$(this).parent().addClass("active");
    	$activeLine
    	.width($(".active").width())
    	.css("left", $(".active a").position().left)
    	.data("origLeft", $activeLine.position().left)
    	.data("origWidth", $activeLine.width());
    });
    
    }, function() {
        $activeLine.stop().animate({
            left: $activeLine.data("origLeft"),
            width: $activeLine.data("origWidth")
        });
    });
});