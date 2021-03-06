#! /usr/bin/python

import sys
import os
import os.path
import linescanner
from optparse import OptionParser

tarfileprefix = "results"
seedfile = 's.e.e.d'
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
        print "usage: " + os.path.basename( sys.argv[0] ) + \
            " --dir=<directory> --seedsfile=<seedsfile> [--mem=<memory_in_MB>] [--mail]"
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
seedfile = dir + '/' + seedfile

if options.mail != None:
    mail = 'true'
    errormail = ' --errormail '
    
if options.mem != None:
    mem = " --mem=" + options.mem
     
me = 'ka1379-912@online.de'
account = '1und1'
subject = '\'DiscreteAndRealtimeSimulation: sequence-test finished!\''
errorsubject = '\'DiscreteAndRealtimeSimulation: ERROR occured!\''
errorfile = 'error.log'
errorfilestring = ""
mailfile = 'mailfile'

if mail == 'true':
    errorfilestring = " 2>>" + dir + "/" + errorfile + " "

try:
    
    file = open( seedsfile, 'r' )

    """ run test """    
    for seedvalue in linescanner.linetokens( file ):

        passvalue+=1
        os.system( "echo " + seedvalue + " > " + seedfile )
        print "pass = " , passvalue
        print "seedvalue = ", seedvalue
        os.system( "python " + execdir + "/run_test.py" + " --seedfile=" + seedfile + " --dir=" + dir
                   + mem + " --pass=" + str( passvalue ) + errormail + errorfilestring )
        os.remove( seedfile )
        
    file.close()
        
    """ confidence-intervals """
    if os.system( 'java -cp ' + execdir + '/../java:' + execdir + '/../java/commons-math-1.1.jar ConfidenceIntervalCalculator '
                  + dir + errorfilestring ) != 0:
        if mail == 'true':
            os.system( 'echo $HOSTNAME > ' + mailfile )
            os.system( 'echo ' + dir + ' >> ' + mailfile )
            os.system( 'echo "" >> ' + mailfile )
            os.system( 'echo  ERROR while running java ConfidenceIntervalCalculator for dir ' + dir + ': >> ' +  mailfile )
            os.system( 'cat ' + errorfile + ' >> mailfile' )
            os.system( "python " + execdir + "/mail.py --from=" + me + " --to=" + me + " --subject=" + errorsubject + " --account="
                   + account + " --textfile=" + mailfile + errorfilestring )
            print 'mail sent'
            os.remove( mailfile )
 
    """ gnuplot """
    os.system( "python " + execdir + "/exec_gnuplotfile.py --dir=" + dir + errorfilestring )
 
    """ tar-archive """
    olddir = os.getcwd()
    newdir = dir
    os.chdir( newdir )
    newerrorfilestring = ""
    if mail == 'true':
        newerrorfilestring = " 2>>" + errorfile + " "
    print "building tar-archive " + tarfileprefix + ".tar of generated data-files"
    os.system( 'tar cf ' + tarfileprefix + '.tar' + ' *.gnuplotdata *.gnuplot *.ps *.cfg *.sim *.al testscenarios gnuplotfiles'
                + newerrorfilestring )
    print "compressing tar-file " + tarfileprefix + '.tar' 
    os.system( 'gzip -9 ' + tarfileprefix + '.tar' )
    os.chdir( olddir )
    
    """ mail """
    if mail == 'true':
        os.system( 'echo $HOSTNAME > ' + mailfile )
        os.system( 'echo ' + dir + ' >> ' + mailfile )
        os.system( "python " + execdir + "/mail.py --from=" + me + " --to=" + me + " --subject=" + subject + " --account="
                   + account + " --textfile=" + mailfile + errorfilestring )
        print 'mail sent'
        os.remove( mailfile )
        
except IOError:
    sys.stderr.write( 'file "' + seedsfile + '" not found\n' )
    sys.exit( 1 )