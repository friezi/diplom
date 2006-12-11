set terminal postscript
unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_Exponential_MeanValues.gnuplotdata' w l
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set output 'vgl_ToTR.ps'
replot 'totalTemporaryRequests_Polynomial_MeanValues.gnuplotdata' w l
