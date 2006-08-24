#! /bin/bash

for dir in vergleich_*
do
	python ../../tools/python/reset_pass.py $dir
done
