set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_mpp3600_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_mpp3600_MVR.ps'
replot queue

