set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SoftDegrading_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SoftDegrading.gnuplot'
set output 'ToTR_SoftDegrading_MVR.ps'
replot queue

