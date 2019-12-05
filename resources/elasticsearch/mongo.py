from pymongo import MongoClient
from pprint import pprint


class Mongo():

	def __init__(self):
		self.client = None
		self.db = None

	def establishMongoConnection(self):
		client = MongoClient('localhost', 27017)
		self.client = client
		self.db = self.client['indexTest']

	def getCollection(self, colName):
		collection = self.db[colName]
		return collection

	def createDocument(self, collection, data):
		papers = self.db.papers
		result = papers.insert_one(data)
		print('One post: {0}'.format(result.inserted_id))

	def upsertAuthor(self, collection, data):
		authors = self.db.authors
		result = authors.update_one()
		pass

	def upsertCluster(self, collection, data):
		pass




mongo1 = Mongo()
mongo1.establishMongoConnection()
papers_collection = mongo1.getCollection("papers")

post_data = {
	'title': 'Python and MongoDB',
	'content': 'PyMongo is fun, you guys',
	'author': 'Scott'
}

post_data['_id'] = 1234

mongo1.createDocument(papers_collection, post_data)

