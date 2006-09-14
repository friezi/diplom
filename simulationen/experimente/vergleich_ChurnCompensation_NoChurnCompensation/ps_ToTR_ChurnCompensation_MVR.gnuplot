set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_ChurnCompensation_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set output 'ToTR_ChurnCompensation_MVR.ps'
replot
