#! /bin/bash

error_exit()
{

    echo 'usage: start_experiments.sh <experiments_file> <commmon_parameters_file> <seedsfile> [<memory_in_MB>]'
    exit

}

build_mailtext()
{

    echo $HOSTNAME > mailtext
    echo "calculation complete!" >> mailtext

    echo "" >> mailtext
    echo "results stored in file :" >> mailtext
    echo "${RESULTFILE}.gz" >> mailtext

    echo "" >> mailtext
    echo "common parameters:" >> mailtext
    cat ${common_parameters} >> mailtext

    echo "" >> mailtext
    echo "seeds:" >> mailtext
    cat ${seedsfile} >> mailtext

    echo "" >> mailtext
    echo "errors:" >> mailtext
    for dir in $DIRS
    do
      echo "" >> mailtext
      echo $dir ":" >> mailtext
      echo "" >> mailtext
      cat ${dir}/error.log >> mailtext
    done

}

DATE=`date --iso-8601`

SUFFIX=${HOSTNAME}_${DATE}

RESULTFILE=results_${SUFFIX}.tar

pytool=../../tools/python

if [[ $1 = "" ]]; then error_exit; fi
if [[ $2 = "" ]]; then error_exit; fi
if [[ $3 = "" ]]; then error_exit; fi

experiments_file=$1
common_parameters=$2
seedsfile=$3

if [[ $4 != "" ]]; then mem="--mem=$4"; fi

rm -f error.log

DIRS=`awk '!/(#.*)/ {print $1}' ${experiments_file}`

for dir in ${DIRS}
do
    python ${pytool}/set_common_parameters.py --dir=${dir} --common_parameters=${common_parameters}
    python ${pytool}/run_sequencetest.py --dir=${dir} --seedsfile=${seedsfile} --mail ${mem}
    python ${pytool}/restore_originals.py --dir=${dir}
done

tar cf ${RESULTFILE} ${common_parameters} ${seedsfile}

for dir in ${DIRS}
do
    tar rf ${RESULTFILE} ${dir}/results.tar.gz
done

gzip -9 ${RESULTFILE}

build_mailtext

python ${pytool}/mail.py --from=ka1379-912@online.de --to=ka1379-912@online.de --subject="DiscreteAndRealtimeSimulation: calculation complete!" \
			 --account=1und1 --textfile=mailtext
rm -f mailtext
echo final mail sent

