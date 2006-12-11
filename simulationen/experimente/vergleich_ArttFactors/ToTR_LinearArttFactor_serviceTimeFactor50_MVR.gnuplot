unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_LinearArttFactor_serviceTimeFactor50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_serviceTimeFactor50.gnuplot'
replot queue
replot 'totalTemporaryRequests_LinearArttFactor_serviceTimeFactor50_MeanValues.gnuplotdata' w l
