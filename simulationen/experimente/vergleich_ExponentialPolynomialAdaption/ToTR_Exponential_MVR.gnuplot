unset parametric
set yrange [0:200]
plot 'totalTemporaryRequests_Exponential_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_Exponential_MeanValues.gnuplotdata' w l
