<?php
set_time_limit(0);
//@header('Content-type: text/html;charset=UTF-8');

// �ɼ���ҳ��ַ 
$url = 'http://www.soushi.com/jiemu1_tv525/w2/';

$province= "tvprogram";
$cdate="20100119";
$channel="cctv6";
$channelname="CCTV��ӰƵ��";
$channelname= iconv('gbk','utf-8',$channelname);

if(isset($_GET["c"]) && isset($_GET["d"]) && isset($_GET["province"]) ){
	$province=$_GET["province"];
$c=	preg_split("/--/",$_GET["c"]);
//var_dump($c);
$channel=$c[0];
$channelname=$c[1];
$channelname = iconv("gbk","utf-8",$channelname);

$cdate= trim($_GET["d"]);
	
	$cweek=date("w",strtotime($cdate));
	if($cweek==0){ $cweek=7;}
	
$url = trim($c[2]).'w'.$cweek.'/';
echo $url. $channelname ;

}

require 'conn.php';

mysql_query("set names utf8"); 
mysql_query("
CREATE TABLE  if not exists  $province (
  `Id` int(11) NOT NULL auto_increment,
  `channel` varchar(50) default NULL,
  `date` varchar(20) default NULL,
  `starttime` varchar(10) default NULL,
  `endtime` varchar(10) default NULL,
  `program` varchar(100) default NULL,
  `daynight` varchar(10) default NULL,
  `channelname` varchar(50) default NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
");



//echo "<p> start gather�� $url </p>";


// ��ȡҳ����� 
$returnc = file_get_contents($url);
$j=0;
while(strlen($returnc)<100 && $j<3){
$returnc = file_get_contents($url);
$j++;
}

$start = strpos($returnc,'<ul class="programe_list">');
$end =strpos($returnc,'</div><!--end channel_content-->');
echo '$start='.$start;
echo '$$end='.$end;

$r=substr($returnc,$start,$end-$start);


//echo $r;
// ����ƥ������ 
$preg = '|<li><b(.*)>(.*)</b>(.*)</li>|isU';
preg_match_all($preg, $r, $tv);

//var_dump($tv);

$count=count($tv[2]);

if($count > 5){
	mysql_query("delete from $province where channel='$channel' and date='$cdate'");
}

for($i=0;$i<$count;$i++) {
	$starttime1 = trim($tv[2][$i]);
	echo "<br> \n ��ʼʱ�䣺". $starttime1;
	if($i+1<$count){
		 $endtime1 = trim($tv[2][$i+1]);
	}
	else{
	    $endtime1 = "00:00";
	}
	echo "\n ����ʱ�䣺". $endtime1;
	
	$prog = trim($tv[3][$i]);

	echo "\n ��Ŀ". $prog;
	$prog= filterHtml($prog);
	if(strlen($prog)>20){
	  // $prog=substr($prog,0,20);
	}
    $prog= iconv('gbk','utf-8',$prog);
	
	$daynight = trim($tv[1][$i]);
	if(trim($daynight)==""){
		$daynight =  "d";
	}
	else{
		$daynight =  "n";
	}
	echo "daynight=".$daynight ;
   
    mysql_query("INSERT INTO $province (channel,date,starttime,endtime,program,daynight,channelname) VALUES ('$channel','$cdate','$starttime1','$endtime1', '$prog', '$daynight','$channelname')");

}

mysql_close($con);



	
	function cleanhref($document){
		if(strpos($document,'target="_blank">')>0){
			return substr($document,strpos($document,'target="_blank">')+16,strpos($document,'</a>')-strpos($document,'target="_blank">')-16 );
		}
		else{
			return $document;
		}
		
	
	}
	
	function filterHtml($str) {
        $str=eregi_replace("<\/*[^<>]*>", '', $str);
        $str=str_replace(" ", '', $str);
        $str=str_replace("\n", '', $str);
        $str=str_replace("\t", '', $str);
        $str=str_replace("::", ':', $str);
        $str=str_replace(" ", '', $str);
   //$str=str_replace("&nbsp;", '', $str);
        return $str;
	}
	
function replaceHtmlAndJs($document)
{
 $document = trim($document);
 if (strlen($document) <= 0)
 {
    return $document;
 }
 $search = array ("'<script[^>]*?>.*?</script>'si",  // ȥ�� javascript
                  "'<[\/\!]*?[^<>]*?>'si",          // ȥ�� HTML ���
                  "'([\r\n])[\s]+'",                // ȥ���հ��ַ�forasp.cn����
                  "'&(quot|#34);'i",                // �滻 HTML ʵ��
                  "'&(amp|#38);'i",
                  "'&(lt|#60);'i",
                  "'&(gt|#62);'i",
                  "'&(nbsp|#160);'i",
      "\"",
      "\'",
                  );                    // ��Ϊ PHP ��fo��asp��.��cn
 $replace = array ("",
                   "",
                   "\\1",
                   "\"",
                   "&",
                   "<",
                   ">",
                   " ",
       " ",
       " "
                   );
 return @preg_replace ($search, $replace, $document);
}
?>
