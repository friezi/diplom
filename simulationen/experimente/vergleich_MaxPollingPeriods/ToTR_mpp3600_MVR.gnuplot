unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_mpp3600_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_mpp3600_MeanValues.gnuplotdata' w l
