unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_NoBalancing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_NoBalancing_MeanValues.gnuplotdata' w l
