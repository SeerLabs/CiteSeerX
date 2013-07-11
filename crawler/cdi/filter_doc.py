# -*- coding: latin-1 -*-
import re
import urlparse
import sys
import os
import codecs


###########################################################################
# Using this program
# You will need
#	1. A directory containing the textfiles 
#	2. A file with the file id and url of the textfile

# Update:
#textFileDir = '/export/newFilter/textfiles'
#urlMapFile = 'doc_url_list.csv'
#textFileDir = './'
#urlMapFile = 'doc_url_list.csv'
# The textfile name should be of the format fileid.yyy (the extension must only contains 3 letters)
# where fileid is an entry in the urlMapFile:
# fileid,http://urlforfileid.url
###########################################################################
# Output
# prints fileid,{1,-1} to the stdout
# where fileid is the filename, +1 means the document is a paper, -1 means
# it is not

def log_reg_classify(dictValues):
	lgreg = {}
	constant = 0.91
	lgreg['absPos'] = 0.79
	lgreg['figure'] = 0.06
	lgreg['nwsCount'] = -1.64
	lgreg['outCount'] = -0.08
	lgreg['pgotc'] = -0.52
	lgreg['pop'] = 0.52
	lgreg['rfwPos'] = 0.49
	lgreg['sldCount'] =-0.08
	lgreg['thgBool'] = -0.79
	lgreg['thppCount'] = 0.06
	lgreg['thppPos'] = 0.62
	lgreg['titleCap'] =-8.15
	lgreg['uPaper'] = 0.79
	lgreg['uResearch'] = 0.95
	lgreg['yYCount'] = -25.03
	sum = constant
	for k in lgreg.keys():
		if(dictValues.has_key(k)):
			sum+=(dictValues[k]*lgreg[k])
	if(sum > 0):
		return 1
	else:
		return -1



def processURL(urlString):
	urlSegments = urlparse.urlparse(urlString)
	sfeat = ['uResearch','uPaper','uThesis','uResume','uNewsletter','uJob'
		 'uLecture','uReport']
	uDict = {}
	uDict.fromkeys(sfeat,0)
	for x in urlSegments[2:]:
		if(re.search("research",x,re.I)):
			uDict['uResearch'] = 1
		elif(re.search("paper|publish|publication|pub",x,re.I)):
			uDict['uPaper'] = 1
		elif(re.search("thesis|diss|theses|etd",x,re.I)):
			uDict['uThesis'] = 1
		elif(re.search("CV|vitae|vita|\Wcv|resume|Resume",x)):
			uDict['uResume'] = 1
		elif(re.search("newsletter",x,re.I)):
			uDict['uNewsletter'] = 1
		elif(re.search("career|job|opportunities",x,re.I)):
			uDict['uJob'] = 1
		elif(re.search("course|teaching|slides|lecture|\W*[A-Z]+[0-9]+\W",x,re.I)):
			uDict['uLecture'] = 1
		elif(re.search("report",x)):
			uDict['uReport'] = 1
	return uDict


