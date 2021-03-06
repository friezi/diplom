set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoChurnCompensation400_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers400_80_900.gnuplot'
replot queue
set output 'ToTR_NoChurnCompensation400_80_900_MVR.ps'
replot
