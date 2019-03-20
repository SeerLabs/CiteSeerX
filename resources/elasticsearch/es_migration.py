import MySQLdb
from paper import paper
import pprint
import getpass

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

	list_of_paper_ids = get_ids(citeseerx_db_cur, 5)

	password_string = getpass.getpass("Please enter the csxrepo02 password: ")

	for paper_id in list_of_paper_ids:
		paper1 = paper(paper_id)
		paper1.paper_table_fields(citeseerx_db_cur)
		paper1.authors_table_fields(citeseerx_db_cur)
		paper1.keywords_table_fields(citeseerx_db_cur)
		paper1.csx_citegraph_query(csx_citegraph_cur)
		paper1.retrieve_full_text(password_string)
		pprint.pprint(paper1.values_dict)

	



