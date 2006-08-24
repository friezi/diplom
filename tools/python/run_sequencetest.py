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

""" main """

if len(sys.argv) != 2:
    print "invalid syntax!"
    print "usage: " + os.path.basename(sys.argv[0]) + " <directory>"
    sys.exit(1)
    
dir = sys.argv[1]
execdir = os.path.dirname(sys.argv[0])
me = 'ka1379-912@online.de'
account = '1und1'
subject = '\'sequence-test finished!\''
mailfile = 'mailfile'

try:
    for line in fileinput.input(seedsfile):
        seedvalue = cutNewline(line)
        passvalue+=1
        os.system("echo " + seedvalue + " > seed")
        print "pass = " ,passvalue
        os.system("python " + execdir + "/run_test.py " + dir + " " + str(passvalue))
        
    os.system('java -cp ' + execdir + '/../java:' + execdir + "/../java/commons-math-1.1.jar ConfidenceIntervalCalculator " + dir)

    os.system('echo ' + dir + ' > ' + mailfile)
    os.system("python " + execdir + "/mail.py --from=" + me + " --to=" + me + " --subject=" + subject + " --account=" + account + " --textfile=" + mailfile)
    print 'mail sent'
    os.remove(mailfile)
        
except IOError:
    print 'file "' + seedsfile + '" not found'
    sys.exit(1)