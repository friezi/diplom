unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_FastIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_FastIncreasing.gnuplot'
replot queue
replot 'totalTemporaryRequests_FastIncreasing_MeanValues.gnuplotdata' w l
