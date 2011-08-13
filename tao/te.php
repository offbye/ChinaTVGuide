<?php
require 'conn.php';



$key="";
$sql="select * from tvprogram  where 1=1 ";
$k='';
$c='';
$d='';
$t='';
$p='';

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
	$sql.= " and starttime < '". $t . "' and endtime>'" .$t. "'" ;
	$k.=$t;
}
if(isset($_GET["p"])  && trim($_GET["p"])!=""){
	$p=trim($_GET["p"]);
	$sql.= " and program like '%".$p. "%'";
	$k.=$p;
}
$ip=$_SERVER["REMOTE_ADDR"];
//保存日志
//mysql_query("insert into log(channel,date,time,program,key,createtime,ip) VALUES('$c','$d','$t','$p','$k',now(),'$ip')");

if(isset($_GET["m"]) && md5($k.'taolite')== $_GET["m"]){
  $sql.= " and channel in ('cctv1','cctv2','cctv3','cctv4','cctv-4oz','cctv-4mz','cctv5','cctv6','cctv7','cctv8','cctv9','cctv10','cctv11','cctv12','cctvxwpd','cctvsepd','cctv_e','cctv_f','cctv-gq','cetvkzkt','jyyt','jyet','jyst','btv1','btv10','ahws','dfaws','dnws','gsws','gdws','gxws','gzws','hbws','hnws','hljws','hubeiws','hunanws','jlws','jsws','jxws','lnws','lyws','nmgws','nxws','qhws','sdws','sx1ws','sx3ws','szws','scws','tjws','xzhyws','xjws','ynws','zjws','zqws','nfwspd') ";
}
elseif(isset($_GET["m"]) && md5($k.'taopro')== $_GET["m"]){
  //echo "newversion--2--http://tv.taopart.net/phpmy/tao/ChinaTVGuidelite.apk--目前有新版本--目前有新版本2.0可用，点击确定升级到新版本";
  //return;
}
else{
	echo "error";
	return;
}





$result = mysql_query( $sql); 
while($row = mysql_fetch_row($result)) {//遍历行数 
//var_dump($rows );
$rows[]=$row;
} 
echo json_encode($rows);

mysql_close($con);
?>
