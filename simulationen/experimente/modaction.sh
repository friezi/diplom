mod=`pwd`/modifyContent.sh

for dir in $*
do
	cd $dir
	for file in mac*
	do
	echo $file
#		sed --in-place=.bak -e 's/seedValue=\(.*\)/seedValue=-7847005838091513385/g' $file
#		sed --in-place=.bak -e 's/processingTimeFeedRequests\(.*\)/\n/g' -e 's/processingTimeUnrepliedRequests\(.*\)/\n/g' -e 's/saveProperties\(.*\)=\(.*\)false/saveProperties=true/g' $file
		sed --in-place=.bak -e 's/saveProperties\(.*\)=\(.*\)true/saveProperties=false/g' $file
#		rm -f *.bak
	done
	cd ..
done
