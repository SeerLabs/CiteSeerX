import os
import json
import pprint
import json

global full_text
full_text = ''


def find_all_json(path):
	list_of_json_files = []
	for file in os.listdir(path):
		if file.endswith('.json'): 
			list_of_json_files.append(os.path.join(path, file))
	return list_of_json_files


def load_json_to_dict(path):
	with open(path, 'r') as f:
		return json.load(f)


def get_ID(path):
	first = path.split('.tei')[0]
	second = first.split('son/')[1]
	return second


def get_title(dictionary):
	try:
		return dictionary['TEI']['teiHeader']['fileDesc']['titleStmt']['title']['text']
	except:
		return None

def get_authors(dictionary):
	authors = []
	try:
		for i in dictionary['TEI']['teiHeader']['fileDesc']['sourceDesc']['biblStruct']['analytic']['author']:
			auth_dict = {
				'name': '',
				'author_id': '',
				'cluster': ''
			}
			if type(i) == dict and 'ns1:persName' in i:
				try:
					auth_dict['name'] = i['ns1:persName']['ns1:forename']['text'] + ' ' + i['ns1:persName']['ns1:surname']
					authors.append(auth_dict)
				except:
					pass
	except:
		pass
	return authors

def get_abstract(dictionary):
	try:
		if 'abstract' in dictionary['TEI']['teiHeader']['profileDesc'].keys() or dictionary['TEI']['teiHeader']['profileDesc']['abstract'] != None:
			try:
				return dictionary['TEI']['teiHeader']['profileDesc']['abstract']['ns1:div']['ns1:p']
			except:
				pass
		else:
			return None
	except:
		return None

def get_year(dictionary):
	try:
		dictionary['TEI']['teiHeader']['fileDesc']['publicationStmt']['date']['when']
		return dictionary['TEI']['teiHeader']['fileDesc']['publicationStmt']['date']['when']
	except:
		pass
	try:
		return dictionary['TEI']['teiHeader']['fileDesc']['publicationStmt']['date']
	except:
		return None
# Still needs work ######
def get_keywords(dictionary):
	try:
		list_of_keywords = dictionary['TEI']['teiHeader']['profileDesc']['textClass']['keywords']['term']
		return list_of_keywords
	except:
		return None

# Not sure if this is even included in Grobid output #####
def get_venue(dictionary):
	pass

# Use or build a DOI extracting API ########
def get_doi(dictionary):
	pass

def get_text(node, key): #######
	if isinstance(node, list):
		for i in node:
			for x in get_text(i, key):
				yield x
	elif isinstance(node, dict):
		if key in node:
			try:
				yield node[key]
			except:
				pass
		for j in node.values():
			for x in get_text(j, key):
				yield x

def write_file(dictionary):
	with open(('new_jsons/' + dictionary['paper_id'] + '.json'), 'w') as file:
		json.dump(dictionary, file)

if __name__ == '__main__':

	files = find_all_json('/data/swp5504/results_json/')
	
	paper_ID_count = 0
	title_count = 0
	author_count = 0
	keyword_count = 0
	abstract_count = 0
	year_count = 0
	full_text_count = 0

	for file in files:

		json_data = {}

		ID = get_ID(file)

		dictionary = load_json_to_dict(file)

		title = get_title(dictionary)

		authors = get_authors(dictionary)
		year = get_year(dictionary)

		abstract = get_abstract(dictionary)

		keywords = get_keywords(dictionary)

		for i in list(get_text(dictionary, 'ns1:p')):
			if isinstance(i, str):
				full_text += i

		json_data['paper_id'] = ID
		json_data['title'] = title
		json_data['cluster'] = ''
		json_data['authors'] = authors
		json_data['keywords'] = keywords
		json_data['abstract'] = abstract
		json_data['year'] = year
		json_data['venue'] = ''
		json_data['ncites'] = 0
		json_data['scites'] = 0
		json_data['doi'] = ''
		json_data['incol'] = None
		json_data['authorNorms'] = None
		json_data['text'] = full_text
		json_data['cites'] = [None, None]
		json_data['citedby'] = [None, None]
		json_data['vtime'] = None

		if json_data['paper_id']:
			paper_ID_count += 1
		if json_data['title']:
			title_count += 1
		if json_data['authors']:
			author_count += 1
		if json_data['keywords']:
			keyword_count += 1
		if json_data['abstract']:
			abstract_count += 1
		if json_data['year']:
			year_count += 1
		if json_data['text']:
			full_text_count += 1

		write_file(json_data)

		pprint.pprint(json_data)
	print('paper_ID_count = ' + str(paper_ID_count))
	print('title = ' + str(title_count))
	print('keywords = ' + str(keyword_count))
	print('abstracts = ' + str(abstract_count))
	print('year = ' + str(year_count))
	print('full text = ' + str(full_text_count))

