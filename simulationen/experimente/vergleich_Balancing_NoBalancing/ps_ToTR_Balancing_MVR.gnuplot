set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Balancing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
set output 'ToTR_Balancing_MVR.ps'
replot 'totalTemporaryRequests_Balancing_MeanValues.gnuplotdata' w l
