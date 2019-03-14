import MySQLdb
import paramiko




class paper:


	def __init__(self, paper_id):
		self.paper_id = paper_id
		self.values_dict = {

			"paper_id": self.paper_id,	#unique paper_id
			"title": None,	#string of title of paper
			"authors": [
				{
				"name": None, #string of authors name,
				"author_id": None #string of numerical value
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

		statement = "SELECT title, abstract, year, venue, ncites, selfCites FROM papers WHERE id='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()[0]
		
		print(type(result_tuple[2]))

		self.values_dict['title'] = result_tuple[0]
		self.values_dict['abstract'] = result_tuple[1]
		self.values_dict['year'] = result_tuple[2]
		self.values_dict['venue'] = result_tuple[3]
		self.values_dict['ncites'] = result_tuple[4]
		self.values_dict['selfCites'] = result_tuple[5]

	#this function queries the authors table for author ids and names related to a paper_id
	def authors_table_fields(self, cur):

		statement = "SELECT name, id FROM authors WHERE paperid='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()
		
		self.values_dict['authors'] = result_tuple

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

	def retrieve_full_text(self):

		ssh = paramiko.SSHClient()

		ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

		ssh.connect('csxrepo02.ist.psu.edu', username='swp5504',password='password')

		d_path = self.paper_id.split('.')

		stdin, stdout, stderr = ssh.exec_command(f'cd data/repository/rep1/{d_path[0]}/{d_path[1]}/{d_path[2]}/{d_path[3]}/{d_path[0]}; cat {self.paper_id}.body;')
		outlines = stdout.readlines()
		resp = ''.join(outlines)
		print('this is the full text:')
		print(resp)




