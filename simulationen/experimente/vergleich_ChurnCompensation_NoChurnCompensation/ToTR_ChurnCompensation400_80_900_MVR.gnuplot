unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_ChurnCompensation400_80_900_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers400_80_900.gnuplot'
replot queue
replot 'totalTemporaryRequests_ChurnCompensation400_80_900_MeanValues.gnuplotdata' w l
