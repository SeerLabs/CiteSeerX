/**
* Utilities for copying, adding, and removing authors on the document
* correction form. - Isaac Councill
*/
repeatCounter_id = "repeatCounter";

function repeat(blockid) {
 var counterNode = document.getElementById(repeatCounter_id);
 var counter = counterNode.innerHTML;
 
 var block = document.getElementById(blockid);
 block.appendChild(createAuthorElement(counter));
 counterNode.innerHTML = parseInt(counter)+1;
}

function createAuthorElement(num) {
 var authElt = document.createElement("fieldset");
 authElt.setAttribute("id", "author_"+num);

 var moveSpan = document.createElement("span");
 moveSpan.className="removeLink";

 var upSpan = document.createElement("span");
 upSpan.className="actionspan";
 upSpan.setAttribute("onClick", 'moveAuthor('+num+', "up")');
 upSpan.appendChild(document.createTextNode("Move Up"));

 var downSpan = document.createElement("span");
 downSpan.className="actionspan";
 downSpan.setAttribute("onClick", 'moveAuthor('+num+', "down")');
 downSpan.appendChild(document.createTextNode("Move Down"));

 moveSpan.appendChild(upSpan);
 moveSpan.appendChild(document.createTextNode(" | "));
 moveSpan.appendChild(downSpan);
 authElt.appendChild(moveSpan);
 
 var fields = ["name", "affil", "address", "email"];
 var fieldLabels = ["Name:", "Affiliation:", "Address:", "Email:"];
 var reqFields = [true, false, false, false];
 for (i=0; i<fields.length; i++) {
  var inputID = "authors["+num+"]."+fields[i];
  var field = document.createElement("span");
  field.className="oneField";

  var label = document.createElement("label");
  label.setAttribute("for", inputID);
  label.className="preField";
  label.appendChild(document.createTextNode(fieldLabels[i]));
  field.appendChild(label);
  
  if (reqFields[i]) {
   label.appendChild(document.createTextNode(" "));
   var req = document.createElement("span");
   req.className="reqMark";
   req.appendChild(document.createTextNode("*"));
   label.appendChild(req);
  }

  var input = document.createElement("input");
  input.setAttribute("type", "text");
  input.setAttribute("size", "40");
  input.setAttribute("id", inputID);
  input.setAttribute("name", inputID);
  input.setAttribute("value", "");
  field.appendChild(input);
  authElt.appendChild(field);
 }

 var ordInput = document.createElement("input");
 ordInput.setAttribute("type", "hidden");
 ordInput.setAttribute("id", "authors["+num+"].order");
 ordInput.setAttribute("name", "authors["+num+"].order");
 ordInput.setAttribute("value", parseInt(num)+1);
 authElt.appendChild(ordInput);

 var delInput = document.createElement("input");
 delInput.setAttribute("type", "hidden");
 delInput.setAttribute("id", "authors["+num+"].deleted");
 delInput.setAttribute("name", "authors["+num+"].deleted");
 delInput.setAttribute("value", "false");
 authElt.appendChild(delInput);

 var delSection = document.createElement("span");
 delSection.className="duplicateLink actionspan";
 delSection.setAttribute("onclick", 'deleteSection("author_'+num+'", "authors['+num+'].deleted")');
 delSection.appendChild(document.createTextNode("Remove This Author"));
 authElt.appendChild(delSection);
 
 return authElt;
}
 
function deleteSection(sectionid, deleteid) {
 var section = document.getElementById(sectionid);
 section.style.display = "none";
 document.getElementById(deleteid).value = "true";
}

function replaceAll(str, from, to) {
 var i=str.indexOf(from);
 while(i>-1) {
  str=str.replace(from,to);
  i=str.indexOf(from);
 }
 return str;
}

function moveAuthor(authNum, direction) {
 var counter = parseInt(document.getElementById(repeatCounter_id).innerHTML);
 var to = authNum;
 if (direction=='up' && authNum>0) {
  while(--to>=0) {
   var delid = 'authors['+to+'].deleted';
   try { if(document.getElementById(delid).value=='false') break;
   } catch (err) { return; }
  }
 }
 if (direction=='down') {
  while(++to<counter) {
   var id = 'authors['+to+'].deleted';
   try { if(document.getElementById(id).value=='false') break;
   } catch (err) { }
  }
  if (to==counter) repeat('repeat_block');
 }
 if (to != authNum) {
  var fromElts = ['authors['+authNum+'].name','authors['+authNum+'].affil',
                  'authors['+authNum+'].address','authors['+authNum+'].email',
                  'authors['+authNum+'].deleted'];
  var toElts = ['authors['+to+'].name','authors['+to+'].affil',
                  'authors['+to+'].address','authors['+to+'].email',
                  'authors['+to+'].deleted'];
  var tmp;
  for (i=0; i<fromElts.length; i++) {
   tmp = document.getElementById(toElts[i]).value;
   document.getElementById(toElts[i]).value = document.getElementById(fromElts[i]).value;
   document.getElementById(fromElts[i]).value = tmp;
  }
 }
}
  
