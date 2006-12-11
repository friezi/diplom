set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_Exponential_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set output 'ToTR_Exponential_MVR.ps'
replot
