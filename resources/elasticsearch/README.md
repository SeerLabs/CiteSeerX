# CiteSeerX ElasticSearch Migration

The following documentation is intended for administers of the CiteSeerX system who are migrating data from the MySQL database located at csxdb02.ist.psu.edu to the ElasticSearch instance located on csxindex01.ist.psu.edu. By changing one line in the following script and running it, all of the data will successfully transfer from the MySQL system into ElasticSearch. Also described below is the file systems structure and what is contained within this file. A requirements.txt file is included for all the Python dependencies required.

Additionally, this code is containerized and can be ran through Docker with the below command. It is important to note that while the migration script is containerized, the ElasticSearch instance is not and must still be ran in daemon mode prior to the running of the migration script.

The steps to migrate the data from MySQL to ElasticSearch are listed below:

1. Ensure Correct Schema and Parameters
2. Run the ElasticSearch Instance
3. Run the Migration Script (could do through the Docker container)
4. Check on the Migration and watch it realtime in Kibana

## Ensuring Correct Schema and Parameters

In order to understand how the JSON schemas are organized, it is important to know what each of the Python files (and other files) in this directory are used for. The table below illustrates this:

| Filename        | Use         |
| ------------- |:-------------:|
| es_migration.py      | Main migration script, establishes connections to databases, determines how many papers to migrate |
| elasticpython.py      | Is the only file to connect to and interface with ElasticSearch     |
| paper.py | Defines the paper index schema, traverses through directories to retrieve full text of papers, interfaces with MySQL databases      |
| author.py | Defines the author index schema, interfaces with MySQL DB to get author fields |
| cluster.py | Defines the cluster index schema, interfaces with the MySQL DB to get cluster fields  |

Based off of this table then, it is easy to see that to change the schema of the paper index, author index, or cluster index, then we must edit paper.py, author.py, and cluster.py, respectively. In each of these files, the data schema is implemented in a dictionary that is an attribute of the respective class (paper, author, or cluster). The dictionary is easy to change but some other code in other files may need to change occordingly. The data schemas as they are used in Python and inserted into ElasticSearch are as follows:

Paper Index Schema:

```python
    {
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
```

Authors Index Schema:
 
 ```python
 {

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
```

Clusters Index Schema:

```python
{

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
```

The last parameter that must be fine-tuned is the variable named number_of_papers_to_index which is currently on line 84 of es_migration.py.
```python
number_of_papers_to_index = 200000 # CHANGE ME!
```
By specifying the number assigned to this variable, we are changing the number of papers to be indexed by ElasticSearch.

## Running the ElasticSearch Instance

Currently, the ElasticSearch instance and config files run out of the directory /home/swp5504/elasticsearch-6.2.4 on csxindex01.ist.psu.edu.

The config file, incase anything needs to be changed or updated for ElasticSearch, is located in the file /home/swp5504/elasticsearch-6.2.4/config/elasticsearch.yml

The executable of ElasticSearch is located in the /home/swp5504/elasticsearch-6.2.4/bin subdirectory and it can be correctly run as a daemon service by using this command once you have successfully changed directories to this bin folder:

```bash
./elasticsearch -d
```
After you run this, use the following command to ensure that ElasticSearch is running properly on this machine:

```bash
top
```
