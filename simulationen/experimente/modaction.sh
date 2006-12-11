mod=`pwd`/modifyContent.sh

for dir in $*
do

	if [[ ! -d ${dir} ]]
	then
		continue
	fi

	pushd $dir
	for file in *.gnuplot
	do
	echo $file
#		sed --in-place=.bak -e 's/seedValue=\(.*\)/seedValue=-7847005838091513385/g' $file
#		sed --in-place=.bak -e 's/processingTimeFeedRequests\(.*\)/\n/g' -e 's/processingTimeUnrepliedRequests\(.*\)/\n/g' -e 's/saveProperties\(.*\)=\(.*\)false/saveProperties=true/g' $file
		sed --in-place=.bak -e 's/Time\/secs/Time\/ticks/g' $file
#		rm -f *.bak
	done
	popd
done
