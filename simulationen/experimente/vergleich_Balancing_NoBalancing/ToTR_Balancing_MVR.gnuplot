unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_Balancing_MeanValueRanges.gnuplotdata' w l title "Konfidenzintervalle"
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set title "Ausbalancierung"
replot 'totalTemporaryRequests_Balancing_MeanValues.gnuplotdata' w l title "Mittelwerte"
