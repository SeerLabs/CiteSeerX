/**
 * Script for show/hide citation context functionality.  Show/hide is
 * based on a script with the original copyright:
 * (C) www.dhtmlgoodies.com, November 2005
 * by Alf Magne Kalleland, with customizations by IGC
 *
 * Ajax through DWR.
 *
 * Isaac Councill
 *
 */
var dhtmlgoodies_slideSpeed = 10;	// Higher value = faster
var dhtmlgoodies_timer = 10;	// Lower value = faster

var objectIdToSlideDown = false;
var dhtmlgoodies_activeId = false;
var dhtmlgoodies_slideInProgress = false;
function showHideContent(inputId)
{
 if(dhtmlgoodies_slideInProgress)return;
 dhtmlgoodies_slideInProgress = true;
 if(!inputId)inputId = this.id;
 inputId = inputId + '';
 var numericId = inputId.replace(/[^0-9]/g,'');
 var answerDiv = document.getElementById('context_' + numericId);

 objectIdToSlideDown = false;

 if(!answerDiv.style.display || answerDiv.style.display=='none'){		
  answerDiv.style.display='block';
  answerDiv.style.visibility = 'visible';		
  slideContent(numericId,dhtmlgoodies_slideSpeed);
 }else{
  slideContent(numericId,(dhtmlgoodies_slideSpeed*-1));
  dhtmlgoodies_activeId = false;
 }	
}

function slideContent(inputId,direction)
{
 var obj =document.getElementById('context_' + inputId);
 var contentObj = document.getElementById('context_cont_' + inputId);
 height = obj.clientHeight;
 if(height==0)height = obj.offsetHeight;
 height = height + direction;
 rerunFunction = true;
 if(height>contentObj.offsetHeight){
  height = contentObj.offsetHeight;
  rerunFunction = false;
 }
 if(height<=1){
  height = 1;
  rerunFunction = false;
 }
 obj.style.height = height + 'px';
 var topPos = height - contentObj.offsetHeight;
 if(topPos>0)topPos=0;
 contentObj.style.top = topPos + 'px';
 if(rerunFunction){
  setTimeout('slideContent(' + inputId + ',' + direction + ')',dhtmlgoodies_timer);
 }else{
  if(height<=1){
   obj.style.display='none'; 
   if(objectIdToSlideDown && objectIdToSlideDown!=inputId){
    document.getElementById('context_' + objectIdToSlideDown).style.display='block';
    document.getElementById('context_' + objectIdToSlideDown).style.visibility='visible';
    slideContent(objectIdToSlideDown,dhtmlgoodies_slideSpeed);				
   }else{
    dhtmlgoodies_slideInProgress = false;
   }
  }else{
   dhtmlgoodies_activeId = inputId;
   dhtmlgoodies_slideInProgress = false;
  }
 }
}

DWREngine.setErrorHandler(null);
DWREngine.setWarningHandler(null);

function showContextField(citing, cited) {
 var id = "context_cont_"+citing;
 if (document.getElementById(id).innerHTML == '') {
  GetContextJS.getContext(citing, cited,
   { callback:function(dataFromServer) {
      document.getElementById(id).innerHTML = dataFromServer;
      showHideContent(id);
     }
   });
 } else {
  showHideContent(id);
 }
}
