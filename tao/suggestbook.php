<?php
require 'conn.php';
$ip=$_SERVER["REMOTE_ADDR"];

if(isset($_GET["c"])  && trim($_GET["c"])!="" && isset($_GET["e"])  && trim($_GET["e"])!="" ){
  	$c= addslashes(trim($_GET["c"]));
  	$e= addslashes(trim($_GET["e"]));
  	//保存日志
	if(mysql_query("insert into suggest (c,e,createtime,ip) VALUES('$c','$e',now(),'$ip')")){
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
