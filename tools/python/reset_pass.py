#! /usr/bin/python
import os
import sys
import linescanner
import fileinput

tempfile = "t.e.m.p.f.i.l.e"
infix = ".pass"
testscenarios = "testscenarios"

if len(sys.argv) != 2:
    print "usage: " + os.path.basename( sys.argv[0] ) + ' <directory>'
    sys.exit()
    
os.chdir(sys.argv[1])


input = fileinput.input(testscenarios)
for simulation in linescanner.linetokens( input ):

    """ potentiellen infix entfernen """
    os.system( "sed -e 's/^[ ]*\(gnuplotFileCoeffVarCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
                + " -e 's/^[ ]*\(gnuplotFileMeanValueCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
                + " -e 's/^[ ]*\(gnuplotFileStdDevCPP\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g'"
                + " -e 's/^[ ]*\(gnuplotFileTotalTemporaryRequests\)[ ]*=\(.*\)" + infix + ".*\(.gnuplotdata\)$/\\1=\\2\\3/g' "
                + simulation + " > " + tempfile )
    os.rename( tempfile, simulation )
