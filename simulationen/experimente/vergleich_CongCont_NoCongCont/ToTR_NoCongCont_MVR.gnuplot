unset parametric
set yrange [0:350000]
set xlabel "Time/kticks"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoCongCont_MeanValueRanges.gnuplotdata' w l title "Konfidenzintervalle"
load 'queue.gnuplot'
replot queue
set title "Keine Staukontrolle"
replot 'totalTemporaryRequests_NoCongCont_MeanValues.gnuplotdata' w l title "Mittelwerte"
