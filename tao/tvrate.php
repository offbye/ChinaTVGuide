<?php
require 'conn.php';



$key="";
$sql="select * from tvrate  where 1=1 ";
$k='';
$c='';
$d='';
$t='';
$p='';
$tel='';
$imei='';
$ua='';


if(isset($_GET["city"])  && trim($_GET["city"])!=""){
	$city=trim($_GET["city"]);
	$sql.= " and tvcity='".$city. "'";
	$k.=$c;
}
else{
   	$sql.= " and tvcity='北京'";
}


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


$sql=$sql.' order by id desc limit 10 ';

$result = mysql_query( $sql); 
while($row = mysql_fetch_row($result)) {//遍历行数 
$rows[]=$row;
} 
echo json_encode(array_reverse($rows));

mysql_close($con);
?>
