#!/bin/bash

filename="cctv.txt"
#d=`date  +%Y%m%d`
d=20091220
url=http://localhost/phpmy/tao/tv1.php
i=0
while read LINE
do
  echo $LINE
  wget -q  $url?c=$LINE"&d="$d
  i=$i+1
  sleep 2
done < $filename