def processFile(fileName,url,id):
	seqofFeatures = ['absCount','ugPos','thppPos','figure','thppCount','expCount','memCount','pubCount','orkCount','eduCount',
			 'paperRule','owuCount','yYCount','titleCap','rvBool','wpPage','tcSen',
			 'uResearch','uPaper','clpmgCount','rfwPos','chpCount','seCount','nwsCount',
			 'guidePos','empty','paperCoagBool','dtCount','uReport','lcnote','pgotl',
			 'uLecture','kctPos','handCount','outCount','sldCount','refBool','tlcSen',
			 'thgBool','refPos','pgotc','pot','pop','thmBool','empoeCount',
			]
	dictFeatures = dict.fromkeys(seqofFeatures,0)
	try:
		fileHandle = codecs.open(fileName,'r','utf-8')
	except IOError:
		dictFeatures['empty'] = 1
		return dictFeatures

	dictURL = processURL(url)
	for y in dictURL.keys():
		if(y in seqofFeatures):
			dictFeatures[y] = dictURL[y]

	# Line by line processing
	lineCounter = 0.0
	nonEmptyLine = 0.0
	content = fileHandle.read()
	for currentLine in re.split("\n",content):
		lineCounter = lineCounter+1.0
		currentLine.rstrip("\n")
		if(len(currentLine) > 0):
			nonEmptyLine+=1.0
		if(re.match(r'^[A-Z]',currentLine)):
			dictFeatures['tcSen']+=1.0
			linLen = len(re.findall(r'\b\w+\b',currentLine))
			if(linLen < 4):
				dictFeatures['tlcSen']+=1.0
	fileHandle.close()
	# 
	# Content Processing
	docLen = float(len(content))
	pageContents = re.split(r'\cL',content)
	pages = len(pageContents)
	if(docLen < 1):
		dictFeatures['empty'] = 1
		return dictFeatures
	content = re.sub(r'[\"\'\cL\cM]+','',content)
	words = re.split(r'[\s\t,\.\-]+',content)
	wordCount = float(len(words))
	for term in words:
		if(re.match(r'^[A-Z]\S+$',term)):
			dictFeatures['titleCap']+=1
	if(wordCount < 1):
		dictFeatures['empty'] = 1
		return dictFeatures
	# Normalization
	rwLines = wordCount/nonEmptyLine
	dictFeatures['wpPage'] = -1
	# 300 is about the mean number of words per page
	if pages > 0:
		dictFeatures['wpPage'] = wordCount/(pages*300.0)
		if re.search(r'^\s*Transactions\b',pageContents[0],re.M):
			dictFeatures['pot'] = 1
		if re.search(r'^\s*Proceedings\b',pageContents[0],re.M):
			dictFeatures['pop'] = 1
		pageWords = re.split(r'\s+\n',pageContents[0])
		pageLines = re.split(r'\n',pageContents[0])
		for y in pageLines:
			if re.search(r'^[A-Z]',y):
				dictFeatures['pgotl']+=1.0
		pageTitleCap = len(re.findall(r'\b[A-Z]\S+$',pageContents[0]))
		dictFeatures['pgotc'] = pageTitleCap/len(pageWords)


	dictFeatures['titleCap'] = dictFeatures['titleCap']/wordCount
	dictFeatures['tlcSen'] = dictFeatures['tlcSen']/nonEmptyLine
	dictFeatures['tcSen'] = dictFeatures['tcSen']/nonEmptyLine
	# Resumes
	# 'expCount','memCount','pubCount','orkCount','eduCount'
	dictFeatures['expCount'] = len(re.findall(r'\bEXPERIENCE|Experience|Work\s+History|WORK\s+HISTORY\b',content))
	dictFeatures['memCount'] = len(re.findall(r'\bMembership|Memberships|MEMBERSHIP|MEMBERSHIPS|Affiliations\b',content))
	dictFeatures['pubCount'] = len(re.findall(r'\bPublications|PUBLICATIONS|PAPERS|PATENTS|Patents\b',content))
	dictFeatures['orkCount'] = len(re.findall(r'\bEmployment|Skills|SKILLS|Hobbies|AWARDS|Awards\b',content))
	dictFeatures['eduCount'] = len(re.findall(r'(Education|EDUCATION)(.*)(Degree|DEGREE|degree|Diploma|Masters|M\.S\.|B\.S\.|Ph\.D|Doctoral|Bachelors|University)\b',content))
	dictFeatures['empoeCount'] = dictFeatures['expCount']+ dictFeatures['memCount']+ dictFeatures['pubCount'] + dictFeatures['orkCount'] + dictFeatures['eduCount']	
	
	dictFeatures['yYCount'] = len(re.findall(r'\byou|your\b',content,re.I))/wordCount
	dictFeatures['owuCount'] = len(re.findall(r'\bour|we|us\b',content,re.I))/wordCount
	# Papers
	paperRule = -1
	if(re.search(r'^\s*Abstract|ABSTRACT\b',content,re.M)):
		absSplit = re.split(r'^\s*Abstract|ABSTRACT\b',content,re.M)
		dictFeatures['absPos'] = len(absSplit[0])/docLen
		dictFeatures['absCount'] = len(absSplit)
		paperRule = 1
	else:
		dictFeatures['absPos'] = -1
		dictFeatures['absCount'] = -1

	if(re.search(r'^[0-9\.\s]*Introduction|INTRODUCTION\b',content,re.M)):
		intSplit = re.split(r'^[0-9\.\s]*Introduction|INTRODUCTION\b',content,re.M)
		if(dictFeatures['absPos'] == -1):
			dictFeatures['absPos'] = len(intSplit[0])/docLen
		if re.search(r'^[\d\s\.]*References|REFERENCES|Bibliography|BIBLIOGRAPHY\s*$',content,re.M):
			paperRule = 1
		else:
			paperRule = -1
	else:
		paperRule = -1

	if(re.search(r'^[\d\s\.]*References|REFERENCES|Bibliography|BIBLIOGRAPHY\s*$',content,re.M)):
		refOcc = re.split(r'^[\d\s\.]*References|REFERENCES|Bibliography|BIBLIOGRAPHY\s*$',content,re.M)
		dictFeatures['refPos'] = len(refOcc[len(refOcc)-1])/docLen
		dictFeatures['refBool'] = 1
	else:
		dictFeatures['refPos'] = -1
		dictFeatures['refBool'] = -1

	
	if(re.search(r'\b[Tt]his\s+paper\b',content)):
		thppOcc = re.split(r'\b[Tt]his\s+paper\b',content)
		dictFeatures['thppPos'] = len(thppOcc[0])/docLen
		dictFeatures['thppCount'] = len(thppOcc)
	else:
		dictFeatures['thppPos'] = -1
		dictFeatures['thppCount'] = -1

	if re.search(r'\b[Tt]his\s+[Gg]uide\b',content):
		dictFeatures['thgBool'] = 1
	
	if re.search(r'\b[Tt]his\s+[Mm]anual\b',content):
		dictFeatures['thmBool'] = 1

	if re.search(r'\bDissertation|Thesis\b',content,re.I):
		dtOcc = re.split(r'\bDissertation|Thesis\b',content,re.I)
		dictFeatures['dtCount'] = len(dtOcc)/wordCount
	else:
		dictFeatures['dtCount'] = -1

	if(re.search("thispaper",content)):
		dictFeatures['paperCoagBool'] = 1
	
	# Figures/Tables
	if(re.search(r'^\s*Figure\b|Fig\.|FIGURE|figure\s+',content,re.M)):
		figList = re.split(r'^\s*Figure\b|Fig\.|FIGURE|figure\s+',content,re.M)
		dictFeatures['figure'] = len(figList)
	else:
		dictFeatures['figure'] = -1
	
	# Article
	dictFeatures['rfwPos'] = -1
	if(re.search(r'\bRELATED\s+WORK|Related\s+Work|Future\s+Work|FUTURE\s+WORK\b',content)):
		rfwOcc = re.split(r'\bRELATED\s+WORK|Related\s+Work|Future\s+Work|FUTURE\s+WORK\b',content)
		dictFeatures['rfwPos']=len(rfwOcc[0])/docLen
	
	# User Guides
	if(re.search(r'\bUSERS\s+GUIDE|[U]sers\s+[gG]uide|[U]ser\s+[gG]uide|User\'s\s+Guide\b',content)):
		ugOcc = re.split(r'\bUSERS\s+GUIDE|[Uu]sers\s+[gG]uide|[Uu]ser\s+[gG]uide|User\'s\s+Guide\b',content)
		dictFeatures['ugPos'] = len(ugOcc[0])/docLen
	else:
		dictFeatures['ugPos'] = -1

	if(re.search(r'\bGuide\b',content)):
		guideOcc = re.split(r'\bGuide\b',content)
		dictFeatures['guidePos'] = len(guideOcc[0])/docLen

	if(re.search(r'^[\s]*Keyword|Category|Terms|Keywords|KEYWORDS\b',content,re.M)):
		kctOcc = re.split(r'^[\s]*Keyword|Category|Terms|Keywords|KEYWORDS\b',content,re.M)
		dictFeatures['kctPos'] = len(kctOcc[0])/docLen
	else:
		dictFeatures['kctPos'] = -1

	if(re.search(r'^\s*CHAPTER|Chapter\b',content,re.M)):
		dictFeatures['chpCount'] = len(re.split(r'^\s*CHAPTER|Chapter\b',content,re.M))

	if(re.search(r'\bRevision|Version|Release\s+[0-9\.]+',content,re.M)):
		dictFeatures['rvBool'] = 1

	if(re.search(r'^[\w\s]*Newsletter|NEWSLETTER\b',content,re.M)):
		nwsOcc = re.split(r'^[\w\s]*Newsletter|NEWSLETTER\b',content,re.M)
		dictFeatures['nwsCount'] = len(nwsOcc)/wordCount
	else:
		dictFeatures['nwsCount'] = -1
	# Slides	
	if(re.search(r'^\s*SLIDE|SLIDES|Slide|Slides\b',content,re.M)):
		dictFeatures['sldCount'] = len(re.split(r'^\s*SLIDE|SLIDES|Slide|Slides\b',content,re.M))
	else:
		dictFeatures['sldCount'] = -1
	
	if(re.search(r'\bOutline|OUTLINE\b',content)):
		dictFeatures['outCount'] = len(re.split(r'\bOutline|OUTLINE\b',content))
	
	if(re.search(r'\bSubscription|Subscribe|Enrollment\b',content,re.I)):
		dictFeatures['seCount'] = len(re.split(r'\bSubscription|Subscribe|Enrollment\b',content,re.I))
	if(re.search(r'\bCredit|Lecture|Points|Marks|Grade\b',content)):
		dictFeatures['clpmgCount'] = len(re.split(r'\bCredit|Lecture|Points|Marks|Grade\b',content))/wordCount

	if re.search(r'\b(Lecture|Course)\s+Notes\b',content,re.I):
		dictFeatures['lcnote'] = len(re.split(r'\b(Lecture|Course)\s+Notes\b',content,re.I))

	# Rules
	dictFeatures['paperRule'] = paperRule
	dictFeatures['id'] = id
	return dictFeatures

