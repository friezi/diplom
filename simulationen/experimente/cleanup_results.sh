#! /bin/bash

#DIRS="vergleich_Balancing_NoBalancing vergleich_CongCont_NoCongCont vergleich_queuegroessen"

for dir in vergleich_*
do
	cd $dir
	rm -f *.gnuplotdata queue*.*gnuplot *.bak
	cd ..
done

