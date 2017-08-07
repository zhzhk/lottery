create table web_crawel_site(
	`ID` int(3) auto_increment,
	`web_type` varchar(3) comment "地址类型",
	`web_site` varchar(100) comment "刷水地址",
	`username` varchar(10) comment "刷水用户名",
	`password` varchar(10) comment "刷用户名密码",
	`uid` varchar(100) comment "刷水uid",
	`language` varchar(10) comment "语言类型",
	
	primary key(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment "爬虫URL表";

drop table web_crawel_site

insert into web_crawel_site(`web_type`,`web_site`,`username`,`password`,`uid`,`language`)
values('2','https://www.6686sky.net/app/member/FT_browse/body_var.php','','','','zh-cn')
values('1','https://www.6686sky.net/app/member/login.php','sim123','123456','','zh-cn')

desc web_crawel_site

select * from web_crawel_site

desc match_sports

