unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SoftIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SoftIncreasing.gnuplot'
replot queue
replot 'totalTemporaryRequests_SoftIncreasing_MeanValues.gnuplotdata' w l
