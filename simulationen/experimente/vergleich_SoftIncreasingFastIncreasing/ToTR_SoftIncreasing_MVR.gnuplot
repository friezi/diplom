unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_SoftIncreasing_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_SoftIncreasing.gnuplot'
replot queue
replot 'totalTemporaryRequests_SoftIncreasing_MeanValues.gnuplotdata' w l
