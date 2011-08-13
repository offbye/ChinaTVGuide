#!/bin/bash


cd /opt/phpMyAdmin/sh/p
d=$1
filename=$2
#d=`date -d '+1 day'   +%Y%m%d`
url=http://localhost/phpmy/tao/tvp.php
i=0
while read LINE
do
  echo $LINE
  wget -q  "$url?c=$LINE&d=$d&province=$filename"
  i=$i+1
  sleep 2
done <   $filename

rm -rf tvp.php?c*
