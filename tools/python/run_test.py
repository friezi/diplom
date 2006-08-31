#! /usr/bin/python

import sys
import os
import linescanner
from optparse import OptionParser

testscenarios = "testscenarios"
seedfile = "seed"
tempfile = "t.e.m.p.f.i.l.e"
infix = ".pass"

def parsecmdl():
    
    error = False
    
    """ parse commandline """
    parser = OptionParser()
    parser.add_option( '--dir', action='store', type='string', dest='dir' )
    parser.add_option( '--pass', action='store', type='string', dest='passvalue' )
    parser.add_option( '--mem', action='store', type='string', dest='mem' )
 
    ( options, args ) = parser.parse_args()
 
    if options.dir == None:
        print "Please give directory in '--dir='" 
        error = True
       
    if error == True:
        print "usage: " + os.path.basename( sys.argv[0] ) + " --dir=<directory> [--pass=<passvalue>] [--mem=<memory_in_MB>]"
        sys.exit( 1 )
        
    return ( options, args )


""" main """

(options,args) = parsecmdl()
    
seedvalue = ""
passvalue = ""
mem = ""

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

dir = options.dir
os.chdir( dir )

print "entering directory " + dir

file = open( testscenarios, 'r' )
for simulation in linescanner.linetokens( file ):

    if seedvalue != "":
        os.system( "sed -e 's/^[ ]*\(seedValue\)[ ]*=.*$/\\1=" + seedvalue +"/g' " + simulation + " > " + tempfile )
        os.rename( tempfile, simulation )
 
    """ potentiellen infix entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + simulation + " > " + tempfile )
    os.rename( tempfile, simulation )
        
    if passvalue != "":
        
        """ infix _pass<value> eintragen """
        os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g'"
                   + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)\(.gnuplotdata\)$/\\1=\\2" + infix + passvalue +"\\3/g' "
                   + simulation + " > " + tempfile )
        os.rename( tempfile, simulation )
    
    print "starting simulation " + simulation    
    os.system( "java " + mem + "-cp ../../DiscreteAndRealtimeSimulation/ Simulation " + simulation )

    """ infix wieder entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + simulation + " > " + tempfile )
    os.rename( tempfile, simulation )
    
file.close()
    
    
