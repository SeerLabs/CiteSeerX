# CiteSeerX ElasticSearch Migration

The following documentation is intended for administers of the CiteSeerX system who are migrating data from the MySQL database located at csxdb02.ist.psu.edu to the ElasticSearch instance located on csxindex01.ist.psu.edu. By changing one line in the following script and running it, all of the data will successfully transfer from the MySQL system into ElasticSearch. Also described below is the file systems structure and what is contained within this file. A requirements.txt file is included for all the Python dependencies required.

Additionally, this code is containerized and can be ran through Docker with the below command. It is important to note that while the migration script is containerized, the ElasticSearch instance is not and must still be ran in daemon mode prior to the running of the migration script.

The steps to migrate the data from MySQL to ElasticSearch are listed below:

1. Ensure Correct Schema and Parameters
2. Run the ElasticSearch Instance
3. Run the Migration Script (could do through the Docker container)
4. Check on the Migration and watch it realtime in Kibana

### Ensuring Correct Schema and Parameters

In order to understand how the JSON schemas are organized, it is important to know what each of the Python files (and other files) in this directory are used for. The table below illustrates this:

| Filename        | Use         |
| ------------- |:-------------:|
| es_migration.py      | Main migration script, establishes connections to databases, determines how many papers to migrate |
| elasticpython.py      | Is the only file to connect to and interface with ElasticSearch     |
| paper.py | Defines the paper index schema, traverses through directories to retrieve full text of papers, interfaces with MySQL databases      |
| author.py | Defines the author index schema, interfaces with MySQL DB to get author fields |
| cluster.py | Defines the cluster index schema, interfaces with the MySQL DB to get cluster fields  |

