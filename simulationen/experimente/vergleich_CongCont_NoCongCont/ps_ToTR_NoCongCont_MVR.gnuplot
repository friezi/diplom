set terminal postscript
unset parametric
set yrange [0:200000]
set output 'ToTR_NoCongCont_MVR.ps'
plot 'totalTemporaryRequests_NoCongCont_MeanValueRanges.gnuplotdata' w l

