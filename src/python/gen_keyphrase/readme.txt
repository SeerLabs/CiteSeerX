1. Copy settings/mysql_settings.template to settings/mysql_settings.
   Specify MySQL settings in settings/mysql_settings.

2. Run 
   $ ./gen_paper_keywords_noun.py

3. If you want to remove some inapproprate terms, run 
   $ ./drop_paper_keywords.py --drop_keyword="THE TERM TO BE DROPPED"
