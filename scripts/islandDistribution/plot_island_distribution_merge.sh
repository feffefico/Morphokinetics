#!/bin/bash

set -o nounset
set -o errexit
set -o pipefail
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

chunk=40
coverageLimit=31
mkdir -p islandDistribution

if [ $# -lt 2 ]
then
    echo "using default chunk size $chunk"
    echo "using default coverage $coverageLimit"
else
    chunk=$1
    coverageLimit=$2
fi

echo "#coverage time avgIslandSize avgNucleations avgIslandNumber avgNumberOfEvents avgRatios sumRatios" > out

for ((i=1;i<$coverageLimit;i++))
do 
    coverage=$(awk '{print $1}' islandDistribution/execTime_$i)
    time=$(awk '{print $2}' islandDistribution/execTime_$i)
    avgIslandSize=$(awk '{print $1}' islandDistribution/average_$i)
    avgIslandNumber=$(awk '{sum+=$4}END{print sum/NR}' islandDistribution/times_$i)
    avgNucleations=$(awk '{sum+=$3}END{print sum/NR}' islandDistribution/times_$i)
    avgNumberOfEvents=$(awk '{sum+=$8}END{print sum/NR}' islandDistribution/times_$i)
    avgRatios=$(awk '{sum+=$6}END{print sum/NR}' islandDistribution/times_$i)
    sumRatios=$(awk '{sum+=$9}END{print sum/NR}' islandDistribution/times_$i)
    
    echo "$coverage $time $avgIslandSize $avgNucleations $avgIslandNumber $avgNumberOfEvents $avgRatios $sumRatios" >> out
done

sort -n out > islandDistribution/islandSizeVsTime
rm out
