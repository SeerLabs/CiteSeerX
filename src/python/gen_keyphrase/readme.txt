1. Copy settings/mysql_settings.template to settings/mysql_settings.
   Specify MySQL settings in settings/mysql_settings.

2. Install NLTK dependencies for python

    ~# python
    >> import nltk (if this fails then pip install nltk first)
    >> nltk.download()
    Downloader> d (for download)
    Identifier> wordnet stopwords maxent_treebank_pos_tagger averaged_perceptron_tagger

3. Run 
   $ ./gen_paper_keywords_noun.py

4. If you want to remove some inapproprate terms, run 
   $ ./drop_paper_keywords.py --drop_keyword="THE TERM TO BE DROPPED"
   

Troubleshooting:

If you get a python error about a bad zip file then one of the files download using nltk.download() may be corrupt. You may need to get a new version for this to work.
