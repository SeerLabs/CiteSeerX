// ------------------------------------------------------------------------------------------
// Repeat Behavior
// ------------------------------------------------------------------------------------------
      
   if(wFORMS) {
		// Component properties 
		wFORMS.className_repeat 			= "repeat";
		wFORMS.className_delete 			= "removeable";
		wFORMS.className_duplicateLink 		= "duplicateLink";
		wFORMS.className_removeLink 		= "removeLink";
		wFORMS.className_preserveRadioName  = "preserveRadioName";		
		wFORMS.idSuffix_repeatCounter		= "-RC";
		wFORMS.idSuffix_duplicateLink		= "-wfDL";									 
		wFORMS.preserveRadioName			= false;									 // if true, Repeat behavior will preserve name attributes for radio input. 
		wFORMS.limitSwitchScope				= true;									 	 // if true, Repeat behavior will limit the scope of nested switches.
		
		if(!wFORMS.arrMsg) wFORMS.arrMsg 	= new Array();
		wFORMS.arrMsg[0] 					= "Add another response"; 					 // repeat link
		wFORMS.arrMsg[1] 					= "Will duplicate this question or section." // title attribute on the repeat link 
		wFORMS.arrMsg[2] 					= "Remove"; 								 // remove link
		wFORMS.arrMsg[3] 					= "Will remove this question or section." 	 // title attribute on the remove link
		
		wFORMS.behaviors['repeat'] = {
			
			onRepeat: null, /* Function to run after the element is repeated */
			onRemove: null, 	/* Function to run after the element is removed  */
			allowRepeat: null, /* Function for fine control on repeatable section */
			
		   	// ------------------------------------------------------------------------------------------
		   	// evaluate: check if the behavior applies to the given node. Adds event handlers if appropriate
		   	// ------------------------------------------------------------------------------------------
			evaluate: function(node) {
				
				// Repeatable element
				if(wFORMS.helpers.hasClass(node, wFORMS.className_repeat)) {
					//wFORMS.debug('evaluate/repeat: '+ node.id,3);
				   
					 if(!node.id) 
						node.id = wFORMS.helpers.randomId();
						
					// Check if we have a 'repeat' link
					var repeatLink = document.getElementById(node.id + wFORMS.idSuffix_duplicateLink);
					if(!repeatLink) {				
						// create the repeat link
						repeatLink = wFORMS.behaviors['repeat'].createRepeatLink(node.id);
												
						// find where to insert the link
						if(node.tagName.toUpperCase()=="TR") {
							// find the last TD
							var n = node.lastChild;	
							while(n && n.nodeType != 1)  
								n = n.previousSibling;
							if(n && n.nodeType == 1) 
								n.appendChild(repeatLink);
							// Else Couldn't find the TD. Table row malformed ?
						} else
							node.appendChild(repeatLink);
					}
					// Add hidden counter field if necessary
					var counterField = document.getElementById(node.id + wFORMS.idSuffix_repeatCounter);
					if(!counterField) {
						// IE Specific :-(
						if(document.all && !window.opera) { 
							// see http://msdn.microsoft.com/workshop/author/dhtml/reference/properties/name_2.asp
							var counterFieldId = node.id + wFORMS.idSuffix_repeatCounter;
							if(navigator.appVersion.indexOf("MSIE") != -1 && navigator.appVersion.indexOf("Windows") == -1) // IE5 Mac
								counterField   = document.createElement("INPUT NAME=\"" + counterFieldId + "\"");
							else
								counterField   = document.createElement("<INPUT NAME=\"" + counterFieldId + "\"></INPUT>"); 					
							counterField.type  ='hidden';
							counterField.id    = counterFieldId; 
							counterField.value = "1";
						}
						else {
							counterField = document.createElement("INPUT"); 
							counterField.setAttribute('type','hidden'); // hidden
							counterField.setAttribute('value','1');
							counterField.setAttribute('name', node.id + wFORMS.idSuffix_repeatCounter);
							counterField.setAttribute('id', node.id + wFORMS.idSuffix_repeatCounter); 
						}
						
						// get the form element						
						var form = node.parentNode;
						while(form && form.tagName.toUpperCase() != "FORM")
							form = form.parentNode;
						
						form.appendChild(counterField);
					}
					
					// Add event handler			
					wFORMS.helpers.addEvent(repeatLink,'click',wFORMS.behaviors['repeat'].duplicateFieldGroup);			
				}	
		 	  	// ------------------------------------------------------------------------------------------
				// Removeable element
				if(wFORMS.helpers.hasClass(node, wFORMS.className_delete)) {
					var removeLink = wFORMS.behaviors['repeat'].createRemoveLink();
					// find where to insert the link
					if(node.tagName.toUpperCase()=="TR") {
						// find the last TD
						var n = node.lastChild;	
						while(n && n.nodeType != 1)  
							n = n.previousSibling;
						if(n && n.nodeType == 1) 
							n.appendChild(removeLink);
						// Else Couldn't find the TD. Table row malformed ?
					} else
						node.appendChild(removeLink);
				}	
           	},
			createRepeatLink: function(id) {
				var repeatLink = document.createElement("a"); 
				var spanNode = document.createElement("span");  // could be used for CSS image replacement 
				var textNode = document.createTextNode(wFORMS.arrMsg[0]);
				repeatLink.id = id + wFORMS.idSuffix_duplicateLink;	
				repeatLink.setAttribute('href',"#");	
				repeatLink.className = wFORMS.className_duplicateLink;			
				repeatLink.setAttribute('title', wFORMS.arrMsg[1]);	
				spanNode.appendChild(textNode); 
				repeatLink.appendChild(spanNode); 
				return repeatLink;
			},
			createRemoveLink: function() {
				var removeLink = document.createElement("a");
				var spanNode   = document.createElement("span");  // could be used for CSS image replacement 
				var textNode   = document.createTextNode(wFORMS.arrMsg[2]);
				removeLink.setAttribute('href',"#");	
				removeLink.className = wFORMS.className_removeLink;
				removeLink.setAttribute('title',wFORMS.arrMsg[3]);	
				spanNode.appendChild(textNode); 
				removeLink.appendChild(spanNode);
				wFORMS.helpers.addEvent(removeLink,'click',wFORMS.behaviors['repeat'].removeFieldGroup);
				return removeLink;
			},
		   	duplicateFieldGroup: function(e) {
		   					
				var element  = wFORMS.helpers.getSourceElement(e);
				if(!element) element = e
				
				// override of the wFORMS.preserveRadioName property using a class on the repeat link.
				var preserveRadioName = wFORMS.helpers.hasClass(element,wFORMS.className_preserveRadioName) ? true : wFORMS.preserveRadioName;
				//wFORMS.debug('preserveRadioName='+preserveRadioName);
				
				// Get Element to duplicate.				
				while (element && !wFORMS.helpers.hasClass(element,wFORMS.className_duplicateLink)) {
					element = element.parentNode;
				}	
				var idOfRepeatedSection = element.id.replace(wFORMS.idSuffix_duplicateLink,"");
				var element = document.getElementById(idOfRepeatedSection); 
				
				
				if (element) {
					var wBehavior = wFORMS.behaviors['repeat']; // shortcut
					
					// Check if we have a custom function that prevents the repeat
					if(wBehavior.allowRepeat) {						
						if(!wBehavior.allowRepeat(element)) return false;
					}
					
					// Extract row counter information
					counterField = document.getElementById(element.id + wFORMS.idSuffix_repeatCounter);
					if(!counterField) return; // should not happen.
					var rowCount = parseInt(counterField.value) + 1;
					// Prepare id suffix
					var suffix = "-" + rowCount.toString()
					// duplicate node tree 
					var dupTree = wBehavior.replicateTree(element, null, suffix, preserveRadioName);  //  sourceNode.cloneNode(true); 
					// find insert point in DOM tree (after existing repeated element)
					var insertNode = element.nextSibling;
					
					while(insertNode && 
						 (insertNode.nodeType==3 ||       // skip text-node that can be generated server-side when populating a previously repeated group 
						  wFORMS.helpers.hasClass(insertNode,wFORMS.className_delete))) {						
						insertNode = insertNode.nextSibling;
					}
					element.parentNode.insertBefore(dupTree,insertNode);	 // Buggy rendering in IE5/Mac
					// if(navigator.appVersion.indexOf("MSIE") != -1 && navigator.appVersion.indexOf("Windows") == -1)			
					//
					
					// the copy is not duplicable, it's removeable
					dupTree.className = element.className.replace(wFORMS.className_repeat,wFORMS.className_delete);
					// Save new row count 			
					document.getElementById(element.id + wFORMS.idSuffix_repeatCounter).value = rowCount;
					// re-add wFORMS behaviors
					wFORMS.addBehaviors(dupTree);
					
					if(wBehavior.onRepeat)
						wBehavior.onRepeat(element,dupTree);
				}				
				
				return wFORMS.helpers.preventEvent(e);
			},
			
		   	removeFieldGroup: function(e) { 
				var element  = wFORMS.helpers.getSourceElement(e);
				if(!element) element = e
				// Get Element to remove.
				var element = element.parentNode;
				while (element && !wFORMS.helpers.hasClass(element,wFORMS.className_delete)) {
					element = element.parentNode;
				}	
				element.parentNode.removeChild(element);
				if(wFORMS.behaviors['repeat'].onRemove)
						wFORMS.behaviors['repeat'].onRemove(element);
				return wFORMS.helpers.preventEvent(e);
			},	
			
			removeRepeatCountSuffix: function(str) {
				return str.replace(/-\d+$/,'');
			},
	
			replicateTree: function(element,parentElement, idSuffix, preserveRadioName) {
				
				// Duplicating TEXT-NODE (do not copy value of textareas)
				if(element.nodeType==3) { 
					if(element.parentNode.tagName.toUpperCase() != 'TEXTAREA')
						var newElement = document.createTextNode(element.data); 
				} 
				// Duplicating ELEMENT-NODE
				else if(element.nodeType==1) { 
					
					// Do not copy repeat/remove links
					if(wFORMS.helpers.hasClass(element,wFORMS.className_duplicateLink) ||
					   wFORMS.helpers.hasClass(element,wFORMS.className_removeLink)) 							
						return null; 
					// Exclude duplicated elements of a nested repeat group
					if(wFORMS.helpers.hasClass(element,wFORMS.className_delete)) 
						return null; 
					// Adjust row suffix id if we find a nested repeat group 
					if(wFORMS.helpers.hasClass(element,wFORMS.className_repeat) && parentElement!=null)
						idSuffix = idSuffix.replace('-','__');
					
					if(!document.all || window.opera) { 
						// Common Branch
						var newElement = document.createElement(element.tagName); 
					} else {
						// IE Branch 
						// see http://msdn.microsoft.com/workshop/author/dhtml/reference/properties/name_2.asp						
						var tagHtml = element.tagName;
						
						if(element.name) 					
							if (element.tagName.toUpperCase() == "INPUT" && 
								element.type.toLowerCase()    == "radio" && preserveRadioName)
								tagHtml += " NAME='" + element.name + "' ";
							else
								tagHtml += " NAME='" + wFORMS.behaviors['repeat'].removeRepeatCountSuffix(element.name) + idSuffix + "' ";
						if(element.type) {
							tagHtml += " TYPE='" + element.type + "' ";
						}
						if(element.selected) 
							tagHtml += " SELECTED='SELECTED' ";
						if(element.checked)
							tagHtml += " CHECKED='CHECKED' ";
	
						if(navigator.appVersion.indexOf("MSIE") != -1 && navigator.appVersion.indexOf("Windows") == -1) // IE5 Mac
							var newElement = document.createElement(tagHtml);
						else
							var newElement = document.createElement("<" + tagHtml + "></"+ element.tagName + ">"); 
						try { newElement.type = element.type; } catch(e) {}; // nail it down for IE5 ?, breaks in IE6
						
					}
				 
					// duplicate attributes										
					for(var i=0; i< element.attributes.length; i++) {
						var attribute = element.attributes[i];
						
						// Get attribute value. 
						if(	attribute.specified || // in IE, the attributes array contains all attributes in the DTD
							attribute.nodeName.toLowerCase() == 'value' ) { // attr.specified buggy in IE?  
							// Add the row suffix if necessary.
							if(	attribute.nodeName.toLowerCase() == "id" || 
								attribute.nodeName.toLowerCase() == "name" ||
								attribute.nodeName.toLowerCase() == "for") {
															
								if(wFORMS.hasBehavior('hint') && 
								   attribute.nodeValue.indexOf(wFORMS.idSuffix_fieldHint) != -1)  {
									//leave the field hint suffix at the end of the id.
									var value = attribute.nodeValue;
									value= wFORMS.behaviors['repeat'].removeRepeatCountSuffix(value.substr(0,value.indexOf(wFORMS.idSuffix_fieldHint))) + idSuffix + wFORMS.idSuffix_fieldHint;
								}
								else {
									if(element.tagName.toUpperCase()=="INPUT" && 
									   element.getAttribute('type',false).toLowerCase()=="radio" &&
									   attribute.nodeName.toLowerCase() == "name" && 
									   preserveRadioName) {
										var value = attribute.nodeValue;						
									}
									else {
										// var value = wFORMS.behaviors['repeat'].removeRepeatCountSuffix(attribute.nodeValue) + idSuffix;
										var value = attribute.nodeValue + idSuffix;
									}
								}
							} else {
								// Do not copy the value attribute for text/password/file input
								if(attribute.nodeName.toLowerCase() == "value" &&
								   element.tagName.toUpperCase()=='INPUT'      &&  
								  (element.type.toLowerCase() == 'text'     || 
								   element.type.toLowerCase() == 'password' || 
								   element.type.toLowerCase() == 'hidden' ||
								   element.type.toLowerCase() == 'file')) 
									var value='';   
								// Do not copy the switch behavior's 'event handled' flag, stored in the rel attribute
								else if(attribute.nodeName.toLowerCase() == "rel" && 
										attribute.nodeValue.indexOf('wfHandled') != -1) {
									var value = attribute.nodeValue.replace('wfHandled','');
								} else 
									var value = attribute.nodeValue;
							}
							// Create attribute and assign value
							switch(attribute.nodeName.toLowerCase()) {
								case "class":
									newElement.className = value; 
									break;
								case "style": // inline style attribute (fix for IE)
									if(element.style && element.style.cssText) 
										newElement.style.cssText = element.style.cssText; 
									break;								
								case "onclick": // inline event handler (fix for IE)
									newElement.onclick     = element.onclick;							
									break;							
								case "onchange":							
									newElement.onchange    = element.onchange;							
									break;							
								case "onsubmit":
									newElement.onsubmit    = element.onsubmit;							
									break;							
								case "onmouseover":							
									newElement.onmouseover = element.onmouseover;							
									break;							
								case "onmouseout":							
									newElement.onmouseout  = element.onmouseout;							
									break;							
								case "onmousedown":
									newElement.onmousedown = element.onmousedown;							
									break;							
								case "onmouseup":
									newElement.onmouseup   = element.onmouseup;							
									break;							
								case "ondblclick":
									newElement.ondblclick  = element.ondblclick;							
									break;							
								case "onkeydown":
									newElement.onkeydown   = element.onkeydown;							
									break;							
								case "onkeyup":
									newElement.onkeyup     = element.onkeyup;							
									break;							
								case "onblur": 
									newElement.onblur      = element.onblur;							
									break;							
								case "onfocus":
									newElement.onfocus     = element.onfocus;							
									break;
								default:
									newElement.setAttribute(attribute.name, value, 0);
							}
						}
					}				
				}
				if(parentElement && newElement) 
					parentElement.appendChild(newElement);
				for(var i=0; i<element.childNodes.length;i++) {
					wFORMS.behaviors['repeat'].replicateTree(element.childNodes[i],newElement,idSuffix, preserveRadioName);
				}
				return newElement;
			}
       } // End wFORMS.behaviors['repeat']
	   

   }
