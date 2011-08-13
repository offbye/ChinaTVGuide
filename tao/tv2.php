<?php
set_time_limit(0);
@header('Content-type: text/html;charset=UTF-8');

// 采集首页地址 
$url = 'http://www.ezhun.com/channel/cctv3_20091215.htm';
$tablename= substr($url,29,strpos($url,'_')-29);
$cdate= substr($url,strpos($url,'_')+1, strpos($url,'.h')-strpos($url,'_')-1);


if(isset($_GET["c"]) && isset($_GET["d"])){
$url = 'http://www.ezhun.com/channel/'. $_GET["c"] .'_'. $_GET["d"] .'.htm';
$tablename= trim($_GET["c"]);
$cdate= trim($_GET["d"]);
}


//echo "<br>tablename=".$tablename;
//echo '<br>cdate='.$cdate;

require 'conn.php';

mysql_query("set names utf8"); 
mysql_query("
CREATE TABLE  if not exists  $tablename (
  `Id` int(11) NOT NULL auto_increment,
  `channel` varchar(50) default NULL,
  `date` varchar(20) default NULL,
  `starttime` varchar(10) default NULL,
  `endtime` varchar(10) default NULL,
  `program` varchar(100) default NULL,
  `daynight` varchar(10) default NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
");
mysql_query("delete from tvprogram where channel='$tablename' and date='$cdate'");


//echo "<p> start gather： $url </p>";


// 获取页面代码 
$returnc = file_get_contents($url);


$start = strpos($returnc,'<td id="channel_am">');
$end =strpos($returnc,'<td id="channel_pm">');
echo '$start='.$start;
echo '$$end='.$end;

$r=substr($returnc,$start,$end-$start);


//echo $r;
// 设置匹配正则 
$preg = '|<td class=time>(.*)</td>|isU';
preg_match_all($preg, $r, $starttime);

//var_dump($starttime);

$preg = '|target=_blank>(.*)</a></td></tr>|isU';
preg_match_all($preg, $r, $title);
//var_dump($title);

$count=count($starttime[1]);
for($i=0;$i<$count;$i++) {

	$starttime1 = trim($starttime[1][$i]);
	echo "<br> \n 时间：". $starttime1;
	if($i+1<$count){
		 $endtime1 = trim($starttime[1][$i+1]);
	}
	else{
	    $endtime1 = "18:00";
	}
   
		
	$prog = trim($title[1][$i]);
	if(strpos($prog,'</a>')){
		$prog=substr($prog,0,strpos($prog,'</a>'));
		echo "\n 节目：". $prog;
	}
	else{
		echo "\n 节目：". $prog;
	}
	$prog=str_replace("&gt;介绍&quot;&gt;","",$prog);
	
	mysql_query("INSERT INTO $tablename (channel,date,starttime,endtime,program,daynight) VALUES ('$tablename','$cdate','$starttime1','$endtime1', '$prog', 'd')");
    mysql_query("INSERT INTO tvprogram (channel,date,starttime,endtime,program,daynight) VALUES ('$tablename','$cdate','$starttime1','$endtime1', '$prog', 'd')");

}

//gather night

$start = strpos($returnc,'id="channel_pm1">');
$end =strpos($returnc,'<td align="center" colspan="2">
				<table cellspacing=2 id=datelist><tr>');
echo '$start='.$start;
echo '$$end='.$end;

$r=substr($returnc,$start,$end-$start);


//echo $r;
// 设置匹配正则 
$preg = '|<td class=time>(.*)</td>|isU';
preg_match_all($preg, $r, $starttime);

//var_dump($starttime);

$preg = '|target=_blank>(.*)</a></td></tr>|isU';
preg_match_all($preg, $r, $title);
//var_dump($title);

$count=count($starttime[1]);
for($i=0;$i<$count;$i++) {

	$starttime1 = trim($starttime[1][$i]);
	echo "<br> \n 时间：". $starttime1;
	
	if($i+1<$count){
		 $endtime1 = trim($starttime[1][$i+1]);
	}
	else{
	    $endtime1 = "06:00";
	}
	
	$prog = trim($title[1][$i]);
	if(strpos($prog,'</a>')){
		$prog=substr($prog,0,strpos($prog,'</a>'));
		echo "\n 节目：". $prog;
	}
	else{
		echo "\n 节目：". $prog;
	}
	$prog=str_replace("&gt;介绍&quot;&gt;","",$prog);
    mysql_query("INSERT INTO $tablename (channel,date,starttime,endtime,program,daynight) VALUES ('$tablename','$cdate','$starttime1','$endtime1', '$prog', 'n')");
    mysql_query("INSERT INTO tvprogram (channel,date,starttime,endtime,program,daynight) VALUES ('$tablename','$cdate','$starttime1','$endtime1', '$prog', 'n')");
}


mysql_close($con);
?>
