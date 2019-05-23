
class author:

	def __init__(self, author_id):
		''' Input: The specific author ID of an author
			Output: None
			Method: Build a value dictionary with all of the relevant schema information
		'''

		self.author_id = author_id
		self.values_dict = {

			"author_id": self.author_id, #disambiguated author ID
			"name": None, #name of the author
			"clusters": [ #list of cluster_ids which include this author name
				
			],
			"papers": [ #list of paper_ids that this author has written
				
			],
			"affiliation": None, #the department or affiliation of author
			"address": None, #address of the author
			"email": None #email address of the author

		}



	def authors_table_fields(self, cur):
		''' Input: MySQL database connection
			Output: None
			Method: Query the MySQL database (authors table specifically) for a specific 
			authorID and properly organize the author data returned 
			in the values_dict data structure. 

		'''

		statement = "SELECT affil, address, email FROM authors WHERE id='" + str(self.author_id) + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()[0]

		self.values_dict['affiliation'] = result_tuple[0]
		self.values_dict['address'] = result_tuple[1]
		self.values_dict['email'] = result_tuple[2]






