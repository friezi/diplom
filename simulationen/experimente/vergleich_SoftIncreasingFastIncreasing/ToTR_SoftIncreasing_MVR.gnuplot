unset parametric
plot 'totalTemporaryRequests_SoftIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_SoftIncreasing_MeanValues.gnuplotdata' w l
