#!/usr/bin/python
# delete the directory after importing is finished
# If no parameter is provided, it will delete the "inputdir" in runconfig.py,
# otherwise, the user can submit a directory and it will be deleted.
# The user need to confirm the deletion
#
import runconfig
import commands
import os
def startup(verbal=True):
    print 'Directory to be deleted (in runconfig.py):\n', runconfig.inputdir
    yn = raw_input("Confirm to delete this directory [yes/no]: ")
    if yn == 'yes':
	if not os.path.exists(runconfig.inputdir):
	    print 'Directory does not exist or has already been deleted.'
	else:
	    rmcmd = 'rm -rfv '+runconfig.inputdir
	    rmoutput = commands.getoutput(rmcmd)
	    print rmoutput
            print 'Directory deleted.'
    elif yn == 'no':
	print 'Directory NOT deleted.'
    else:
	print 'Unrecoganizable input. Enter "yes" or "no".'

startup(verbal=True)
