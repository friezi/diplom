#! /usr/bin/python

import sys
import fileinput
import os
import os.path

seedsfile = "seeds"
seedvalue = ""
passvalue = 0

def cutNewline(line):
    retstring = line
    if line[len(line)-1] == '\n':
        retstring = line[0:len(line)-1]
    elif line[len(line)-1] == '\r':
        retstring = line[0:len(line)-1]
    else:
        return line
 
    if retstring[len(retstring)-1] == '\n':
        retstring = retstring[0:len(retstring)-1]
    elif retstring[len(retstring)-1] == '\r':
        retstring = retstring[0:len(retstring)-1]
        
    return retstring

if len(sys.argv) != 2:
    print "invalid syntax!"
    print "usage: " + os.path.basename(sys.argv[0]) + " <directory>"
    sys.exit(1)
    
dir = sys.argv[1]

try:
    for line in fileinput.input(seedsfile):
        seedvalue = cutNewline(line)
        passvalue+=1
        os.system("echo " + seedvalue + " > seed")
        os.system("echo " + str(passvalue) + " > pass")
        os.system(os.path.dirname(sys.argv[0]) + "/run_test.py " + dir)
        
except IOError:
    print 'file "' + seedsfile + '" not found'
    sys.exit(1)