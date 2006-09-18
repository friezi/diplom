set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_SoftAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_DegradingUploadScalingFactor'
set output 'ToTR_SoftAdapting_MVR.ps'
replot queue

