unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_Balancing_setServiceTimeFactor50_1_MeanValues.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_setServiceTimeFactor50_1.gnuplot'
replot queue
replot 'totalTemporaryRequests_NoBalancing_setServiceTimeFactor50_1_MeanValues.gnuplotdata' w l
