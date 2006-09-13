set terminal postscript
unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_FastDegrading_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_FastDegrading.gnuplot'
set output 'ToTR_FastDegrading_MVR.ps'
replot queue

