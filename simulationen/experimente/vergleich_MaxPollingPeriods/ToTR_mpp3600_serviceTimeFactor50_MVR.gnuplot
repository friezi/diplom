unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_mpp3600_serviceTimeFactor50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_mpp3600_serviceTimeFactor50_MeanValues.gnuplotdata' w l