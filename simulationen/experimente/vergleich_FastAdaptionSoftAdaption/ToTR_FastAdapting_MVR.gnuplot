unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_FastAdapting_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_DegradingUploadScalingFactor.gnuplot'
replot queue
replot 'totalTemporaryRequests_FastAdapting_MeanValues.gnuplotdata' w l
