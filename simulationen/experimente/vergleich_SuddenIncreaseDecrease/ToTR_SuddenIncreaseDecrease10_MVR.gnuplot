unset parametric
plot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValueRanges.gnuplotdata' w l
load 'queue.gnuplot'
replot queue
replot 'totalTemporaryRequests_SuddenDecreaseIncrease10_MeanValues.gnuplotdata' w l