#! /usr/bin/python

import sys
import os
import os.path
import linescanner
from optparse import OptionParser

tarfileprefix = "datas"
seedvalue = ""
passvalue = 0

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option( '--dir', action='store', type='string', dest='dir' )
    parser.add_option( '--seedsfile', action='store', type='string', dest='seedsfile' )
    parser.add_option( '--mail', action='store_true', dest='mail' )
    parser.add_option( '--mem', action='store', type='string', dest='mem' )
 
    ( options, args ) = parser.parse_args()
 
    if options.dir == None:
        print "Please give directory in '--dir='" 
        error = True
  
    if options.seedsfile == None:
        print "Please give seedsfile in '--seedsfile='" 
        error = True
        
    if error == True:
        print "usage: " + os.path.basename( sys.argv[0] ) + " --dir=<directory> --seedsfile=<seedsfile> [--mem=<memory_in_MB>] [--mail]"
        sys.exit( 1 )
        
    return ( options, args )

""" main """

mail = 'false'
errormail = ""
mem = ""

( options, args ) = parsecmdl()
    
dir = options.dir
execdir = os.path.dirname( sys.argv[0] )
seedsfile = options.seedsfile

if options.mail != None:
    mail = 'true'
    errormail = ' --errormail '
    
if options.mem != None:
    mem = "--mem=" + options.mem + " "
     
me = 'ka1379-912@online.de'
account = '1und1'
subject = '\'DiscreteAndRealtimeSimulation: sequence-test finished!\''
errorsubject = '\'DiscreteAndRealtimeSimulation: ERROR occured!\''
errorfile = 'error.log'
mailfile = 'mailfile'

try:
    
    file = open( seedsfile, 'r' )
    
    for seedvalue in linescanner.linetokens( file ):

        passvalue+=1
        os.system( "echo " + seedvalue + " > seed" )
        print "pass = " , passvalue
        print "seedvalue = ", seedvalue
        os.system( "python " + execdir + "/run_test.py " + "--dir=" + dir + " " + mem + "--pass=" + str( passvalue ) + errormail )
        
    file.close()
        
    """ confidence-intervals """
    if os.system( 'java -cp ' + execdir + '/../java:' + execdir + "/../java/commons-math-1.1.jar ConfidenceIntervalCalculator " + dir ) != 0:
        if mail == 'true':
            os.system( 'echo $HOSTNAME > ' + mailfile )
            os.system( 'echo ' + dir + ' >> ' + mailfile )
            os.system( 'echo "" >> ' + mailfile )
            os.system( 'echo  ERROR: >> ' +  mailfile )
            os.system( 'cat ' + errorfile + ' >> mailfile' )
            os.system( "python " + execdir + "/mail.py --from=" + me + " --to=" + me + " --subject=" + errorsubject + " --account="
                   + account + " --textfile=" + mailfile )
            print 'mail sent'
            os.remove( mailfile )
 
    """ gnuplot """
    os.system( "python " + execdir + "/exec_gnuplotfile.py " + dir )
 
    """ tar-archive """
    olddir = os.getcwd()
    newdir = dir
    os.chdir( newdir )
    print "building tar-archive " + tarfileprefix + ".tgz of generated data-files"
    os.system( 'tar cfz ' + tarfileprefix + '.tgz' + ' *.gnuplotdata queue*.gnuplot *.ps' )
    os.chdir( olddir )
    
    """ mail """
    if mail == 'true':
        os.system( 'echo $HOSTNAME > ' + mailfile )
        os.system( 'echo ' + dir + ' >> ' + mailfile )
        os.system( "python " + execdir + "/mail.py --from=" + me + " --to=" + me + " --subject=" + subject + " --account="
                   + account + " --textfile=" + mailfile )
        print 'mail sent'
        os.remove( mailfile )
        
except IOError:
    print 'file "' + seedsfile + '" not found'
    sys.exit( 1 )