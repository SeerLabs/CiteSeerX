import MySQLdb
import paramiko
import getpass
import sys

reload(sys)
sys.setdefaultencoding('utf8')

class paper:


	def __init__(self, paper_id):
		self.paper_id = paper_id
		self.values_dict = {

			"paper_id": self.paper_id,	#unique paper_id
			"title": '',	#string of title of paper
			"cluster": '', #clusterID
			"authors": [
				{
				"name": '', #string of authors name,
				"author_id": '', #string of numerical value
				"cluster": '' #cluster the author belongs to 
				}
			], #list of dictionaries contain author name and author_id
			"keywords": [
				{
					"keyword": '', #string
					"keyword_id": '' #string of numerical value
				}
			], #list of dictionaries of keywords
			"abstract": '', #string
			"year": 0, #integer value
			"venue": '', #string 
			"ncites": 0, #integer value
			"scites": 0, #integer value
			"doi": '', #string ????????????????????????
			"incol": None, #boolean value
			"authorNorms": None, #???????????????????????????????
			"text": '', #string, full text of paper to be indexed
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

		statement = "SELECT title, abstract, year, venue, ncites, selfCites, cluster, versionTime FROM papers WHERE id='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()[0]

		self.values_dict['title'] = str(result_tuple[0])
		self.values_dict['abstract'] = str(result_tuple[1])
		print(result_tuple[2])
		if result_tuple[2]:
			self.values_dict['year'] = int(result_tuple[2])
		self.values_dict['venue'] = str(result_tuple[3])
		self.values_dict['ncites'] = int(result_tuple[4])
		self.values_dict['selfCites'] = int(result_tuple[5])
		self.values_dict['cluster'] = int(result_tuple[6])
		self.values_dict['vtime'] = result_tuple[7].strftime('%Y-%m-%d %H:%M:%S')


	#this function queries the authors table for author ids and names related to a paper_id
	def authors_table_fields(self, cur):

		statement = "SELECT name, id, cluster FROM authors WHERE paperid='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()

		for author in result_tuple:
	
			temp_dict = {	"name": str(author[0]), 
							"author_id": int(author[1]), 
							"cluster": int(author[2]) 
						}
			self.values_dict['authors'].append(temp_dict)
		
		del self.values_dict['authors'][0]
	
		#print(self.values_dict['authors'])

	#this function queries the keywords table and adds a list to the values_dict
	def keywords_table_fields(self, cur):

		statement = "SELECT keyword, id FROM keywords WHERE paperid='" + self.paper_id + "';"

		cur.execute(statement)

		result_tuple = cur.fetchall()

		for keyword in result_tuple:
			temp_dict = {	"keyword": str(keyword[0]), #string
							"keyword_id": int(keyword[1]) #string of numerical value
						}
			self.values_dict['keywords'].append(temp_dict)

		del self.values_dict['keywords'][0]


	#this function queries the csx_citegraph database for relevant information
	def csx_citegraph_query(self, cur):
		
		#this statement grabs the cluster ids who have cited this cluster
		statement = "SELECT citing FROM citegraph WHERE cited=" + str(self.values_dict['cluster']) + ";"
		cur.execute(statement)

		result_citedby_tuple = cur.fetchall()

		#this statement grabs the cluster ids who are cited by this cluster
		statement2 = "SELECT cited FROM citegraph WHERE citing=" + str(self.values_dict['cluster']) + ";"

		cur.execute(statement2)

		result_cites_tuple = cur.fetchall()

		self.values_dict['citedby'] = [int(cite[0]) for cite in result_citedby_tuple]
		self.values_dict['cites'] = [int(cite[0]) for cite in result_cites_tuple]

	def retrieve_full_text(self, password_string):

		ssh = paramiko.SSHClient()

		ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

		ssh.connect('csxrepo02.ist.psu.edu', username='swp5504', password=password_string)

		d_path = self.paper_id.split('.')
		#print(f"cd data/repository/rep1/{d_path[0]}/{d_path[1]}/{d_path[2]}/{d_path[3]}/{d_path[0]}; cat {self.paper_id}.body;")
		stdin, stdout, stderr = ssh.exec_command('cd data/repository/rep1/%s/%s/%s/%s/%s; cat %s.body;' % (d_path[0], d_path[1], d_path[2], d_path[3], d_path[0], self.paper_id))
		outlines = stdout.readlines()
		resp = ''.join(outlines)
		self.values_dict['text'] = str(resp)

	#This function takes an input string and encodes it properly for Elastic to ingest
	def fix_encoding(self, string):
		pass

