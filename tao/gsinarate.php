<?php
set_time_limit(0);
@header('Content-type: text/html;charset=gb2312');
$channelname ='';
$channel='';
$province='';
// 采集地址 
$url = 'http://ent.sina.com.cn/v/2010-04-08/17592922909.shtml';


require 'conn.php';
$tvrate="tvrate";
mysql_query("set names utf8"); 
mysql_query("
CREATE TABLE  if not exists  $tvrate (
  `id` int(11) NOT NULL auto_increment,
  `rank` varchar(5)  NOT NULL,
  `program` varchar(60) NOT NULL,
  `channel` varchar(60) NOT NULL,
  `tvtype` varchar(20) NOT NULL,
  `averageRate` varchar(5) NOT NULL,
  `changes` varchar(5) NOT NULL,
  `highRate` varchar(5) NOT NULL,
   `tvdate` varchar(30) NOT NULL,
   `tvcity` varchar(20) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
");



echo "<p> start gather： $url </p>";


// 获取页面代码 
$returnc = zhoz_get_contents($url);
$j=0;
while(strlen($returnc)<200 && $j<3){
$returnc = zhoz_get_contents($url);
$j++;
}

$title=substr($returnc,strpos($returnc,'<title>'),strpos($returnc,'</title>'));
//echo "title=".$title ."<br>";

$start=strpos($title,'(');
$end=strpos($title,')-');
$tvdate=substr($title,$start+1,$end-$start-1);

echo "tvdate=".$tvdate ."<br>";


$start=strpos($title,')-');
$end=strpos($title,'_');
$tvcity=substr($title,$start+2,$end-$start-2);
echo "tvcity=".$tvcity ."<br>";

$tvcity=iconv('gbk','utf-8',$tvcity);

$start = strpos($returnc,'<div class="blkContainerSblk">');
$end =strpos($returnc,'</tbody>');
echo 'start='.$start;
echo '<br>end='.$end;

$r=substr($returnc,$start,$end-$start);

$r=DeleteHtml($r);
echo $r;

$preg = '|<tr height="17"><td height="17">(\d+)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td><td>(.*)</td></tr>|isU';

//$preg = '|<tr height="19"><td width="35" height="19">(\d+)</td><td width="162" height="19">(.*)</td><td width="149" height="19">(.*)</td><td width="71" height="19">(.*)</td><td width="45" height="19">(.*)</td><td width="61" height="19">(.*)</td><td width="62" height="19">(.*)</td></tr>|isU';
preg_match_all($preg, $r, $rate);

//echo $preg;

//var_dump($rate);


$count=count($rate[1]);
echo '<br>$count='.$count;
if($count > 9){
    //mysql_query("delete from $tvrate where channel='$channel' and date='$cdate'");
}

for($i=0;$i<$count;$i++) {

$rank= iconv('gbk','utf-8',$rate[1][$i]);
$p = iconv('gbk','utf-8',clearhref($rate[2][$i]));
$channel= iconv('gbk','utf-8',$rate[3][$i]);
$tvtype= iconv('gbk','utf-8',$rate[4][$i]);
$averageRate= iconv('gbk','utf-8',$rate[5][$i]);
$changes= iconv('gbk','utf-8',$rate[6][$i]);
$highRate= iconv('gbk','utf-8',$rate[7][$i]);

$insertsql="INSERT INTO $tvrate (rank,program,channel,tvtype,averageRate,changes,highRate,tvdate,tvcity) VALUES ('$rank','$p','$channel','$tvtype', '$averageRate', '$changes','$highRate','$tvdate','$tvcity')";
		
mysql_query($insertsql);

}


mysql_close($con);


 function zhoz_get_contents($url, $second = 20) {      
        $ch = curl_init();      
        curl_setopt($ch,CURLOPT_URL,$url);      
        curl_setopt($ch,CURLOPT_HEADER,0);      
        curl_setopt($ch,CURLOPT_TIMEOUT,$second);      
        curl_setopt($ch,CURLOPT_RETURNTRANSFER, true);      
     
        $content = curl_exec($ch);      
        curl_close($ch);      
        return $content;      
    }
 function DeleteHtml($str) 
{ 
$str = trim($str); 
//$str = strip_tags($str,""); 
$str = str_replace("\t","",$str); 
$str = str_replace("\r\n","",$str); 
$str = str_replace("\r","",$str); 
$str = str_replace("\n","",$str); 
$str = str_replace("&nbsp;","",$str); 
return trim($str); 
}


function clearhref($str) 
{ 
$str = trim($str); 
$str = strip_tags($str,""); 
$str = str_replace("\t","",$str); 
$str = str_replace("\r\n","",$str); 
$str = str_replace("\r","",$str); 
$str = str_replace("\n","",$str); 
$str = str_replace(" ","",$str); 
return trim($str); 
}
?>
