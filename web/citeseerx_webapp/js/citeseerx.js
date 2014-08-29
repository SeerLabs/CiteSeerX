$(function() {
  $('#search_box label').css({
  	'display': 'none',
  	'visibility': 'hidden'
	});
	$('#search label').css({
  	'display': 'none',
  	'visibility': 'hidden'
	});
	$(".s_field").focus();
	$("#search_nav").idTabs();
	$('.s_field').focusout(function() {
	  var sterm = $(this).val();
	  $('.s_field').each(function() {
	    $(this).val(sterm);
	  });
	});
	$(".abstract_toggle").click(function(){
    $(this).siblings(".pubabstract").slideToggle("slow");
  });
});

function toggleCitation(citationNo){
    if (document.getElementById(citationNo).style.display == "none"){
        document.getElementById(citationNo).style.display = "block";
    }
    else{
        document.getElementById(citationNo).style.display = "none";
    }
}