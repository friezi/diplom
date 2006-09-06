set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_SoftDegrading_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
set output 'ToTR_SoftDegrading_MVR.ps'
replot queue

