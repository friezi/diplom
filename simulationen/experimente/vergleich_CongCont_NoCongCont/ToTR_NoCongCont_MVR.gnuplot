unset parametric
set yrange [0:200000]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
plot 'totalTemporaryRequests_NoCongCont_MeanValueRanges.gnuplotdata' w l

