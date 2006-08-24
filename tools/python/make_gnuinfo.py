#! /usr/bin/python

from dircache  import listdir
from re import  compile,match
from optparse import OptionParser
from time import mktime,localtime
import sys

xmlpattern = compile("^.*\.xml")
extractpattern = compile("^.*\+(.*)\+s(.*),_(.*),_(.*),_(.*),_(.*),_(.*),_(.*),_(.*),_(.*)e.*")
intpattern = compile("^(.*)\..*")

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option('--dir',action='store',type='string',dest='dir')
    (options,args) = parser.parse_args()

    if options.dir == None:
        print "Please give direcory in '--dir='" 
        error = True
        
    if error == True:
        sys.exit(1)
        
    return (options,args)

def extractInfo(file):
    """ Extracts event- and timeinfo.
    
    """
    
    match = extractpattern.match(file)
    
    event = int(match.group(1))
    time = int(match.group(2)),int(match.group(3)),int(match.group(4)),int(match.group(5)),int(match.group(6)),\
           int(match.group(7)),int(match.group(8)),int(match.group(9)),int(match.group(10))
           
    secs = int(intpattern.match(str(mktime(time))).group(1))
    
    return (event,secs)

""" main """

(options,args) = parsecmdl()

files = listdir(options.dir)
gnuplotdatafilename = options.dir + "/" + options.dir + "_rss.gnuplotdata"
gnuplotcommandsfilename = options.dir + "/" + options.dir + "_rss.gnuplot"

""" insert all events """
maxevent = 0
events = {}

for file in files:
    if xmlpattern.match(file) != None:
        (event,time) = extractInfo(file)
        events[event] = time
        if event > maxevent:
            maxevent = event
        
gnuplotdatafile = open(gnuplotdatafilename,'w')

if len(events) > 0:
    startingmillis = events[1]
    startinghour = localtime(startingmillis)[3]
    startingsecs = localtime(startingmillis)[4]
else:
    sys.exit()

""" time in minutes """
for event in range(1,maxevent+1):
    gnuplotdatafile.write(str((events[event] - startingmillis) / 3600.0) + " " + str(event) + "\n")
    
gnuplotdatafile.close()

gnuplotcommandsfile = open(gnuplotcommandsfilename,'w')

gnuplotcommandsfile.write("unset parametric\n")
gnuplotcommandsfile.write("set xlabel \"time/hrs -- starttime: " + str(startinghour) + ":" + str(startingsecs) + "h\"\n")
gnuplotcommandsfile.write("set ylabel \"events\"\n")
gnuplotcommandsfile.write("plot '" + gnuplotdatafilename + "' w lp\n")

gnuplotcommandsfile.close()


