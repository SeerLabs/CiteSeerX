
function MetadataCartJS() { }
MetadataCartJS._path = '/dwr';

MetadataCartJS.addToCart = function(p0, callback) {
    DWREngine._execute(MetadataCartJS._path, 'MetadataCartJS', 'addToCart', p0, callback);
}

MetadataCartJS.addToCart = function(p1, callback) {
    DWREngine._execute(MetadataCartJS._path, 'MetadataCartJS', 'addToCart', false, p1, callback);
}

MetadataCartJS.removeFromCart = function(p0, callback) {
    DWREngine._execute(MetadataCartJS._path, 'MetadataCartJS', 'removeFromCart', p0, callback);
}

MetadataCartJS.removeFromCart = function(p1, callback) {
    DWREngine._execute(MetadataCartJS._path, 'MetadataCartJS', 'removeFromCart', false, p1, callback);
}


function GetContextJS() { }
GetContextJS._path = '/dwr';

GetContextJS.getContext = function(p0, p1, callback) {
    DWREngine._execute(GetContextJS._path, 'GetContextJS', 'getContext', p0, p1, callback);
}




















if (DWREngine == null) var DWREngine = {};





DWREngine.setErrorHandler = function(handler) {
DWREngine._errorHandler = handler;
};





DWREngine.setWarningHandler = function(handler) {
DWREngine._warningHandler = handler;
};





DWREngine.setTimeout = function(timeout) {
DWREngine._timeout = timeout;
};





DWREngine.setPreHook = function(handler) {
DWREngine._preHook = handler;
};





DWREngine.setPostHook = function(handler) {
DWREngine._postHook = handler;
};


DWREngine.XMLHttpRequest = 1;


DWREngine.IFrame = 2;






DWREngine.setMethod = function(newMethod) {
if (newMethod != DWREngine.XMLHttpRequest && newMethod != DWREngine.IFrame) {
DWREngine._handleError("Remoting method must be one of DWREngine.XMLHttpRequest or DWREngine.IFrame");
return;
}
DWREngine._method = newMethod;
};





DWREngine.setVerb = function(verb) {
if (verb != "GET" && verb != "POST") {
DWREngine._handleError("Remoting verb must be one of GET or POST");
return;
}
DWREngine._verb = verb;
};





DWREngine.setOrdered = function(ordered) {
DWREngine._ordered = ordered;
};





DWREngine.setAsync = function(async) {
DWREngine._async = async;
};





DWREngine.setTextHtmlHandler = function(handler) {
DWREngine._textHtmlHandler = handler;
}





DWREngine.defaultMessageHandler = function(message) {
if (typeof message == "object" && message.name == "Error" && message.description) {
alert("Error: " + message.description);
}
else {

if (message.toString().indexOf("0x80040111") == -1) {
alert(message);
}
}
};





DWREngine.beginBatch = function() {
if (DWREngine._batch) {
DWREngine._handleError("Batch already started.");
return;
}

DWREngine._batch = {
map:{ callCount:0 },
paramCount:0,
ids:[],
preHooks:[],
postHooks:[]
};
};





DWREngine.endBatch = function(options) {
var batch = DWREngine._batch;
if (batch == null) {
DWREngine._handleError("No batch in progress.");
return;
}

if (options && options.preHook) batch.preHooks.unshift(options.preHook);
if (options && options.postHook) batch.postHooks.push(options.postHook);
if (DWREngine._preHook) batch.preHooks.unshift(DWREngine._preHook);
if (DWREngine._postHook) batch.postHooks.push(DWREngine._postHook);

if (batch.method == null) batch.method = DWREngine._method;
if (batch.verb == null) batch.verb = DWREngine._verb;
if (batch.async == null) batch.async = DWREngine._async;
if (batch.timeout == null) batch.timeout = DWREngine._timeout;

batch.completed = false;


DWREngine._batch = null;



if (!DWREngine._ordered) {
DWREngine._sendData(batch);
DWREngine._batches[DWREngine._batches.length] = batch;
}
else {
if (DWREngine._batches.length == 0) {

DWREngine._sendData(batch);
DWREngine._batches[DWREngine._batches.length] = batch;
}
else {

DWREngine._batchQueue[DWREngine._batchQueue.length] = batch;
}
}
};






