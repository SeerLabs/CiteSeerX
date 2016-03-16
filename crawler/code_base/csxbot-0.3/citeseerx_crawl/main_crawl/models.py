from django.db import models

class ParentUrl(models.Model):
    url = models.CharField(max_length=255, unique=True)
    md5 = models.CharField(max_length=32, unique=True)
    first_crawl_date = models.DateTimeField(db_index=True)
    last_crawl_date = models.DateTimeField(db_index=True)
    is_live = models.BooleanField(db_index=True)

class Document(models.Model):
    url = models.CharField(max_length=255, unique=True)
    md5 = models.CharField(max_length=32, unique=True)
    host = models.CharField(max_length=255, null=True, db_index=True)
    rev_host = models.CharField(max_length=255, null=True, db_index=True)
    content_sha1 = models.CharField(max_length=40, db_index=True)    
    discover_date = models.DateTimeField(db_index=True)
    update_date = models.DateTimeField(db_index=True)
    parent = models.ForeignKey('ParentUrl', null=True, db_index=True)
    state = models.IntegerField(null=True, db_index=True) # 0=crawled 1=ingeted -1=failed to ingest
    submission_id = models.IntegerField(null=True,db_index=True)
    priority = models.IntegerField()

##class Documentt(models.Model):
##    url = models.CharField(max_length=255, unique=True)
##    md5 = models.CharField(max_length=32, unique=True)
##    host = models.CharField(max_length=255, null=True, db_index=True)
##    content_sha1 = models.CharField(max_length=40, db_index=True)
##    discover_date = models.DateTimeField(db_index=True)
##    update_date = models.DateTimeField(db_index=True)
##    parent = models.ForeignKey('ParentUrl', null=True, db_index=True)
##    state = models.IntegerField(null=True, db_index=True) # 0=crawled 1=ingeted -1=failed to ingest
##    submission_id = models.IntegerField(null=True,db_index=True)

class Submission(models.Model):
    url = models.CharField(max_length=255, db_index=True)
    email = models.CharField(max_length=255, db_index=True)
    add_time = models.DateTimeField(auto_now_add=True, db_index=True)
    submitter_name = models.CharField(max_length=255, db_index=True)
    
    def __unicode__(self):
        return 'sub-' + str(self.id)

class HostStat(models.Model):
    host = models.CharField(max_length=255, unique=True)
    ndocs = models.IntegerField(db_index=True)
    ncites = models.IntegerField(db_index=True)
    history = models.TextField(null=True)

class DomainStat(models.Model):
    domain = models.CharField(max_length=50, unique=True)
    ndocs = models.IntegerField(db_index=True)
    ncites = models.IntegerField(db_index=True)
    history = models.TextField(null=True)
    ip = models.CharField(max_length=15, blank=True)
    latitude = models.FloatField(null=True)
    longitude = models.FloatField(null=True)
    country = models.CharField(max_length=2, blank=True, db_index=True)    

class TldStat(models.Model):
    tld = models.CharField(max_length=50, unique=True)
    ndocs = models.IntegerField(db_index=True)
    ncites = models.IntegerField(db_index=True)
    history = models.TextField(null=True)
