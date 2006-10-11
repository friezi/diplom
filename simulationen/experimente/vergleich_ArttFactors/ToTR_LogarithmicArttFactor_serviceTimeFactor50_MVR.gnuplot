unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_LogarithmicArttFactor_serviceTimeFactor50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_serviceTimeFactor50.gnuplot'
replot queue
replot 'totalTemporaryRequests_LogarithmicArttFactor_serviceTimeFactor50_MeanValues.gnuplotdata' w l
