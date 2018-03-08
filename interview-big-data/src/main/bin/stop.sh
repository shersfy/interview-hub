#!/bin/bash
#author yuehan1@lenovo.com
#date 2016-04-28

mainClass=org.interview.big.data.mapreduce.APP
pid=`ps aux | grep $mainClass | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
  kill -9 $pid
  echo "big data stopped success"
else
  echo "No big data process exists"
fi
