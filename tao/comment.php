<?php
require 'conn.php';
$ip=$_SERVER["REMOTE_ADDR"];

if(isset($_GET["rate"])  && trim($_GET["content"])!="" && isset($_GET["channel"])  && trim($_GET["program"])!="" ){
  	$user= addslashes(trim($_GET["user"]));
  	$rate= addslashes(trim($_GET["rate"]));
  	$content= addslashes(trim($_GET["content"]));
  	
  	
  	$channel= addslashes(trim($_GET["channel"]));
  	$program= addslashes(trim($_GET["program"]));
  	$cdate= addslashes(trim($_GET["cdate"]));
  	$channelname= addslashes(trim($_GET["channelname"]));
  	$starttime= addslashes(trim($_GET["starttime"]));
  	
  	//保存日志
	if(mysql_query("insert into Comment (user,rate,content,channel,program,cdate,channelname,starttime,createTime) VALUES('$user','$rate','$content','$channel','$program','$cdate','$channelname','$starttime',now())")){
		echo "ok";
	}
	else{
	    echo "error";
	}
}
else{
  echo "error"; 
}

mysql_close($con);

?>
