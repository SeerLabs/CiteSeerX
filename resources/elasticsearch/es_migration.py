# Import SQL capabilities
import MySQLdb

# Import ElasticSearch capabilities
import elasticpython

# Import each of the schemas and associated methods for each index
from paper import paper
from author import author
from cluster import cluster



def get_ids(cur, n):	
	''' Input: Database cursor (database connection), n number of papers to retrieve
		Output: Returns a list of first 'n' number of paper ids from the SQL DB 
		Method: Queries the database for the paper ids and returns a list of length 'n'

	'''

	statement = "SELECT id FROM papers LIMIT %d;" % (n)

	cur.execute(statement)

	return [tup[0] for tup in cur.fetchall()]


def connect_to_citeseerx_db():
	''' Input: None
		Output: Returns the cursor (connection) to the citeseerx database
		Method: Using the python MySQL API, establishes a connection with the citeseerx DB

	'''

	db = MySQLdb.connect(host="csxdb02.ist.psu.edu",
                        user="csx-prod",
                        passwd="csx-prod",
                        db="citeseerx",
			charset='utf8')

	return db.cursor()


def connect_to_csx_citegraph():
	''' Input: None
		Output: Returns the cursor (connection) to the csx_citegraph DB
		Method: Using the python MySQL API, connects to the csx_citegraph database

	'''

	db = MySQLdb.connect(host="csxdb02.ist.psu.edu",
                        user="csx-prod",
                        passwd="csx-prod",
                        db="csx_citegraph",
			charset='utf8')

	return db.cursor()


def authorHelperUpsert(paper, citeseerx_db_cur):
	''' Input: Paper object with it's values dictionary, and citeseerx database connection
		Output: None
		Method: Iterate through each author on a given paper, prepare the dictionary
				for upsertion into the authors index in ElasticSearch. 
				Upserting means insert if the object doesn't already exist, update if it does
		
	'''

	for auth in paper.values_dict['authors']:

			author1 = author(auth['author_id'])

			author1.values_dict['clusters'] = [auth['cluster']]
			author1.values_dict['name'] = auth['name']
			author1.values_dict['papers'] = [paper.values_dict['paper_id']]

			author1.authors_table_fields(citeseerx_db_cur)

			elasticpython.update_authors_document(es, index='authors', doc_id=author1.values_dict['author_id'],
										doc_type='author', data=author1.values_dict)


def clusterHelperUpsert(paper):
	''' Input: Paper object with it's values dictionary
		Output: None
		Method: Prepare the clusters dictionary for upsertion into ElasticSearch

	'''

	cluster1 = cluster(paper.values_dict['cluster'])

	cluster1.values_dict['included_papers'] = [paper.values_dict['paper_id']]

	list_of_author_names = [auth['name'] for auth in paper.values_dict['authors']]

	cluster1.values_dict['included_authors'] = list_of_author_names

	elasticpython.update_clusters_document(es, index='clusters', doc_id=cluster1.values_dict['cluster_id'],
											doc_type='cluster', data=cluster1.values_dict)




if __name__ == "__main__":
	''' Main Method
		Method: Call all above methods then sets the number of papers to index.
				Iterates through each paper and indexes the paper, all authors, and the cluster
				of said paper.


	'''

	# Establish connections to databases and ElasticSearch
	citeseerx_db_cur = connect_to_citeseerx_db()
	csx_citegraph_cur = connect_to_csx_citegraph()
	es = elasticpython.establish_ES_connection()
	elasticpython.test_ES_connection()

	# Set the number of papers to index by this migration script
	number_of_papers_to_index = 200000

	# Retrieve the list of paper ids
	list_of_paper_ids = get_ids(citeseerx_db_cur, number_of_papers_to_index)

	# Set counter so we can keep track of how many papers have migrated in real-time
	paper_count = 0

	# Iterate through each of the paper_ids selected and add them to the index
	for paper_id in list_of_paper_ids:

		# Every 100 papers print out our current progress
		if paper_count % 100 == 0:
			print('Total paper count: ', str(paper_count))

		# Extract all the fields neccessary for the paper type from the MySQL DBs
		paper1 = paper(paper_id)
		paper1.paper_table_fields(citeseerx_db_cur)
		paper1.authors_table_fields(citeseerx_db_cur)
		paper1.keywords_table_fields(citeseerx_db_cur)
		paper1.csx_citegraph_query(csx_citegraph_cur)
		paper1.retrieve_full_text()

		# Load the paper JSON data into ElasticSearch
		elasticpython.create_document(es, index='citeseerx', doc_id=paper1.values_dict['paper_id'], doc_type='paper', data=paper1.values_dict)

		# We also need to update the other indices like author and cluster
		# By using the update and upserts command in ElasticSearch, we can do this easily
		authorHelperUpsert(paper1, citeseerx_db_cur)
		clusterHelperUpsert(paper1)

		# Increment counter so we can keep track of migration progress
		paper_count += 1


