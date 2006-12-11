set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/ticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoBalancing_setServiceTimeFactor50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_setServiceTimeFactor50.gnuplot'
replot queue
set output 'ToTR_NoBalancing_setServiceTimeFactor50_MVR.ps'
replot