def printFeatureStruct(d):
	dictKeys = d.keys()
	array = []
	for key in sorted(d.iterkeys()):
	    array.append(str(key)+":"+str(d[key]))
	print ",".join(array)

def printHeader(d,classlabel):
	kar = []
	for k in sorted(d.iterkeys()):
		if k != classlabel:
			kar.append(k)
	kar.append(classlabel)
	print ",".join(kar)

def printCSV(d,classlabel):
	dictKeys = d.keys()
	array = []
	for key in sorted(d.iterkeys()):
		if key != classlabel:
			array.append(str(d[key]))
	if(d.has_key(classlabel)):
		array.append(str(d[classlabel]))
	print ",".join(array)
	

def loadLabels(posLabelFile,negLabelFile):
	labelDict = {}
	try:
                posHandle = open(posLabelFile,'r')
        except IOError:
                return {}
        for precord in posHandle:
		labelDict[precord.rstrip()] = 1
	
	try:
		negHandle = open(negLabelFile,'r')
	except IOError:
		return {}
	for nrecord in negHandle:
		labelDict[nrecord.rstrip()] = -1
	return labelDict		
		

def loadURLS(urlFile):
	dicturl = {}
	try:
		fileHandle = open(urlFile,'r')
	except IOError:
		return {}
	for crecord in fileHandle:
		crecord = crecord.rstrip("\n")
		urlRecords = re.split(",",crecord)
		dicturl[urlRecords[0]] = urlRecords[1]
	return dicturl

#dURL = loadURLS(urlMapFile)
class Doc_Content_Filter(object):
	def __init__(self,tdir):		
   		self.textFileDir = tdir # text file directory

        # inf: text file used to classify document based on content
        # inurl: doc url where it is downloaded from
	def Decider(self,inf,inurl):
  		dURL = {inf:inurl}
		for root,dirs,files in os.walk(self.textFileDir):
			count = 0
			for f in files:
				#id = f
				#id = id[0:len(id)-4]
				fileName = os.path.join(root,f)
				if(dURL.has_key(f)):
                                        id = f[0:len(f)-4] # remove the last extension
					d = processFile(fileName,dURL[f],id)
					pred_lab = log_reg_classify(d)
					if (pred_lab > 0):
						#print id+',1'
						return 1
					else:
						#print id+',-1'
						return -1
