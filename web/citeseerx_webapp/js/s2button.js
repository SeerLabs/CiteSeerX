'use strict';
(function() {
  $(function() {
    $('input[name="s2"]').click(function() {
      var query = $('input[name="q"]').val();
      document.location = 'https://www.semanticscholar.org/search?q=' + encodeURIComponent(query);
      return false;
    });
  });
}());
