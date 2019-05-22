# Import capabilities to make HTTP requests to ElasticSearch
import requests

# Import ability to work with JSON objects in Python
import json

# Import ElasticSearch API for Python
from elasticsearch import Elasticsearch


def establish_ES_connection():
	''' Input: None
		Output: ElasticSearch connection
		Method: Using the ElasticSearch Python API

	'''

	es = Elasticsearch([{'host': 'localhost',
						 'port': 9200
						}])

	return es


def test_ES_connection():
	''' Input: None
		Output None
		Method: Test Python's connection to ElasticSearch and print the response		

	'''

	req = requests.get('http://localhost:9200')

	content = req.content

	parsed = json.loads(content)

	print_response(parsed)


def print_response(response):
	''' Input: None
		Output: None
		Method: Prints the JSON of the response from ElasticSearch to test connection

	'''

	print(json.dumps(response, indent=4, sort_keys=True))


#If the document exists already, update the document where the doc_id's are the same
def update_authors_document(es, index, doc_id, doc_type, data):
	''' Input: ElasticSearch instance, index name (authors), document id, document type (authors), and data dictionary
		Output: None
		Method: First we properly format scripts to be ran on ElasticSearch in 
				order to upsert the correct values using the painless scripting 
				language. My formatting the dictionaries in such a way that will 
				allow ElasticSearch to upsert the document into the authors index,
				we don't need to worry about if the document exists already. Then we
				use the traditional 'update' command for ElasticSearch to apply the upsert.
	'''
	
	new_data = {}


	# We also need to add a script to the JSON to check and add the associated data appropriately
	new_data['script'] = {
					"source": "ctx._source.papers.add(params.new_papers); ctx._source.papers.add(params.new_clusters)",
					"lang": "painless",
					"params": {
						"new_papers": data['papers'][0],
						"new_clusters": data['clusters'][0]
					}
	 }

	new_data['upsert'] = {
					"papers": data['papers'],
					"author_id": data['author_id'],
                                        "cluster": data['clusters'],
                                        "name": data['name'],
					"affiliation": data['affiliation'],
					"address": data['address'],
					"email": data['email']

	}

	# Update the specific document located by the ID	
	update1 = es.update(index=index, doc_type=doc_type, id=doc_id,
						body=new_data)


def update_clusters_document(es, index, doc_id, doc_type, data):
	''' Input: ElasticSearch instance, index name (clusters), document id, document type (clusters), and data dictionary
		Output: None
		Method: First we properly format scripts to be ran on ElasticSearch in 
				order to upsert the correct values using the painless scripting 
				language. My formatting the dictionaries in such a way that will 
				allow ElasticSearch to upsert the document into the clusters index,
				we don't need to worry about if the document exists already. Then we
				use the traditional 'update' command for ElasticSearch to apply the upsert.
	'''

	new_data = {}

	new_data['script'] = {
					"source": "ctx._source.included_papers.add(params.new_papers); ctx._source.included_authors.add(params.new_authors)",
					"lang": "painless",
					"params": {
						"new_papers": data['included_papers'][0],
						"new_authors": data['included_authors']
					}
	}


	new_data['upsert'] = {
					"cluster_id": data['cluster_id'],
					"included_papers": data['included_papers'],
					"included_authors": data['included_authors']
	}


	update1 = es.update(index=index, doc_type=doc_type, id=doc_id, body=new_data)
	

def create_document(es, index, doc_id, doc_type, data):
	''' Input: ElasticSearch instance, index name (papers), document id, document type (papers), and data dictionary
		Output: None
		Method: For each paper, we need to create a document in ElasticSearch.

	'''

	# Begin indexing the data in the correct index
	index1 = es.index(index=index, id=doc_id, doc_type=doc_type, body=data)





