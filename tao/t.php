<?php 
	
	echo strtotime('20100118').'<p>';
	echo date("w",strtotime('20100117'));
	
	echo cleanhref('<a href="/jiemu/info158/" target="_blank">第一时间:资讯唤醒每一天</a>');
	
	function cleanhref($document){
		if(strpos($document,'target="_blank">')>0){
			return substr($document,strpos($document,'target="_blank">')+16,strpos($document,'</a>')-strpos($document,'target="_blank">')-16 );
		}
		else{
			return $document;
		}
		
	
	}
?>