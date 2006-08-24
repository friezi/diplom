#! /usr/bin/python

import sys
import fileinput
import os
import linescanner

testscenarios = "testscenarios"
seedfile = "seed"
tempfile = "t.e.m.p.f.i.l.e"
infix = ".pass"


""" main """

if len( sys.argv ) < 2 & len( sys.argv ) > 3:
    print "invalid syntax!"
    print "usage: " + os.path.basename( sys.argv[0] ) + ' <directory> [<pass>]'
    sys.exit( 1 )
    
seedvalue = ""
passvalue = ""

try:
    input = fileinput.input(seedfile)
    seedvalue = linescanner.token(0,input)
    input.close()
except IOError:
    print 'file "seed" not found'
    sys.exit()
except linescanner.OutOfBoundsError, e:
    print e.value
    input.close()
    sys.exit()

if len( sys.argv ) == 3:
    passvalue=sys.argv[2]

dir = sys.argv[1]
os.chdir( dir )

print "entering directory " + dir

input = fileinput.input(testscenarios)
for simulation in linescanner.linetokens( input ):

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
    os.system( "java -cp ../../DiscreteAndRealtimeSimulation/ Simulation " + simulation )

    """ infix wieder entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
               + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
               + simulation + " > " + tempfile )
    os.rename( tempfile, simulation )
    
input.close()
    
    
