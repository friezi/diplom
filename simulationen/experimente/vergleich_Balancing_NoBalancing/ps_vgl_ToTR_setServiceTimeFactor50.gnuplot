set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_Balancing_setServiceTimeFactor50_MeanValues.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_setServiceTimeFactor50.gnuplot'
replot queue
set output 'vgl_ToTR_setServiceTimeFactor50.ps'
replot 'totalTemporaryRequests_NoBalancing_setServiceTimeFactor50_MeanValues.gnuplotdata' w l
