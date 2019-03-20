

class cluster:

	def __init__(self, cluster_id):

		self.cluster_id = cluster_id
		self.values_dict = {

			"cluster_id": self.cluster_id, #unique cluster ID
			"included_papers": [ #list of paper_ids which are included in this cluster
				None,
				None
			],
			"included_authors": [ #list of authors included in this cluster
				None,
				None
			]

		}

	#This function searches the papers table for all papers within a given cluster
	#This is the papers table in the csx_citegraph database
	def papers_table_fields(self, cur):

		statement = "SELECT id FROM papers WHERE cluster='" + self.author_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()[0]
		
		print(type(result_tuple[2]))

		self.values_dict['name'] = result_tuple[0]
		self.values_dict['affiliation'] = result_tuple[1]
		self.values_dict['address'] = result_tuple[2]
		self.values_dict['email'] = result_tuple[3]


	def get_papers(self, cur):
		pass

	def get_authors(self, cur):
		pass          






