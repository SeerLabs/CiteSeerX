from django.conf.urls.defaults import *

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Example:
    # (r'^citeseerx_crawl/', include('citeseerx_crawl.foo.urls')),

    # Uncomment the admin/doc line below and add 'django.contrib.admindocs' 
    # to INSTALLED_APPS to enable admin documentation:
    # (r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # (r'^admin/', include(admin.site.urls)),
           
    (r'^$', 'django.views.generic.simple.direct_to_template', {'template': 'index.htm'}), 
    
    # submission
    (r'^submit/','citeseerx_crawl.main_crawl.submission.handle_submission'),        
    (r'^captcha/',include('captcha.urls')),        
    (r'^tracking/parent/(?P<pid>\d+)/$','citeseerx_crawl.main_crawl.submission.tracking_parent'),
    (r'^tracking/sub/(?P<sid>\d+)/$','citeseerx_crawl.main_crawl.submission.tracking_sub'),        
    (r'^sub/stat/$', 'citeseerx_crawl.main_crawl.submission.sub_stat'),
    (r'^sub/stat/data/$', 'citeseerx_crawl.main_crawl.submission.sub_stat_data'),
    
    # query
    (r'^query/','citeseerx_crawl.main_crawl.query.handle_query'),
    
    # ranking
    (r'^country_ndocs_rank/$','citeseerx_crawl.main_crawl.ranking.country_ndocs_rank'),
    
    (r'^(?P<group_by>[a-z]+)_ndocs_rank/','citeseerx_crawl.main_crawl.ranking.ndocs_rank'),
    (r'^(?P<group_by>[a-z]+)_ncites_rank/','citeseerx_crawl.main_crawl.ranking.ncites_rank'),
    (r'^(?P<group_by>[a-z]+)_cpd_rank/','citeseerx_crawl.main_crawl.ranking.cpd_rank'),
    
    # api
    (r'^api/getdocs.xml$','citeseerx_crawl.main_crawl.api.get_docs_xml'),
    (r'^api/setdocs.xml$','citeseerx_crawl.main_crawl.api.set_docs_xml'),
    
    # apisub
    (r'^apisub/getdocs.xml$','citeseerx_crawl.main_crawl.api.get_docs_xml'),
    (r'^apisub/setdocs.xml$','citeseerx_crawl.main_crawl.api.set_docs_xml'),

    # ----------------
    (r'^history/(?P<group_by>[a-z]+)/(?P<name>.*)','citeseerx_crawl.main_crawl.views.history'),
    (r'^history/','citeseerx_crawl.main_crawl.views.overall_history'),
)
