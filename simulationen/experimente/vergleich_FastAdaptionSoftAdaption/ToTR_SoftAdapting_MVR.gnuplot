unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SoftAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_DegradingUploadScalingFactor.gnuplot'
replot queue
replot 'totalTemporaryRequests_SoftAdapting_MeanValues.gnuplotdata' w l
