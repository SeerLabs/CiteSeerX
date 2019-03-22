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
def document_exists(es, index, doc_id, doc_type):
	pass

#If the document exists already, update the document where the doc_id's are the same
def update_document(es, index, doc_id, doc_type, data):

	#Update the specific document located by the ID
	update1 = es.update(index=index, doc_type=doc_type, id=doc_id,
						body={"doc": data})
	print(update1)

#If the document does not exist, create it in the proper index
def create_document(es, index, doc_id, doc_type, data):

	#Begin indexing the data in the correct index

	index1 = es.index(index=index, id=doc_id, doc_type=doc_type, body=data)
	print(index1)





