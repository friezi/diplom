set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_NoChurnCompensation50_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
set output 'ToTR_NoChurnCompensation50_80_900_MVR.ps'
replot 'totalTemporaryRequests_NoChurnCompensation50_80_900_MeanValues.gnuplotdata' w l
