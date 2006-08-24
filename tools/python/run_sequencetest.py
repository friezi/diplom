#! /usr/bin/python

import sys
import os
import os.path
import linescanner
import fileinput

seedsfile = "seeds"
seedvalue = ""
passvalue = 0

""" main """

mail = 'false'

if ( len(sys.argv) < 2 or len(sys.argv) >3 ):
    print "invalid syntax!"
    print "usage: " + os.path.basename(sys.argv[0]) + " <directory> [-mail]"
    sys.exit(1)
    
dir = sys.argv[1]
execdir = os.path.dirname(sys.argv[0])

if len(sys.argv) == 3:
    if sys.argv[2] == '-mail':
        mail = 'true'
     
me = 'ka1379-912@online.de'
account = '1und1'
subject = '\'sequence-test finished!\''
mailfile = 'mailfile'

try:
    
    input = fileinput.input(seedsfile)
    
    for seedvalue in linescanner.linetokens(input):

        passvalue+=1
        os.system("echo " + seedvalue + " > seed")
        print "pass = " ,passvalue
        print "seedvalue = ",seedvalue
        os.system("python " + execdir + "/run_test.py " + dir + " " + str(passvalue))
        
    input.close()
        
    os.system('java -cp ' + execdir + '/../java:' + execdir + "/../java/commons-math-1.1.jar ConfidenceIntervalCalculator " + dir)
    
    os.system("python " + execdir + "/exec_gnuplotfile.py " + dir)

    os.system('echo ' + dir + ' > ' + mailfile)
    
    if mail == 'true':
        os.system("python " + execdir + "/mail.py --from=" + me + " --to=" + me + " --subject=" + subject + " --account=" + account + " --textfile=" + mailfile)
        print 'mail sent'
        os.remove(mailfile)
        
except IOError:
    print 'file "' + seedsfile + '" not found'
    sys.exit(1)