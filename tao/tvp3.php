<?php
set_time_limit(0);
@header('Content-type: text/html;charset=UTF-8');
$channelname ='';
$channel='';
$province='';
// 采集首页地址 
$url = 'http://www.ezhun.com/channel/cctv3_20091215.htm';


if(isset($_GET["c"]) && isset($_GET["d"]) && isset($_GET["province"]) ){
	$province=$_GET["province"];
$c=	preg_split("/--/",$_GET["c"]);
//var_dump($c);
$channel=$c[0];
$channelname=$c[1];
$channelname = iconv("gbk","utf-8",$channelname);
$url = 'http://www.ezhun.com/channel/'. $channel .'_'. $_GET["d"] .'.htm';
echo $url. $channelname ;

$cdate= trim($_GET["d"]);
	
}


//echo "<br>tablename=".$channel;
//echo '<br>cdate='.$cdate;

require 'conn.php';

mysql_query("set names utf8"); 
mysql_query("
CREATE TABLE  if not exists  $province (
  `Id` int(11) NOT NULL auto_increment,
  `channel` varchar(10) NOT NULL,
  `date` varchar(10) NOT NULL,
  `starttime` varchar(8) NOT NULL,
  `endtime` varchar(10) NOT NULL,
  `program` varchar(80) NOT NULL,
  `daynight` varchar(10) NOT NULL,
  `channelname` varchar(40) NOT NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
");



//echo "<p> start gather： $url </p>";


// 获取页面代码
$ctx = stream_context_create(array(
    'http' => array('timeout' => 20)
    )
);

$returnc = file_get_contents($url,0,$ctx);
$j=0;
while(strlen($returnc)<100 && $j<3){
$returnc = file_get_contents($url);
$j++;
}


$start = strpos($returnc,'<td id="channel_am">');
$end =strpos($returnc,'<td id="channel_pm">');
//echo '$start='.$start;
//echo '$$end='.$end;

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
if($count > 5){
	mysql_query("delete from $province where channel='$channel' and date='$cdate'");
}

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
	$prog=str_replace("&amp;","&",$prog);
	
    //mysql_query("INSERT INTO $channel (channel,date,starttime,endtime,program,daynight) VALUES ('$channel','$cdate','$starttime1','$endtime1', '$prog', 'd')");
    mysql_query("INSERT INTO $province (channel,date,starttime,endtime,program,daynight,channelname) VALUES ('$channel','$cdate','$starttime1','$endtime1', '$prog', 'd','$channelname')");

}

//gather night

$start = strpos($returnc,'id="channel_pm1">');
$end =strpos($returnc,'<td align="center" colspan="2">
				<table cellspacing=2 id=datelist><tr>');
//echo '$start='.$start;
//echo '$$end='.$end;

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
	$prog=str_replace("&amp;","&",$prog);
    //mysql_query("INSERT INTO $channel (channel,date,starttime,endtime,program,daynight) VALUES ('$channel','$cdate','$starttime1','$endtime1', '$prog', 'n')");
    mysql_query("INSERT INTO $province (channel,date,starttime,endtime,program,daynight,channelname) VALUES ('$channel','$cdate','$starttime1','$endtime1', '$prog', 'n','$channelname')");
}


mysql_close($con);
?>
