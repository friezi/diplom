mod=`pwd`/modifyContent.sh

for dir in $*
do
	cd $dir
	for file in *.sim
	do
	echo $file
#		sed --in-place=.bak -e 's/seedValue=\(.*\)/seedValue=-7847005838091513385/g' $file
#		sed --in-place=.bak -e 's/processingTimeFeedRequests\(.*\)/\n/g' -e 's/processingTimeUnrepliedRequests\(.*\)/\n/g' -e 's/saveProperties\(.*\)=\(.*\)false/saveProperties=true/g' $file
		sed --in-place=.bak -e 's/BRITEToplayerFile\(.*\)=\(.*\)/BRITEBrokernetFile=\2/g' $file
#		rm -f *.bak
	done
	cd ..
done
