unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_FastDegrading_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_FastDegrading_MeanValues.gnuplotdata' w l
