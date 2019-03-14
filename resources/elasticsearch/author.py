
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