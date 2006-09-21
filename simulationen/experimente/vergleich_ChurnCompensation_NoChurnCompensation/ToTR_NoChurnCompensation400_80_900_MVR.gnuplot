unset parametric
set yrange [-12:200]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoChurnCompensation400_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers400_80_900.gnuplot'
replot queue
replot 'totalTemporaryRequests_NoChurnCompensation400_80_900_MeanValues.gnuplotdata' w l
