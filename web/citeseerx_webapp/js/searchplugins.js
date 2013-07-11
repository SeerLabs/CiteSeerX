/*
 * Code base in example at: http://developer.mozilla.org/en/docs/Adding_search_engines_from_web_pages
 */

function addSearchPlugin(plugin) {
 if (window.external && ("AddSearchProvider" in window.external)) {
   // Firefox 2 and IE 7, OpenSearch
   if (plugin =='generalp') {
    window.external.AddSearchProvider("http://citeseerx.ist.psu.edu/citeserx/search_plugins/citeseerx_general.xml");
   } else if (plugin =='authorp') {
    window.external.AddSearchProvider("http://citeseerx.ist.psu.edu/citeseerx/search_plugins/citeseerx_author.xml");
   } else if (plugin =='titlep') {
    window.external.AddSearchProvider("http://citeseerx.ist.psu.edu/citeseerx/search_plugins/citeseerx_title.xml");
   }
 } else if (window.sidebar && ("addSearchEngine" in window.sidebar)) {
   // Firefox <= 1.5, Sherlock
   // No matter what. For 1.5 there is only one plug in. 
   window.sidebar.addSearchEngine("http://citeseerx.ist.psu.edu/citeserx/search_plugins/citeseerx_general.src",
                                  "http://citeseerx.ist.psu.edu/citeserx/search_plugins/citeseerx_general.png",
                                  "CiteSeerX General", "");
 } else {
   // No search engine support (IE 6, Opera, etc).
   alert("No search engine support");
 }
}