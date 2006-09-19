unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SubscribersLeave50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
replot 'totalTemporaryRequests_SubscribersLeave50_MeanValues.gnuplotdata' w l
