create table web_proxy_ip(
	`ip` varchar(15) comment "代理IP",
    `port` varchar(5) comment "代理端口",
    `location` varchar(50) comment "代理IP位置",
    `type` varchar(5) comment "IP类型",
    `createtime` datetime comment "创建时间",
    primary key(`ip`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT="代理IP表"