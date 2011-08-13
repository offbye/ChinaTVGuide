<?php
require 'conn.php';

$key="";
$sql="select * from tvrecommend  where 1=1  order by id desc limit 10";
$k='';
$c='';
$d='';
$t='';
$p='';
$tel='';
$imei='';
$ua='';


if(isset($_GET["tel"])  && trim($_GET["tel"])!=""){
	$tel=trim($_GET["tel"]);
}
if(isset($_GET["imei"])  && trim($_GET["imei"])!=""){
	$imei=trim($_GET["imei"]);
}
if(isset($_GET["ua"])  && trim($_GET["ua"])!=""){
	$ua=trim($_GET["ua"]);
}


$ip=$_SERVER["REMOTE_ADDR"];


$result = mysql_query( $sql); 
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
