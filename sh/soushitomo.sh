#!/bin/bash


cd /opt/phpMyAdmin/sh

filename=tvprogram
d=`date -d '+1 day'   +%Y%m%d`
url=http://localhost/phpmy/tao/soushi.php
i=0
while read LINE
do
  echo $LINE
  wget -q  "$url?c=$LINE&d=$d&province=$filename"
  i=$i+1
  sleep 2
done <   $filename

rm -rf soushi.php?c*
