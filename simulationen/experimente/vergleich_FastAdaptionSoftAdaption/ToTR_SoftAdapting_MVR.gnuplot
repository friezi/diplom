unset parametric
plot 'totalTemporaryRequests_SoftAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_SoftAdapting_MeanValues.gnuplotdata' w l
