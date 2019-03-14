

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