set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_NoBalancing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
set output 'ToTR_NoBalancing_MVR.ps'
replot
