unset parametric
plot 'totalTemporaryRequests_mpp910800_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_mpp910800_MeanValues.gnuplotdata' w l
