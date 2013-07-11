# Print information on screen. Types of information includes
# (*) status
#     [description] ------------- [status]
#     e.g.,
#     Item is found ----------- [YES] print in green
#     Record is new ----------- [NO]  print in yellow
#     File not found ---------- [FAIL] print in red
# (*) progress
#     e.g.,
#     [i]/[n] [description]
# (*) parameter
#          [parameter1] = [value1]
#          [parameter2] = [value2]
#     e.g.,
#              database = citeseer
# Colors are
#    HEADER = '\033[95m'  # cyan
#    OKBLUE = '\033[94m'  # blue
#    OKGREEN = '\033[92m' # green
#    WARNING = '\033[93m' # yellow
#    FAIL = '\033[91m'    # red
#    ENDC = '\033[0m'     # default color
#
class printInfo(object):

    # statuslength sets the length of the status "description", default is 40
    def __init__(self,statuslength=40,paralength=20):
	self.colors = {'HEADER':'\033[95m',\
        	'OKBLUE':'\033[94m',\
        	'OKGREEN':'\033[92m',\
        	'WARNING':'\033[93m',\
        	'FAIL':'\033[91m',\
        	'ENDC':'\033[0m'}
	self.statuslength = statuslength
	self.paralength = paralength

    # print status
    def printStatus(self,description,status):
    	# set status colors
	if status.upper() == 'YES' or status.upper() == 'OK' or\
	    status.upper() == 'SUCCESS' or status.upper() == 'TRUE':
	    status = self.colors['OKGREEN']+status.upper()+self.colors['ENDC']

  	elif status.upper() == 'NO' or status.upper() == 'WARNING':
	    status = self.colors['WARNING']+status.upper()+self.colors['ENDC']

	elif status.upper() == 'FAIL' or status.upper() == 'FALSE':
	    status = self.colors['FAIL']+status.upper()+self.colors['ENDC']

	else:
	    status = self.colors['HEADER']+status.upper()+self.colors['ENDC']

	# description will be truncated if it is longer than statuslength
   	# if it is shorter than statuslength, the rest is filled with dashes
	# note there is a space between the last character of the description
	description = description + ' '
	description = description.ljust(self.statuslength,'-')

	statusstring = description+' '+status
	print statusstring

    # print pgoress
    def printProgress(self,description,i,n):
	pass

    # print parameters
    # all parameters are using the "HEADER" color
    # note here at "valuestr" should be a string type, currently does not support 
    # string array. If it was not a string, it must be converted to a string
    # before calling this function
    def printPara(self,description,valuestr):
	# parameter name will be truncated if it is longer than paralength
	# if it is shorter then paralength, the parameter string is aligned
	# on the right (see the examples at the beginning of this module
	
	# color the value string
	valuestr = self.colors['HEADER']+valuestr+self.colors['ENDC']
	
	# adjust the parameter description string
	description = description.rjust(self.paralength,' ')

	parastring = description + " = " + valuestr
	print parastring
