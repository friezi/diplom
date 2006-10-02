set terminal postscript
unset parametric
set yrange [0:350000]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoCongCont_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
set output 'ToTR_NoCongCont_MVR.ps'
replot
