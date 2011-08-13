<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="zh-CN" dir="ltr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
<meta name="keywords" content="webview Android" />
</head>
<body>
<?php
require 'conn.php';
$key="";
$sql="select distinct e from suggest  order by id desc limit 10";


$result = mysql_query( $sql); 
while($row = mysql_fetch_row($result)) {//遍历行数 
$temp.= $row[0].'<br />';

} 
mysql_close($con);
echo '感谢以下网友对本软件提出建议：<br />'.$temp;
?>
</body>
</html>