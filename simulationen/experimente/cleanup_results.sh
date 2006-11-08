#! /bin/bash

#DIRS="vergleich_Balancing_NoBalancing vergleich_CongCont_NoCongCont vergleich_queuegroessen"

for dir in $*
do
	pushd $dir
	for gnuplotdata in *.gnuplotdata
	do
		rm -f ${gnuplotdata}
	done
	rm -f queue*.*gnuplot
	rm -f markers*.gnuplot
	rm -f *.bak
	rm -f *.log
	rm -f *.tgz
	rm -f *.log
	popd
done

