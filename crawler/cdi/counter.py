from pprint import pprint

# add a counter and add or subtract numbers from these counters
class Counter(object):
  def __init__(self):
    self.counternames = []
    self.newCounter('all')

  # set counter values
  def setCounter(self,countername,number=0):
    setattr(self,countername,number)

  # create a new counter named "countername"
  def newCounter(self,countername):
    setattr(self,countername,0)
    self.counternames.append(countername)

  # add a number to a counter (1 by default)
  def addCounter(self,countername,number=1):
    countervalue = getattr(self,countername)
    setattr(self,countername,countervalue+number)
    return getattr(self,countername)

  # subtract a number to a counter (1 by default)
  def subCounter(self,countername,number=1):
    countervalue = getattr(self,countername)
    setattr(self,countername,countervalue+number)
    return getattr(self,countername)

  # check if a counter has already existed
  def isanewCounter(self,countername):
    if countername in self.counternames:
      return False
    else:
      return True

  # print counters
  def printCounter(self):
    # determine the length of the longest countername
    # note that the 'list' function copies the reference only, not the list itself
    # so changing "allnames" does not change "self.counternames"
    allnames = list(self.counternames)
    allnames.append('Counter')
    field_length = len(max(allnames,key=len))
    header =  '| {title0:<'+str(field_length)+'} | {title1:<20} |'
    header = header.format(title0='Counter',title1='Value')
    horizontal_line = '+'+'-'*(field_length+2)+'+'+'-'*22+'+'

    print(horizontal_line)
    print(header)
    print(horizontal_line)
    for ct in self.counternames:
      entry_row = '| {counter:<'+str(field_length)+'} | {value:<20} |' 
      entry_row = entry_row.format(counter=ct,value=getattr(self,ct))
      horizontal_line = '+'+'-'*(field_length+2)+'+'+'-'*22+'+'

      print(entry_row)
      print(horizontal_line)
 
  def printCountertoFile(self,filename):
      allnames = list(self.counternames)
      allnames.append('Counter')
      field_length = len(max(allnames,key=len))
      header =  '{title0:<'+str(field_length)+'} {title1:<20}\n'
      header = header.format(title0='Counter',title1='Value')
      f = open(filename,'w')
      f.write(header) 
      for ct in self.counternames:
          entry_row = '{counter:<'+str(field_length)+'} {value:<20}\n'
          entry_row = entry_row.format(counter=ct,value=getattr(self,ct))
          f.write(entry_row)
      f.close()
