unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Balancing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_Balancing_MeanValues.gnuplotdata' w l