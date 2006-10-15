unset parametric
set yrange [-12:200]
set xrange [0:6000]
set xlabel "Time/secs"
set ylabel "RSSFeed-Requests"
set xzeroaxis
set arrow from 1,-6 to 1,0
set label "ST(50.0)" at 1,-8
set arrow from 600,-6 to 600,0
set label "JB" at 600,-8
set arrow from 1001,-6 to 1001,0
set label "JE" at 1001,-8
set label "<Aktionsbereich>" at 2010,-5
set label "<Anfragen in der Queue>" at 2010,20
set label "<Verworfene Anfragen>" at 2010,60
queue=40
plot queue
set terminal postscript
set output 'legende.ps'
replot
