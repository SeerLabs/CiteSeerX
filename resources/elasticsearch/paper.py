import MySQLdb
import paramiko
import getpass

class paper:


	def __init__(self, paper_id):
		self.paper_id = paper_id
		self.values_dict = {

			"paper_id": self.paper_id,	#unique paper_id
			"title": None,	#string of title of paper
			"cluster": None, #clusterID
			"authors": [
				{
				"name": None, #string of authors name,
				"author_id": None, #string of numerical value
				"cluster": None #cluster the author belongs to 
				}
			], #list of dictionaries contain author name and author_id
			"keywords": [
				{
					"keyword": None, #string
					"keyword_id": None #string of numerical value
				}
			], #list of dictionaries of keywords
			"abstract": None, #string
			"year": None, #integer value
			"venue": None, #string 
			"ncites": None, #integer value
			"scites": None, #integer value
			"doi": None, #string ????????????????????????
			"incol": None, #boolean value
			"authorNorms": None, #???????????????????????????????
			"text": None, #string, full text of paper to be indexed
			"cites": [	#list of cluster_ids that this paper cites
					None,
					None
			], 
			"citedby":[	#list of cluster_ids that cites this paper
					None,
					None
			], 
			"vtime": None, #string version time

		}

	#this function queries the paper table for all the info pertaining to that paper_id
	def paper_table_fields(self, cur):

		statement = "SELECT title, abstract, year, venue, ncites, selfCites, cluster FROM papers WHERE id='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()[0]

		self.values_dict['title'] = result_tuple[0]
		self.values_dict['abstract'] = result_tuple[1]
		self.values_dict['year'] = result_tuple[2]
		self.values_dict['venue'] = result_tuple[3]
		self.values_dict['ncites'] = result_tuple[4]
		self.values_dict['selfCites'] = result_tuple[5]
		self.values_dict['cluster'] = result_tuple[6]

	#this function queries the authors table for author ids and names related to a paper_id
	def authors_table_fields(self, cur):

		statement = "SELECT name, id, cluster FROM authors WHERE paperid='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()

		for author in result_tuple:
			temp_dict = {	"name": author[0], 
							"author_id": str(author[1]).split('L')[0], 
							"cluster": str(author[2]).split('L')[0] 
						}
			self.values_dict['authors'].append(temp_dict)
		
		del self.values_dict['authors'][0]
	
		#print(self.values_dict['authors'])

	#this function queries the keywords table and adds a list to the values_dict
	def keywords_table_fields(self, cur):

		statement = "SELECT keyword, id FROM keywords WHERE paperid='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()
		
		self.values_dict['keywords'] = result_tuple


	#this function queries the csx_citegraph database for relevant information
	def csx_citegraph_query(self, cur):
		
		#this statement grabs the citing data from the citegraph table
		statement = "SELECT id, paperid FROM citations WHERE cluster IN (SELECT citing FROM citegraph WHERE id IN (SELECT id FROM citations WHERE paperid='" + self.paper_id + "'));"

		cur.execute(statement)

		result_citedby_tuple = cur.fetchall()

		#this statement grabs the cited data from teh citegraph table
		statement2 = "SELECT id, paperid FROM citations WHERE cluster IN (SELECT cited FROM citegraph WHERE id IN (SELECT id FROM citations WHERE paperid='" + self.paper_id + "'));"

		cur.execute(statement2)

		result_cites_tuple = cur.fetchall()

		self.values_dict['citedby'] = result_citedby_tuple
		self.values_dict['cites'] = result_cites_tuple

	def retrieve_full_text(self, password_string):

		ssh = paramiko.SSHClient()

		ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

		ssh.connect('csxrepo02.ist.psu.edu', username='swp5504', password=password_string)

		d_path = self.paper_id.split('.')
		#print(f"cd data/repository/rep1/{d_path[0]}/{d_path[1]}/{d_path[2]}/{d_path[3]}/{d_path[0]}; cat {self.paper_id}.body;")
		stdin, stdout, stderr = ssh.exec_command('cd data/repository/rep1/%s/%s/%s/%s/%s; cat %s.body;' % (d_path[0], d_path[1], d_path[2], d_path[3], d_path[0], self.paper_id))
		outlines = stdout.readlines()
		resp = ''.join(outlines)
		self.values_dict['text'] = resp




