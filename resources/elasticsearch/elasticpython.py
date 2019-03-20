import requests
import json
from elasticsearch import Elasticsearch

#This function connects to ElasticSearch on the localhost on port 9200
def establish_ES_connection():

	es = Elasticsearch([{'host': 'localhost',
						 'port': 9200
						}])

	return es

#Quickly tests whether the ElasticSearch connection is successful
def test_ES_connection():

	req = requests.get('http://localhost:9200')

	content = req.content

	parsed = json.loads(content)

	print_response(parsed)

#function to print the response in a easily-readible format
def print_response(response):
	print(json.dumps(response, indent=4, sort_keys=True))

#Check whether a document on a particular Index exists already
def document_exists(doc_id, doc_type):
	pass

#If the document exists already, update the document where the doc_id's are the same
def update_document(doc_id, doc_type, document):
	pass

#If the document does not exist, create it in the proper index
def create_document(doc_type, document):
	pass



	
