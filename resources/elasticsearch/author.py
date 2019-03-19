
class author:

	def __init__(self, author_id):

		self.author_id = author_id
		self.values_dict = {

			"author_id": self.author_id, #disambiguated author ID
			"name": None #name of the author
			"included_clusters": [ #list of cluster_ids which include this author name
				None,
				None
			],
			"included_papers": [ #list of paper_ids that this author has written
				None,
				None
			],
			"affiliation": None, #the department or affiliation of author
			"address": None, #address of the author
			"email": None #email address of the author

		}

	def authors_table_fields(self, cur):

		statement = "SELECT name, affiliation, address, email FROM papers WHERE id='" + self.author_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()[0]
		
		print(type(result_tuple[2]))

		self.values_dict['name'] = result_tuple[0]
		self.values_dict['affiliation'] = result_tuple[1]
		self.values_dict['address'] = result_tuple[2]
		self.values_dict['email'] = result_tuple[3]


	def additional_table_data(self, cur):
		pass





