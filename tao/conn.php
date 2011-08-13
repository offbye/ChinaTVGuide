<?php
$con = mysql_connect("localhost","tao","taopart2009");
if (!$con)
  {
  die('Could not connect: ' . mysql_error());
  }
mysql_query("SET NAMES 'utf8'; ",$con);
mysql_select_db("tao", $con);

?>