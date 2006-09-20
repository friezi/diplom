set terminal postscript
unset parametric
set yrange [-12:200]
plot 'totalTemporaryRequests_FastAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_DegradingUploadScalingFactor.gnuplot'
set output 'ToTR_FastAdapting_MVR.ps'
replot queue