DWREngine._errorHandler = DWREngine.defaultMessageHandler;


DWREngine._warningHandler = null;


DWREngine._preHook = null;


DWREngine._postHook = null;


DWREngine._batches = [];


DWREngine._batchQueue = [];


DWREngine._handlersMap = {};


DWREngine._method = DWREngine.XMLHttpRequest;


DWREngine._verb = "POST";


DWREngine._ordered = false;


DWREngine._async = true;


DWREngine._batch = null;


DWREngine._timeout = 0;


DWREngine._DOMDocument = ["Msxml2.DOMDocument.6.0", "Msxml2.DOMDocument.5.0", "Msxml2.DOMDocument.4.0", "Msxml2.DOMDocument.3.0", "MSXML2.DOMDocument", "MSXML.DOMDocument", "Microsoft.XMLDOM"];


DWREngine._XMLHTTP = ["Msxml2.XMLHTTP.6.0", "Msxml2.XMLHTTP.5.0", "Msxml2.XMLHTTP.4.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];










DWREngine._execute = function(path, scriptName, methodName, vararg_params) {
var singleShot = false;
if (DWREngine._batch == null) {
DWREngine.beginBatch();
singleShot = true;
}

var args = [];
for (var i = 0; i < arguments.length - 3; i++) {
args[i] = arguments[i + 3];
}

if (DWREngine._batch.path == null) {
DWREngine._batch.path = path;
}
else {
if (DWREngine._batch.path != path) {
DWREngine._handleError("Can't batch requests to multiple DWR Servlets.");
return;
}
}


var params;
var callData;
var firstArg = args[0];
var lastArg = args[args.length - 1];

if (typeof firstArg == "function") {
callData = { callback:args.shift() };
params = args;
}
else if (typeof lastArg == "function") {
callData = { callback:args.pop() };
params = args;
}
else if (lastArg != null && typeof lastArg == "object" && lastArg.callback != null && typeof lastArg.callback == "function") {
callData = args.pop();
params = args;
}
else if (firstArg == null) {



if (lastArg == null && args.length > 2) {
DWREngine._handleError("Ambiguous nulls at start and end of parameter list. Which is the callback function?");
}
callData = { callback:args.shift() };
params = args;
}
else if (lastArg == null) {
callData = { callback:args.pop() };
params = args;
}
else {
DWREngine._handleError("Missing callback function or metadata object.");
return;
}


var random = Math.floor(Math.random() * 10001);
var id = (random + "_" + new Date().getTime()).toString();
var prefix = "c" + DWREngine._batch.map.callCount + "-";
DWREngine._batch.ids.push(id);


if (callData.method != null) {
DWREngine._batch.method = callData.method;
delete callData.method;
}
if (callData.verb != null) {
DWREngine._batch.verb = callData.verb;
delete callData.verb;
}
if (callData.async != null) {
DWREngine._batch.async = callData.async;
delete callData.async;
}
if (callData.timeout != null) {
DWREngine._batch.timeout = callData.timeout;
delete callData.timeout;
}


if (callData.preHook != null) {
DWREngine._batch.preHooks.unshift(callData.preHook);
delete callData.preHook;
}
if (callData.postHook != null) {
DWREngine._batch.postHooks.push(callData.postHook);
delete callData.postHook;
}


if (callData.errorHandler == null) callData.errorHandler = DWREngine._errorHandler;
if (callData.warningHandler == null) callData.warningHandler = DWREngine._warningHandler;


DWREngine._handlersMap[id] = callData;

DWREngine._batch.map[prefix + "scriptName"] = scriptName;
DWREngine._batch.map[prefix + "methodName"] = methodName;
DWREngine._batch.map[prefix + "id"] = id;


for (i = 0; i < params.length; i++) {
DWREngine._serializeAll(DWREngine._batch, [], params[i], prefix + "param" + i);
}


DWREngine._batch.map.callCount++;
if (singleShot) {
DWREngine.endBatch();
}
};


DWREngine._sendData = function(batch) {

if (batch.map.callCount == 0) return;

for (var i = 0; i < batch.preHooks.length; i++) {
batch.preHooks[i]();
}
batch.preHooks = null;

if (batch.timeout && batch.timeout != 0) {
batch.interval = setInterval(function() { DWREngine._abortRequest(batch); }, batch.timeout);
}

var urlPostfix;
if (batch.map.callCount == 1) {
urlPostfix = batch.map["c0-scriptName"] + "." + batch.map["c0-methodName"] + ".dwr";
}
else {
urlPostfix = "Multiple." + batch.map.callCount + ".dwr";
}


if (batch.method == DWREngine.XMLHttpRequest) {
if (window.XMLHttpRequest) {
batch.req = new XMLHttpRequest();
}

else if (window.ActiveXObject && !(navigator.userAgent.indexOf("Mac") >= 0 && navigator.userAgent.indexOf("MSIE") >= 0)) {
batch.req = DWREngine._newActiveXObject(DWREngine._XMLHTTP);
}
}

var query = "";
var prop;


if (batch.req) {
batch.map.xml = "true";

if (batch.async) {
batch.req.onreadystatechange = function() { DWREngine._stateChange(batch); };
}

var indexSafari = navigator.userAgent.indexOf("Safari/");
if (indexSafari >= 0) {
var version = navigator.userAgent.substring(indexSafari + 7);
if (parseInt(version, 10) < 400) batch.verb == "GET";
}
if (batch.verb == "GET") {



batch.map.callCount = "" + batch.map.callCount;

for (prop in batch.map) {
var qkey = encodeURIComponent(prop);
var qval = encodeURIComponent(batch.map[prop]);
if (qval == "") DWREngine._handleError("Found empty qval for qkey=" + qkey);
query += qkey + "=" + qval + "&";
}

try {
batch.req.open("GET", batch.path + "/exec/" + urlPostfix + "?" + query, batch.async);
batch.req.send(null);
if (!batch.async) DWREngine._stateChange(batch);
}
catch (ex) {
DWREngine._handleMetaDataError(null, ex);
}
}
else {
for (prop in batch.map) {
if (typeof batch.map[prop] != "function") {
query += prop + "=" + batch.map[prop] + "\n";
}
}

try {
batch.req.open("POST", batch.path + "/exec/" + urlPostfix, batch.async);
batch.req.setRequestHeader('Content-Type', 'text/plain');
batch.req.send(query);
if (!batch.async) DWREngine._stateChange(batch);
}
catch (ex) {
DWREngine._handleMetaDataError(null, ex);
}
}
}
else {
batch.map.xml = "false";
var idname = "dwr-if-" + batch.map["c0-id"];

batch.div = document.createElement("div");
batch.div.innerHTML = "<iframe src='javascript:void(0)' frameborder='0' width='0' height='0' id='" + idname + "' name='" + idname + "'></iframe>";
document.body.appendChild(batch.div);
batch.iframe = document.getElementById(idname);
batch.iframe.setAttribute("style", "width:0px; height:0px; border:0px;");

if (batch.verb == "GET") {
for (prop in batch.map) {
if (typeof batch.map[prop] != "function") {
query += encodeURIComponent(prop) + "=" + encodeURIComponent(batch.map[prop]) + "&";
}
}
query = query.substring(0, query.length - 1);

batch.iframe.setAttribute("src", batch.path + "/exec/" + urlPostfix + "?" + query);
document.body.appendChild(batch.iframe);
}
else {
batch.form = document.createElement("form");
batch.form.setAttribute("id", "dwr-form");
batch.form.setAttribute("action", batch.path + "/exec" + urlPostfix);
batch.form.setAttribute("target", idname);
batch.form.target = idname;
batch.form.setAttribute("method", "POST");
for (prop in batch.map) {
var formInput = document.createElement("input");
formInput.setAttribute("type", "hidden");
formInput.setAttribute("name", prop);
formInput.setAttribute("value", batch.map[prop]);
batch.form.appendChild(formInput);
}
document.body.appendChild(batch.form);
batch.form.submit();
}
}
};


DWREngine._stateChange = function(batch) {
if (!batch.completed && batch.req.readyState == 4) {
try {
var reply = batch.req.responseText;

if (reply == null || reply == "") {
DWREngine._handleMetaDataWarning(null, "No data received from server");
}
else {
var contentType = batch.req.getResponseHeader("Content-Type");
if (!contentType.match(/^text\/plain/) && !contentType.match(/^text\/javascript/)) {
if (DWREngine._textHtmlHandler && contentType.match(/^text\/html/)) {
DWREngine._textHtmlHandler();
}
else {
DWREngine._handleMetaDataWarning(null, "Invalid content type from server: '" + contentType + "'");
}
}
else {



if (reply.search("DWREngine._handle") == -1) {
DWREngine._handleMetaDataWarning(null, "Invalid reply from server");
}
else {
eval(reply);
}
}
}


DWREngine._clearUp(batch);
}
catch (ex) {
if (ex == null) ex = "Unknown error occured";
DWREngine._handleMetaDataWarning(null, ex);
}
finally {



if (DWREngine._batchQueue.length != 0) {
var sendbatch = DWREngine._batchQueue.shift();
DWREngine._sendData(sendbatch);
DWREngine._batches[DWREngine._batches.length] = sendbatch;
}
}
}
};






DWREngine._handleResponse = function(id, reply) {

var handlers = DWREngine._handlersMap[id];
DWREngine._handlersMap[id] = null;

if (handlers) {


try {
if (handlers.callback) handlers.callback(reply);
}
catch (ex) {
DWREngine._handleMetaDataError(handlers, ex);
}
}


if (DWREngine._method == DWREngine.IFrame) {
var responseBatch = DWREngine._batches[DWREngine._batches.length-1];

if (responseBatch.map["c"+(responseBatch.map.callCount-1)+"-id"] == id) {
DWREngine._clearUp(responseBatch);
}
}
};


DWREngine._handleServerError = function(id, error) {

var handlers = DWREngine._handlersMap[id];
DWREngine._handlersMap[id] = null;

if (error.message) DWREngine._handleMetaDataError(handlers, error.message, error);
else DWREngine._handleMetaDataError(handlers, error);
};


DWREngine._eval = function(script) {
return eval(script);
}


DWREngine._abortRequest = function(batch) {
if (batch && !batch.completed) {
clearInterval(batch.interval);
DWREngine._clearUp(batch);
if (batch.req) batch.req.abort();

var handlers;
for (var i = 0; i < batch.ids.length; i++) {
handlers = DWREngine._handlersMap[batch.ids[i]];
DWREngine._handleMetaDataError(handlers, "Timeout");
}
}
};


DWREngine._clearUp = function(batch) {
if (batch.completed) {
DWREngine._handleError("Double complete");
return;
}


if (batch.div) batch.div.parentNode.removeChild(batch.div);
if (batch.iframe) batch.iframe.parentNode.removeChild(batch.iframe);
if (batch.form) batch.form.parentNode.removeChild(batch.form);


if (batch.req) delete batch.req;

for (var i = 0; i < batch.postHooks.length; i++) {
batch.postHooks[i]();
}
batch.postHooks = null;


for (var i = 0; i < DWREngine._batches.length; i++) {
if (DWREngine._batches[i] == batch) {
DWREngine._batches.splice(i, 1);
break;
}
}

batch.completed = true;
};


DWREngine._handleError = function(reason, ex) {
if (DWREngine._errorHandler) DWREngine._errorHandler(reason, ex);
};


DWREngine._handleWarning = function(reason, ex) {
if (DWREngine._warningHandler) DWREngine._warningHandler(reason, ex);
};


DWREngine._handleMetaDataError = function(handlers, reason, ex) {
if (handlers && typeof handlers.errorHandler == "function") handlers.errorHandler(reason, ex);
else DWREngine._handleError(reason, ex);
};


DWREngine._handleMetaDataWarning = function(handlers, reason, ex) {
if (handlers && typeof handlers.warningHandler == "function") handlers.warningHandler(reason, ex);
else DWREngine._handleWarning(reason, ex);
};








DWREngine._serializeAll = function(batch, referto, data, name) {
if (data == null) {
batch.map[name] = "null:null";
return;
}

switch (typeof data) {
case "boolean":
batch.map[name] = "boolean:" + data;
break;
case "number":
batch.map[name] = "number:" + data;
break;
case "string":
batch.map[name] = "string:" + encodeURIComponent(data);
break;
case "object":
if (data instanceof String) batch.map[name] = "String:" + encodeURIComponent(data);
else if (data instanceof Boolean) batch.map[name] = "Boolean:" + data;
else if (data instanceof Number) batch.map[name] = "Number:" + data;
else if (data instanceof Date) batch.map[name] = "Date:" + data.getTime();
else if (data instanceof Array) batch.map[name] = DWREngine._serializeArray(batch, referto, data, name);
else batch.map[name] = DWREngine._serializeObject(batch, referto, data, name);
break;
case "function":

break;
default:
DWREngine._handleWarning("Unexpected type: " + typeof data + ", attempting default converter.");
batch.map[name] = "default:" + data;
break;
}
};


DWREngine._lookup = function(referto, data, name) {
var lookup;

for (var i = 0; i < referto.length; i++) {
if (referto[i].data == data) {
lookup = referto[i];
break;
}
}
if (lookup) return "reference:" + lookup.name;
referto.push({ data:data, name:name });
return null;
};


DWREngine._serializeObject = function(batch, referto, data, name) {
var ref = DWREngine._lookup(referto, data, name);
if (ref) return ref;



if (data.nodeName && data.nodeType) {
return DWREngine._serializeXml(batch, referto, data, name);
}


var reply = "Object:{";
var element;
for (element in data) {
batch.paramCount++;
var childName = "c" + DWREngine._batch.map.callCount + "-e" + batch.paramCount;
DWREngine._serializeAll(batch, referto, data[element], childName);

reply += encodeURIComponent(element) + ":reference:" + childName + ", ";
}

if (reply.substring(reply.length - 2) == ", ") {
reply = reply.substring(0, reply.length - 2);
}
reply += "}";

return reply;
};


DWREngine._serializeXml = function(batch, referto, data, name) {
var ref = DWREngine._lookup(referto, data, name);
if (ref) return ref;

var output;
if (window.XMLSerializer) output = new XMLSerializer().serializeToString(data);
else output = data.toXml;

return "XML:" + encodeURIComponent(output);
};


DWREngine._serializeArray = function(batch, referto, data, name) {
var ref = DWREngine._lookup(referto, data, name);
if (ref) return ref;

var reply = "Array:[";
for (var i = 0; i < data.length; i++) {
if (i != 0) reply += ",";
batch.paramCount++;
var childName = "c" + DWREngine._batch.map.callCount + "-e" + batch.paramCount;
DWREngine._serializeAll(batch, referto, data[i], childName);
reply += "reference:";
reply += childName;
}
reply += "]";

return reply;
};


DWREngine._unserializeDocument = function(xml) {
var dom;
if (window.DOMParser) {
var parser = new DOMParser();
dom = parser.parseFromString(xml, "text/xml");
if (!dom.documentElement || dom.documentElement.tagName == "parsererror") {
var message = dom.documentElement.firstChild.data;
message += "\n" + dom.documentElement.firstChild.nextSibling.firstChild.data;
throw message;
}
return dom;
}
else if (window.ActiveXObject) {
dom = DWREngine._newActiveXObject(DWREngine._DOMDocument);
dom.loadXML(xml);
return dom;
}
else {
var div = document.createElement("div");
div.innerHTML = xml;
return div;
}
};





DWREngine._newActiveXObject = function(axarray) {
var returnValue;
for (var i = 0; i < axarray.length; i++) {
try {
returnValue = new ActiveXObject(axarray[i]);
break;
}
catch (ex) {   }
}
return returnValue;
};


if (typeof window.encodeURIComponent === 'undefined') {
DWREngine._utf8 = function(wide) {
wide = "" + wide;
var c;
var s;
var enc = "";
var i = 0;
while (i < wide.length) {
c = wide.charCodeAt(i++);

if (c >= 0xDC00 && c < 0xE000) continue;
if (c >= 0xD800 && c < 0xDC00) {
if (i >= wide.length) continue;
s = wide.charCodeAt(i++);
if (s < 0xDC00 || c >= 0xDE00) continue;
c = ((c - 0xD800) << 10) + (s - 0xDC00) + 0x10000;
}

if (c < 0x80) {
enc += String.fromCharCode(c);
}
else if (c < 0x800) {
enc += String.fromCharCode(0xC0 + (c >> 6), 0x80 + (c & 0x3F));
}
else if (c < 0x10000) {
enc += String.fromCharCode(0xE0 + (c >> 12), 0x80 + (c >> 6 & 0x3F), 0x80 + (c & 0x3F));
}
else {
enc += String.fromCharCode(0xF0 + (c >> 18), 0x80 + (c >> 12 & 0x3F), 0x80 + (c >> 6 & 0x3F), 0x80 + (c & 0x3F));
}
}
return enc;
}

DWREngine._hexchars = "0123456789ABCDEF";

DWREngine._toHex = function(n) {
return DWREngine._hexchars.charAt(n >> 4) + DWREngine._hexchars.charAt(n & 0xF);
}

DWREngine._okURIchars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

window.encodeURIComponent = function(s)  {
s = DWREngine._utf8(s);
var c;
var enc = "";
for (var i= 0; i<s.length; i++) {
if (DWREngine._okURIchars.indexOf(s.charAt(i)) == -1) {
enc += "%" + DWREngine._toHex(s.charCodeAt(i));
}
else {
enc += s.charAt(i);
}
}
return enc;
}
}


if (typeof Array.prototype.splice === 'undefined') {
Array.prototype.splice = function(ind, cnt)
{
if (arguments.length == 0) return ind;
if (typeof ind != "number") ind = 0;
if (ind < 0) ind = Math.max(0,this.length + ind);
if (ind > this.length) {
if (arguments.length > 2) ind = this.length;
else return [];
}
if (arguments.length < 2) cnt = this.length-ind;

cnt = (typeof cnt == "number") ? Math.max(0, cnt) : 0;
removeArray = this.slice(ind, ind + cnt);
endArray = this.slice(ind + cnt);
this.length = ind;

for (var i = 2; i < arguments.length; i++) this[this.length] = arguments[i];
for (i = 0; i < endArray.length; i++) this[this.length] = endArray[i];

return removeArray;
}
}


if (typeof Array.prototype.shift === 'undefined') {
Array.prototype.shift = function(str) {
var val = this[0];
for (var i = 1; i < this.length; ++i) this[i - 1] = this[i];
this.length--;
return val;
}
}


if (typeof Array.prototype.unshift === 'undefined') {
Array.prototype.unshift = function() {
var i = unshift.arguments.length;
for (var j = this.length - 1; j >= 0; --j) this[j + i] = this[j];
for (j = 0; j < i; ++j) this[j] = unshift.arguments[j];
}
}


if (typeof Array.prototype.push === 'undefined') {
Array.prototype.push = function() {
var sub = this.length;
for (var i = 0; i < push.arguments.length; ++i) {
this[sub] = push.arguments[i];
sub++;
}
}
}


if (typeof Array.prototype.pop === 'undefined') {
Array.prototype.pop = function() {
var lastElement = this[this.length - 1];
this.length--;
return lastElement;
}
}
function addToCartProxy(cid) {
 var spanid = 'cmsg_'+cid;
 document.getElementById(spanid).innerHTML = "processing...";
 MetadataCartJS.addToCart(cid, {
  callback:function(data) {
   cartCallback(data,spanid);
  }
 });
}
function cartCallback(data, spanid) {
 document.getElementById(spanid).innerHTML = data;
}