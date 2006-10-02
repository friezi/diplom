unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_Exponential_MeanValues.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
replot 'totalTemporaryRequests_Polynomial_MeanValues.gnuplotdata' w l
