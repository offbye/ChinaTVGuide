<?php
	require 'conn.php';
$ip=$_SERVER["REMOTE_ADDR"];
	mysql_query("insert into logbook (c,k,createtime,ip,tel,imei) VALUES('c','taolite',now(),'$ip','tel','imei')");

$rows =array(
	        array("4149893","http://img2.douban.com/mpic/s4108222.jpg","每天懂一点好玩心理学"),
			array("4012145","http://img2.douban.com/mpic/s4059780.jpg","中国的经济制度"),
	 		array("4146477","http://t.douban.com/mpic/s4081375.jpg","北大批判"),
	        array("4121562","http://t.douban.com/mpic/s4043196.jpg","吃的真相"),
	        array("4011670","http://t.douban.com/spic/s4016513.jpg","人间失格"),
			array("4074636","http://t.douban.com/lpic/s4066862.jpg","放学后"),
			array("4072044","http://t.douban.com/spic/s4007699.jpg","微笑的咖啡杯"),
			array("4124727","http://t.douban.com/spic/s4075572.jpg","孤独六讲"),
		);
echo json_encode($rows);

?>
