set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_NoChurnCompensation_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
set output 'ToTR_NoChurnCompensation_MVR.ps'
replot 'totalTemporaryRequests_NoChurnCompensation_MeanValues.gnuplotdata' w l
