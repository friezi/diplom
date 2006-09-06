unset parametric
plot 'totalTemporaryRequests_FastIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_FastIncreasing_MeanValues.gnuplotdata' w l
