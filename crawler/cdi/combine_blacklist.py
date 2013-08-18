#!/usr/local/bin/python2.6
# this small program combines blacklist and blacklist.exact
# reason to combine: 
# (1) they have different format
#      e.g., black_list: www\.socio\.com
#      e.g., black_list.exact: www.socio.com
# (2) they may overlap
#
# These two files are located at
# /export/csxcrawl/filter/
# The combined file is named as "black_list.dat" which should
# be used for delurl.py 
#
def startup():
  # initial setups
  black_listf = '/export/csxcrawl/filter/black_list'
  black_listexactf = '/export/csxcrawl/filter/black_list.exact'
  outputf = 'black_list.dat'
  black_listdat = []

  # load the first black_list file
  black_list = file(black_listf).readlines()
  black_listexact = file(black_listexactf).readlines()

  # loop over each host in black_list, convert host names 
  # to correct formats and append them to new black list
  for host in black_list:
    # remove the last '\n'
    host = host.strip()
    # but you add another backslash before '.'
    s = host.split('\\')
    j = ''.join(s)
    black_listdat.append(j)
 
  # loop over each host in black_list.exact, check for duplication
  # and only append new host to the new black list
  for host in black_listexact:
    # remove the last '\n'
    host = host.strip()
    # check for duplication
    if host not in black_listdat:
      print 'new host: ',host
      black_listdat.append(host)

  # delete output file if it has existed
  # write the new black_list into file
  f = open(outputf,'w')
  for host in black_listdat:
    f.write(host+'\n')

  f.close()
  print 'file output to: '+outputf

  
startup()
