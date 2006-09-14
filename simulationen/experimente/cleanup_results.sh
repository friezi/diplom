#! /bin/bash

#DIRS="vergleich_Balancing_NoBalancing vergleich_CongCont_NoCongCont vergleich_queuegroessen"

for dir in $*
do
	cd $dir
	rm -f *.gnuplotdata queue*.*gnuplot markers*.gnuplot *.bak *.log *.tgz
	cd ..
done

