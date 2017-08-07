select * from web_football_matches_data
truncate table web_football_matches_data
create table web_football_matches_data(
	`MID` bigint(10) comment "赛事ID",
	`Type` char(5) not null comment "赛事类型",
	`MB_MID` bigint(10) comment "主队ID",
	`TG_MID` bigint(10) comment "客队ID",
	`MB_Team_cn` varchar(50) comment "主队名称",
	`TG_Team_cn` varchar(50) comment "客队名称",
	`MB_Team_tw` varchar(50) comment "主队名称",
	`TG_Team_tw` varchar(50) comment "客队名称",
	`MB_Team_en` varchar(50) comment "主队名称",
	`TG_Team_en` varchar(50) comment "客队名称",
	`M_Date` date not null comment "赛事日期",
	`M_Time` varchar(10) not null comment "开赛时间",
	`M_Start` datetime not null comment "开赛时间",
	`M_League_cn` varchar(50) comment "联赛名称",
	`M_League_tw` varchar(50) comment "联赛名称",
	`M_League_en` varchar(50) comment "联赛名称",
	`M_Type` varchar(5),
	-- 今日赛事区
	`MB_Win_Rate` varchar(10) comment "独赢主队赔率",
	`TG_Win_Rate` varchar(10) comment "独赢客队赔率",
	`M_Flat_Rate` varchar(10) comment "独赢和赔率",

	`ShowTypeR` char(1) comment "让球方H-主队让球，c-客队让球", -- 让球方 
	`M_LetB` varchar(10) comment "让球水位",
	`MB_LetB_Rate` varchar(10) comment "让球主队赔率",
	`TG_LetB_Rate` varchar(10) comment "让球客队赔率",

	`MB_Dime` varchar(10) comment "主队大小水位",
	`TG_Dime` varchar(10) comment "客队大小水位",
	`MB_Dime_Rate` varchar(10) comment "主队大小赔率",
	`TG_Dime_Rate` varchar(10) comment "客队大小赔率",
	
	`MB_Win_Rate_H` varchar(10) comment "半场独赢主队赔率",
	`TG_Win_Rate_H` varchar(10) comment "半场独赢客队赔率",
	`M_Flat_Rate_H` varchar(10) comment "半场独赢和赔率",
	
	`ShowTypeHR` char(1) comment "半场让球方H-主队让球，c-客队让球",
	`M_LetB_H` varchar(10) comment "半场让球水位",
	`MB_LetB_Rate_H` varchar(10) comment "半场让球主队赔率",
	`TG_LetB_Rate_H` varchar(10) comment "半场让球客队赔率",
	
	`MB_Dime_H` varchar(10) comment "半场大小主队水位",
	`TG_Dime_H` varchar(10) comment "半场大小客队水位",
	`MB_Dime_Rate_H` varchar(10) comment "半场大小主队赔率",
	`TG_Dime_Rate_H` varchar(10) comment "半场大小客队赔率",
	
	`S_Single_Rate` varchar(10) comment "单赔率",
	`S_Double_Rate` varchar(10) comment "双赔率",
	`R_Show` char(1) comment "是否展示今日赛事",
	
	-- 滚球区
	`MB_Win_Rate_RB` varchar(10) comment "滚球主队独赢赔率",
	`TG_Win_Rate_RB` varchar(10) comment "滚球客队独赢赔率",
	`M_Flat_Rate_RB` varchar(10) comment "滚球和赔率",
	
	`ShowTypeRB` char(1) comment "滚球让球方H-主队让球，c-客队让球", -- 让球方
	`M_LetB_RB` varchar(10) comment "滚球让球水位",
	`MB_LetB_Rate_RB` varchar(10) comment "滚球主队让球赔率",
	`TG_LetB_Rate_RB` varchar(10) comment "滚球客队让球赔率",
	
	`MB_Dime_RB` varchar(10) comment "滚球大小主队水位",
	`TG_Dime_RB` varchar(10) comment "滚球大小客队水位",
	`MB_Dime_Rate_RB` varchar(10) comment "滚球大小主队赔率",
	`TG_Dime_Rate_RB` varchar(10) comment "滚球大小客队赔率",

	`MB_Win_Rate_RB_H` varchar(10) comment "半场滚球独赢主队赔率",
	`TG_Win_Rate_RB_H` varchar(10) comment "半场滚球独赢客队赔率",
	`M_Flat_Rate_RB_H` varchar(10) comment "半场滚球独赢和赔率",

	`ShowTypeHRB` char(1) comment "半场滚球让球方H-主队让球，c-客队让球",
	`M_LetB_RB_H` varchar(10) comment "半场滚球让球水位",
	`MB_LetB_Rate_RB_H` varchar(10) comment "半场滚球让球主队赔率",
	`TG_LetB_Rate_RB_H` varchar(10) comment "半场滚球让球客队赔率",

	`MB_Dime_RB_H` varchar(10) comment "半场滚球大小主队水位",
	`TG_Dime_RB_H` varchar(10) comment "半场滚球大小客队水位",
	`MB_Dime_Rate_RB_H` varchar(10) comment "半场滚球大小主队赔率",
	`TG_Dime_Rate_RB_H` varchar(10) comment "半场滚球大小客队赔率",

	`S_Single_Rate_RB` varchar(10) comment "滚球单赔率",
	`S_Double_Rate_RB` varchar(10) comment "滚球双赔率",

	`RB_Show` char(1) comment "滚球是否展示",

	`MB_Inball` int(3) comment "全场完结主队得分",
	`TG_Inball` int(3) comment "全场完结客队得分",
	
	`MB_Inball_HR` int(3) comment "半场完结主队得分",
	`TG_Inball_HR` int(3) comment "半场完结客队得分",

	`MB_Ball` int(3) comment "主队即时比分",
	`TG_Ball` int(3) comment "客队即时比分",

	`MB_Card` int(3) comment "主队卡",
	`TG_Card` int(3) comment "客队卡",

	`MB_Red` int(3) comment "主队红牌",
	`TG_Red` int(3) comment "客队红牌",

	`Hot` char(1),
	`isOpen` char(1) comment "赛事是否开始",
	`isFinish` char(1) comment "赛事是否完结",
	`isCancel` char(1) comment "赛事是否取消",
	`isChecked` char(1) comment "赛事是否核对",
	`isCheckout` char(1) comment "赛事是否审核",

	`now_play` char(1) comment "赛事当前时间",
	`source_type` varchar(5) comment "数据来源",
	primary key (MID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment "足球赛事数据表";