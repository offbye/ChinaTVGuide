<?php
require 'conn.php';



$key="";
$sql="select * from tvprogram  where 1=1 ";
$k='';
$c='';
$d='';
$t='';
$p='';
$tel='';
$imei='';
$ua='';


if(isset($_GET["c"])  && trim($_GET["c"])!=""){
	$c=trim($_GET["c"]);
	$sql.= " and channel='".$c. "'";
	$k.=$c;
}
if(isset($_GET["d"])  && trim($_GET["d"])!=""){
	$d=trim($_GET["d"]);
	$sql.= " and date='".$d. "'";
	$k.=$d;
}

if(isset($_GET["t"]) && trim($_GET["t"])!=""){
	//t=20:32
	$t=trim($_GET["t"]);
	if(substr($t,0,2)=="23"){
		$sql.= " and starttime < '". $t . "' and starttime like '2%'  and ( endtime>'$t' or endtime like '00:%')"  ;
	}
	else{
		$sql.= " and starttime < '". $t . "' and endtime>'" .$t. "'" ;
	}
	
	$k.=$t;
}
if(isset($_GET["p"])  && trim($_GET["p"])!=""){
	$p=trim($_GET["p"]);
	$sql.= " and program like '%".$p. "%'";
	$k.=$p;
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



if(isset($_GET["m"]) && md5($k.'hiapk20100414')== $_GET["m"]){
	mysql_query("insert into log (c,d,t,p,k,createtime,ip,tel,imei,ua) VALUES('$c','$d','$t','$p','hiapk20100414',now(),'$ip','$tel','$imei','$ua')");
}
elseif(isset($_GET["m"]) && md5($k.'mm20100414')== $_GET["m"]){
	mysql_query("insert into log (c,d,t,p,k,createtime,ip,tel,imei,ua) VALUES('$c','$d','$t','$p','mm20100414',now(),'$ip','$tel','$imei','$ua')");
}
elseif(isset($_GET["m"]) && md5($k.'le20100414')== $_GET["m"]){
	mysql_query("insert into log (c,d,t,p,k,createtime,ip,tel,imei,ua) VALUES('$c','$d','$t','$p','le20100414',now(),'$ip','$tel','$imei','$ua')");
}
else{
	mysql_query("insert into log (c,d,t,p,k,createtime,ip,tel,imei,ua) VALUES('$c','$d','$t','$p','errorkey',now(),'$ip','$tel','$imei','$ua')");
	echo "error";
	return;
}





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
