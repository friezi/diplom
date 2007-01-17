unset parametric
set yrange [-12:200]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_ChurnCompensation50_80_900_MeanValueRanges.gnuplotdata' w l title "Konfidenzintervalle"
load 'queue.gnuplot'
load 'markers50_80_900.gnuplot'
replot queue
set title "Churn-Kompensation"
replot 'totalTemporaryRequests_ChurnCompensation50_80_900_MeanValues.gnuplotdata' w l title "Mittelwerte"
