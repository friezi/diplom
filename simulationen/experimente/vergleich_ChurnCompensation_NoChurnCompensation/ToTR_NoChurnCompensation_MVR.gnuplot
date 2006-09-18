unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_NoChurnCompensation_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
replot 'totalTemporaryRequests_NoChurnCompensation_MeanValues.gnuplotdata' w l
