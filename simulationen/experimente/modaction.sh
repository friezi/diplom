mod=`pwd`/modifyContent.sh

for dir in $*
do
	cd $dir
	for file in mac*
	do
	echo $file
		sed -i .bak -e 's/seedValue=\(.*\)/seedValue=-7847005838091513385/g' $file
#		rm -f *.bak
	done
	cd ..
done
