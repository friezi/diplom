unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_mpp3600_serviceTimeFactor50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_serviceTimeFactor50.gnuplot'
replot queue
replot 'totalTemporaryRequests_mpp3600_serviceTimeFactor50_MeanValues.gnuplotdata' w l
