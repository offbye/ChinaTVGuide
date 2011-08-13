#!/bin/bash

filename="sjws.txt"
#d=`date  +%Y%m%d`
d=20091219
url=http://localhost/phpmy/tao/tv1.php
i=0
while read LINE
do
  echo $LINE
  wget -q  $url?c=$LINE"&d="$d
  i=$i+1
  sleep 2
done < $filename

