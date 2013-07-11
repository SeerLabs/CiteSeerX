$(document).ready( 
	function() {
    	$('input#unpublishAll').click(function(){
     		var checkAllState = this.checked;
		$("input[name=unpublish]").each(function() {
			this.checked = checkAllState;
		});
	    });
	$('input#dmcaAll').click(function(){
		var checkAllState = this.checked;
		$("input[name=dmcaed]").each(function() {
			this.checked = checkAllState;
		});
	});
});
