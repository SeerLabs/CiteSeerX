# Django setting module 
django_settings_module = 'settings'
# directory where output files are saved (add the "/")
outputdir = 'dochealthcheck/'
# repository (input directory)
inputdir = '/data/csxcrawl/repository/'
# toggle: delete from db
toggle_delete_from_db = True
# toggle: delete from repository
toggle_delete_from_repo = True
# database table name to query from
dbt_name = 'main_crawl_document'
# accepted document mime types
accepted_mimes = ['application/pdf']
# output file
f_docsize = 'docsize.txt'
# unhealthy documents
f_unhealthdoc = 'doccheck.unhealth.txt'
