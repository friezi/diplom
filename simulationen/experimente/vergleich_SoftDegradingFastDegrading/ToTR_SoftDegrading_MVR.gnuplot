unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SoftDegrading_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SoftDegrading.gnuplot'
replot queue
replot 'totalTemporaryRequests_SoftDegrading_MeanValues.gnuplotdata' w l
