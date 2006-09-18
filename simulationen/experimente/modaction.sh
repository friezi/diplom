mod=`pwd`/modifyContent.sh

for dir in $*
do
	cd $dir
	for file in *.gnuplot
	do
	echo $file
		sed -i .bak -e 's/\[0:200\]/[-12:200]/g' $file
#		rm -f *.bak
	done
	cd ..
done
