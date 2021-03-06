unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoBalancing_MeanValueRanges.gnuplotdata' w l title "Konfidenzintervalle"
load 'queue.gnuplot'
load 'markers.gnuplot'
replot queue
set title "Keine Ausbalancierung"
replot 'totalTemporaryRequests_NoBalancing_MeanValues.gnuplotdata' w l title "Mittelwerte"
