unset parametric
plot 'totalTemporaryRequests_FastAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_FastAdapting_MeanValues.gnuplotdata' w l
