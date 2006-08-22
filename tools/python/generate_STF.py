#! /usr/bin/python
from optparse import OptionParser
import sys

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option('--starttime',action='store',type='int',dest='starttime')
    parser.add_option('--timestep',action='store',type='int',dest='timestep')
    parser.add_option('--startvalue',action='store',type='int',dest='startvalue')
    parser.add_option('--endvalue',action='store',type='int',dest='endvalue')
    parser.add_option('--step',action='store',type='int',dest='step')
    (options,args) = parser.parse_args()
    if options.starttime == None:
        print "Please give starttime in '--starttime='" 
        error = True
    if options.timestep == None:
        print "Please give timestep in '--timestep='"
        error = True   
    if options.startvalue == None:
        print "Please give startvalue in '--startvalue='"
        error = True   
    if options.endvalue == None:
        print "Please give endvalue in '--endvalue='"
        error = True   
    if options.step == None:
        print "Please give step in '--step='"
        error = True   
        
    if error == True:
        sys.exit(1)
        
    return (options,args)

(options,args) = parsecmdl()

time = options.starttime
timestep = options.timestep
for n in range(options.startvalue,options.endvalue,options.step):
    print str(time) + " : " + "setServiceTimeFactor(" + str(n) + ")"
    time+=timestep 
