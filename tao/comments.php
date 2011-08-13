<?php
require 'conn.php';
$ip=$_SERVER["REMOTE_ADDR"];

$sql="SELECT id,user,rate,content,channel,program,cdate,channelname,starttime,createTime FROM Comment order by id desc";


$result = mysql_query($sql); 
while($row = mysql_fetch_row($result)) {//遍历行数 
$temp=ereg_replace('&gt;&gt;','>>',$row[5]);
$temp=ereg_replace('&#176;','.',$temp);
$temp=ereg_replace('&#183;','.',$temp);
$row[5]=$temp;
$rows[]=$row;
} 
echo json_encode($rows);

mysql_close($con);
?>
