#! /usr/bin/python

import sys
import os
import linescanner
from propertyreader import PropertyReader, PropertyReaderException
from optparse import OptionParser

testscenarios = "testscenarios"
tempfile = "t.e.m.p.f.i.l.e"
infix = ".pass"

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option( '--dir', action='store', type='string', dest='dir' )
    parser.add_option( '--pass', action='store', type='string', dest='passvalue' )
    parser.add_option( '--errormail', action='store_true', dest='errormail' )
    parser.add_option( '--mem', action='store', type='string', dest='mem' )
    parser.add_option( '--seedfile', action='store', type='string', dest='seedfile' )
 
    ( options, args ) = parser.parse_args()
 
    if options.dir == None:
        print "Please give directory in '--dir='" 
        error = True
 
    if options.seedfile == None:
        print "Please give seedfile in '--seedfile='" 
        error = True
       
    if error == True:
        print "usage: " + os.path.basename( sys.argv[0] ) + " --dir=<directory> --seedfile=<seedfile> [--pass=<passvalue>] " \
                        + "[--errormail] [--mem=<memory_in_MB>]"
        sys.exit( 1 )
        
    return ( options, args )


""" main """

( options, args ) = parsecmdl()

seedfile = options.seedfile    
seedvalue = ""
passvalue = ""
mem = ""
errormail = 'false'
mailfile = 'mailfile'
execdir = os.path.dirname( sys.argv[0] )
me = 'ka1379-912@online.de'
account = '1und1'
subject = '\'DiscreteAndRealtimeSimulation: ERROR occured!\''
errorfile = 'error.log'
errorfilestring = ""

try:
    file = open( seedfile, 'r' )
    seedvalue = linescanner.token( 0, file )
    file.close()
except IOError:
    print 'file "seed" not found'
    sys.exit()
except linescanner.OutOfBoundsError, e:
    print e.value
    file.close()
    sys.exit()

if options.passvalue != None:
    passvalue=options.passvalue
    
if options.mem != None:
    mem = "-Xmx" + options.mem + "m -Xms" + options.mem + "m "
    
if options.errormail != None:
    errormail = 'true'
    
if errormail == 'true':
    errorfilestring = " 2>> " + errorfile + " "

dir = options.dir
os.chdir( dir )

print "entering directory " + dir

file = open( testscenarios, 'r' )
for simulation in linescanner.linetokens( file ):

    """ save original """
    original = simulation + ".orig"
    os.system("cp -f " + simulation + " " + original)

    if seedvalue != "":
        
        """ modify seed value """
        os.system( "sed -e 's/^[ ]*\(seedValue\)[ ]*=.*$/\\1=" + seedvalue +"/g' " + simulation + " > " + tempfile + errorfilestring )
        os.rename( tempfile, simulation )
 
    """ potentiellen infix entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + " -e 's/^[ ]*\(gnuplotFileAvgMsgDelayRatio\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + " -e 's/^[ ]*\(gnuplotFileRelReOmRatio\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + " -e 's/^[ ]*\(gnuplotFileAvgUptodateRatio\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + simulation + " > " + tempfile + errorfilestring )
    os.rename( tempfile, simulation )
        
    if passvalue != "":
        
        """ infix _pass<value> eintragen """
        os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g' "
                   + " -e 's/^[ ]*\(gnuplotFileAvgMsgDelayRatio\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g' "
                   + " -e 's/^[ ]*\(gnuplotFileRelReOmRatio\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g' "
                   + " -e 's/^[ ]*\(gnuplotFileAvgUptodateRatio\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g' "
                   + simulation + " > " + tempfile + errorfilestring )
        os.rename( tempfile, simulation )
    
    print "starting simulation " + simulation    
    if os.system( "java " + mem + "-cp ../../DiscreteAndRealtimeSimulation/ Simulation " + simulation + errorfilestring ) != 0:
        if errormail == 'true':
            """ mail """
            os.system( 'echo $HOSTNAME > ' + mailfile )
            os.system( 'echo ' + dir + ' >> ' + mailfile )
            os.system( 'echo "" >> ' + mailfile )
            os.system( 'echo  ERROR while running java Simulation in simulation ' + simulation + ': >> ' +  mailfile )
            os.system( 'cat ' + errorfile + ' >> mailfile' )
            os.system( "python ../" + execdir + "/mail.py --from=" + me + " --to=" + me + " --subject=" + subject + " --account="
                       + account + " --textfile=" + mailfile )
            os.remove( mailfile )

    """ restore original """
    os.rename( original, simulation )
   
file.close()
    
    
