set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_mpp3600_serviceTimeFactor50_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers_serviceTimeFactor50.gnuplot'
set output 'ToTR_mpp3600_serviceTimeFactor50_MVR.ps'
replot queue

