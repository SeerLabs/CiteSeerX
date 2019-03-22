import MySQLdb
from paper import paper
import pprint
import getpass
import elasticpython
from author import author

#returns the first 'n' number of paper ids from the SQL db in the form of a list
def get_ids(cur, n):	

	statement = "SELECT id FROM papers LIMIT %d;" % (n)

	cur.execute(statement)

	return [tup[0] for tup in cur.fetchall()]

def connect_to_citeseerx_db():
	db = MySQLdb.connect(host="csxdb02.ist.psu.edu",
                        user="csx-prod",
                        passwd="csx-prod",
                        db="citeseerx")

	return db.cursor()

def connect_to_csx_citegraph():
	db = MySQLdb.connect(host="csxdb02.ist.psu.edu",
                        user="csx-prod",
                        passwd="csx-prod",
                        db="csx_citegraph")

	return db.cursor()

if __name__ == "__main__":
	

	citeseerx_db_cur = connect_to_citeseerx_db()

	csx_citegraph_cur = connect_to_csx_citegraph()

	es = establish_ES_connection()

	test_ES_connection()

	list_of_paper_ids = get_ids(citeseerx_db_cur, 5)

	password_string = getpass.getpass("Please enter the csxrepo02 password: ")

	#iterate through each of the paper_ids selected and add them to the index
	for paper_id in list_of_paper_ids:


		#extract all the fields neccessary for the paper type from the MySQL DBs
		paper1 = paper(paper_id)
		paper1.paper_table_fields(citeseerx_db_cur)
		paper1.authors_table_fields(citeseerx_db_cur)
		paper1.keywords_table_fields(citeseerx_db_cur)
		paper1.csx_citegraph_query(csx_citegraph_cur)
		paper1.retrieve_full_text(password_string)

		#Load the paper JSON data into ElasticSearch

		create_document(es, index='citeseerx', doc_id=paper1.values_dict['paper_id'], doc_type='paper', data=paper1.values_dict)


		#pprint.pprint(paper1.values_dict)

	



