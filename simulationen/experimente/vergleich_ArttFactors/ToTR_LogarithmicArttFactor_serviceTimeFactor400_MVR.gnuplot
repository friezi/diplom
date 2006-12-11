unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_LogarithmicArttFactor_serviceTimeFactor400_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_serviceTimeFactor400.gnuplot'
replot queue
replot 'totalTemporaryRequests_LogarithmicArttFactor_serviceTimeFactor400_MeanValues.gnuplotdata' w l
