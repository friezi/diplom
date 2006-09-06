#! /bin/bash

tooldir=../../../tools/python

nohup ${tooldir}/get_rss.py  --dir=nytimes --interval=60 --minutes=1440 &
nohup ${tooldir}/get_rss.py  --dir=slashdot --interval=60 --minutes=1440 &
nohup ${tooldir}/get_rss.py  --dir=sourceforge --interval=60 --minutes=1440 &
nohup ${tooldir}/get_rss.py  --dir=spiegel --interval=60 --minutes=1440 &
nohup ${tooldir}/get_rss.py  --dir=heise --interval=60 --minutes=1440 &

