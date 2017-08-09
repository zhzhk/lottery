package org.crawler.service.impl;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.crawler.entity.UrlData;
import org.crawler.entity.WebFootballMatchesData;
import org.crawler.service.GetDatasService;
import org.crawler.util.CrawlerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class GetDatasServiceImpl implements GetDatasService {
	@Autowired 
	private JdbcTemplate jdbcTemplate;

	/**
	 * 模拟用户登陆，并更新uid
	 */
	@Override
	public String updateUid() {
		UrlData urlData = getUrlData("1");
		String loginUsername = urlData.getUsername();
		String password = urlData.getPassword();
		String loginUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();
		
		Response reponse;
		String uidString = null;
		try {
			reponse = Request.Post(loginUrl)
			.bodyForm(Form.form().add("uid",uid).add("langx",lanaguage).add("mac","")
			.add("ver","").add("JE","").add("username",loginUsername).add("password",password)
			.add("checkbox","on").build()).execute();
			uidString = reponse.returnContent().asString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(uidString);
//		<script>window.location.href='https://www.6686sky.net/app/member/FT_index.php?mtype=3&uid=5c402015669aa35fa7efra8&langx=zh-cn';</script>
		String pattern = ".*uid=(\\w+)&.*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(uidString);
		String msg = null;
		if(m.find()){
//			System.out.println(m.group(1));
			String nuid = m.group(1);
			String uuidSql = "update web_crawel_site set uid = ? where web_type = ?"; 
			jdbcTemplate.update(uuidSql, nuid, "2");
			msg = "更新成功";
		}
		return msg;
	}

	@Override
	/**
	 * 获取足球今日赛事
	 */
	public int getFtTodayMatchesDatas() {
		UrlData urlData = getUrlData("2");
		String crawelUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();		
		Map<String,List<String>> map = new HashMap<String,List<String>>();		
		map = CrawlerUtil.getResult(crawelUrl, uid, lanaguage, "1",0);
		
//		String dataUrl = crawelUrl+"?uid="+uid+"&rtype=r&langx="+lanaguage+"&mtype=3&page_no=0&league_id=&hot_game=undefined&sort_type=undefined&zbreload=1&l=ALL";
//		
//		Response rep;
//		String dataString = null;
//		try {
//			rep = Request.Get(dataUrl).execute();
//			dataString = rep.returnContent().asString();
//		} catch (ClientProtocolException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		List<String> headArray = map.get("headTitle");
//		parent.GameHead=new Array('gid','datetime','league','gnum_h','gnum_c','team_h','team_c','strong',
//		'ratio','ior_RH','ior_RC','ratio_o','ratio_u','ior_OUH','ior_OUC','ior_MH','ior_MC','ior_MN',
//		'str_odd','str_even','ior_EOO','ior_EOE','hgid','hstrong','hratio','ior_HRH','ior_HRC','hratio_o',
//		'hratio_u','ior_HOUH','ior_HOUC','ior_HMH','ior_HMC','ior_HMN','more','eventid','hot','play');
//		parent.GameFT[0]=new Array('2842706','08-07<br>11:00a<br><font color=red>Running Ball</font>',
//		'爱尔兰联赛杯','10202','10201','戈尔韦联','登克尔克','C','1','0.72','0.60','O2.5 / 3','U2.5 / 3','0.69',
//		'0.60','5.60','1.23','4.10','单','双','1.65','1.64','2842706','C','0.5','0.51','0.79','O1 / 1.5',
//		'U1 / 1.5','0.47','0.82','4.80','1.78','2.05','11','0','','N');
		List<WebFootballMatchesData> gameDatas = new ArrayList<WebFootballMatchesData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		List<String> gameDataStrs = map.get("gameData");
		for(String gameDataStr : gameDataStrs){
			String[] dataArray = gameDataStr.replaceAll("'", "").split(",");
			String[] timeArray = dataArray[getElementIndexByValue("datetime",headArray)].split("<br>");
			long time = 0;
			long detailTime = 0;
			try {
				time = dateFormat.parse(year+"-"+timeArray[0]).getTime();
				detailTime = timeFormat.parse(year+"-"+timeArray[0]+" "+timeArray[1].substring(0,timeArray[1].length()-1)+":00").getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date date = new Date(time);
			Timestamp dateTime = new Timestamp(detailTime);
			WebFootballMatchesData gameData = new WebFootballMatchesData();
			gameData.setMid(Integer.parseInt(dataArray[getElementIndexByValue("gid",headArray)]));
			gameData.setType("FT");
			gameData.setMb_mid(Integer.parseInt(dataArray[getElementIndexByValue("gnum_h",headArray)]));
			gameData.setTg_mid(Integer.parseInt(dataArray[getElementIndexByValue("gnum_c",headArray)]));
			gameData.setMb_team_cn(dataArray[getElementIndexByValue("team_h",headArray)]);
			gameData.setTg_team_cn(dataArray[getElementIndexByValue("team_c",headArray)]);
			
			gameData.setM_league_cn(dataArray[getElementIndexByValue("league",headArray)]);
			gameData.setM_date(date);
			gameData.setM_time(timeArray[1]);
			gameData.setM_start(dateTime);
			gameData.setM_type("1");
			gameData.setShowtyper(dataArray[getElementIndexByValue("strong",headArray)]);
			gameData.setM_letb(dataArray[getElementIndexByValue("ratio",headArray)]);
			gameData.setMb_letb_rate(dataArray[getElementIndexByValue("ior_RH",headArray)]);
			gameData.setTg_letb_rate(dataArray[getElementIndexByValue("ior_RC",headArray)]);
			gameData.setMb_dime(dataArray[getElementIndexByValue("ratio_o",headArray)]);
			gameData.setTg_dime(dataArray[getElementIndexByValue("ratio_u",headArray)]);
			gameData.setMb_dime_rate(dataArray[getElementIndexByValue("ior_OUH",headArray)]);
			gameData.setTg_dime_rate(dataArray[getElementIndexByValue("ior_OUC",headArray)]);
			gameData.setMb_win_rate(dataArray[getElementIndexByValue("ior_MH",headArray)]);
			gameData.setTg_win_rate(dataArray[getElementIndexByValue("ior_MC",headArray)]);
			gameData.setM_flat_rate(dataArray[getElementIndexByValue("ior_MN",headArray)]);
			gameData.setS_single_rate(dataArray[getElementIndexByValue("ior_EOO",headArray)]);
			gameData.setS_double_rate(dataArray[getElementIndexByValue("ior_EOE",headArray)]);
			gameData.setShowtypehr(dataArray[getElementIndexByValue("hstrong",headArray)]);
			gameData.setM_letb_h(dataArray[getElementIndexByValue("hratio",headArray)]);
			gameData.setMb_letb_rate_h(dataArray[getElementIndexByValue("ior_HRH",headArray)]);
			gameData.setTg_letb_rate_h(dataArray[getElementIndexByValue("ior_HRC",headArray)]);
			gameData.setMb_dime_h(dataArray[getElementIndexByValue("hratio_o",headArray)]);
			gameData.setTg_dime_h(dataArray[getElementIndexByValue("hratio_u",headArray)]);
			gameData.setMb_dime_rate_h(dataArray[getElementIndexByValue("ior_HOUH",headArray)]);
			gameData.setTg_dime_rate_h(dataArray[getElementIndexByValue("ior_HOUC",headArray)]);
			gameData.setMb_win_rate_h(dataArray[getElementIndexByValue("ior_HMH",headArray)]);
			gameData.setTg_win_rate_h(dataArray[getElementIndexByValue("ior_HMC",headArray)]);
			gameData.setM_flat_rate_h(dataArray[getElementIndexByValue("ior_HMN",headArray)]);
			gameData.setR_show("1");
			gameData.setSource_type("HG");
			gameDatas.add(gameData);
			
		}
		int count = gameDataStrs.size();
		System.out.println("######################"+gameDatas.size()+"#################");
		saveGameDatas(gameDatas);
		return count;
	}

	@Override
	public int getFtRbMatchesDatas() {
		UrlData urlData = getUrlData("2");
		String crawelUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();
		Map<String,List<String>> map = new HashMap<String,List<String>>();		
		map = CrawlerUtil.getResult(crawelUrl, uid, lanaguage, "2",0);
		
//		parent.str_more='直播投注';parent.GameHead=new Array('gid','timer','league','gnum_h','gnum_c','team_h','team_c','strong','ratio','ior_RH',
//		'ior_RC','ratio_o','ratio_u','ior_OUH','ior_OUC','no1','no2','no3','score_h','score_c','hgid','hstrong','hratio','ior_HRH','ior_HRC','hratio_o',
//		'hratio_u','ior_HOUH','ior_HOUC','redcard_h','redcard_c','lastestscore_h','lastestscore_c','ior_MH','ior_MC','ior_MN','ior_HMH','ior_HMC','ior_HMN',
//		'str_odd','str_even','ior_EOO','ior_EOE','eventid','hot','center_tv','play','datetime','retimeset','more','sort_team_h','i_timer',
//		'sort_dy','sort_tmax');
//		parent.GameFT.length=0;parent.GameFT[0]=new Array('2828720','72','美国职业大联盟','70558','70557','肯萨斯城体育会','亚特兰大联','H','0','0.40','0.90',
//		'O1.5','U1.5','0.59','0.70','','','','1','0','2828721','','','','','','','','','0','0','H','','1.18','21.00','5.40','','','','单','双',
//		'1.54','2.35','2129678','','img','Y','08-06<br>08:16p','2H^27','3','肯萨斯城体育会','87','1.18','87');
		List<String> headArray = map.get("headTitle");
		List<WebFootballMatchesData> gameDatas = new ArrayList<WebFootballMatchesData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		List<String> gameDataStrs = map.get("gameData");
		for(String gameDataStr : gameDataStrs){
			String[] dataArray = gameDataStr.replaceAll("'", "").split(",");
			
			String[] timeArray = dataArray[getElementIndexByValue("datetime",headArray)].split("<br>");
			long time = 0;
			long detailTime = 0;
			try {
				time = dateFormat.parse(year+"-"+timeArray[0]).getTime();
				detailTime = timeFormat.parse(year+"-"+timeArray[0]+" "+timeArray[1].substring(0,timeArray[1].length()-1)+":00").getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date date = new java.sql.Date(time);
			Timestamp dateTime = new Timestamp(detailTime);
			WebFootballMatchesData gameData = new WebFootballMatchesData();
			gameData.setMid(Integer.parseInt(dataArray[getElementIndexByValue("gid",headArray)]));
			gameData.setType("FT");
			gameData.setMb_mid(Integer.parseInt(dataArray[getElementIndexByValue("gnum_h",headArray)]));
			gameData.setTg_mid(Integer.parseInt(dataArray[getElementIndexByValue("gnum_c",headArray)]));
			gameData.setMb_team_cn(dataArray[getElementIndexByValue("team_h",headArray)]);
			gameData.setTg_team_cn(dataArray[getElementIndexByValue("team_c",headArray)]);
			
			gameData.setM_league_cn(dataArray[getElementIndexByValue("league",headArray)]);
			gameData.setM_date(date);
			gameData.setM_time(timeArray[1]);
			gameData.setM_start(dateTime);
			gameData.setM_type("1");	
			gameData.setShowtyperb(dataArray[getElementIndexByValue("strong",headArray)]);
			gameData.setM_letb_rb(dataArray[getElementIndexByValue("ratio",headArray)]);
			gameData.setMb_letb_rate_rb(dataArray[getElementIndexByValue("ior_RH",headArray)]);
			gameData.setTg_letb_rate_rb(dataArray[getElementIndexByValue("ior_RC",headArray)]);
			gameData.setMb_dime_rb(dataArray[getElementIndexByValue("ratio_o",headArray)]);
			gameData.setTg_dime_rb(dataArray[getElementIndexByValue("ratio_u",headArray)]);
			gameData.setMb_dime_rate_rb(dataArray[getElementIndexByValue("ior_OUH",headArray)]);
			gameData.setTg_dime_rate_rb(dataArray[getElementIndexByValue("ior_OUC",headArray)]);
			gameData.setMb_win_rate_rb(dataArray[getElementIndexByValue("ior_MH",headArray)]);
			gameData.setTg_win_rate_rb(dataArray[getElementIndexByValue("ior_MC",headArray)]);
			gameData.setM_flat_rate_rb(dataArray[getElementIndexByValue("ior_MN",headArray)]);
			gameData.setS_single_rate_rb(dataArray[getElementIndexByValue("ior_EOO",headArray)]);
			gameData.setS_double_rate_rb(dataArray[getElementIndexByValue("ior_EOE",headArray)]);
			gameData.setShowtypehrb(dataArray[getElementIndexByValue("hstrong",headArray)]);
			gameData.setM_letb_rb_h(dataArray[getElementIndexByValue("hratio",headArray)]);
			gameData.setMb_letb_rate_rb_h(dataArray[getElementIndexByValue("ior_HRH",headArray)]);
			gameData.setTg_letb_rate_rb_h(dataArray[getElementIndexByValue("ior_HRC",headArray)]);
			gameData.setMb_dime_rb_h(dataArray[getElementIndexByValue("hratio_o",headArray)]);
			gameData.setTg_dime_rb_h(dataArray[getElementIndexByValue("hratio_u",headArray)]);
			gameData.setMb_dime_rate_rb_h(dataArray[getElementIndexByValue("ior_HOUH",headArray)]);
			gameData.setTg_dime_rate_rb_h(dataArray[getElementIndexByValue("ior_HOUC",headArray)]);
			gameData.setMb_win_rate_rb_h(dataArray[getElementIndexByValue("ior_HMH",headArray)]);
			gameData.setTg_win_rate_rb_h(dataArray[getElementIndexByValue("ior_HMC",headArray)]);
			gameData.setM_flat_rate_rb_h(dataArray[getElementIndexByValue("ior_HMN",headArray)]);
			gameData.setMb_ball(dataArray[getElementIndexByValue("score_h",headArray)]);
			gameData.setTg_ball(dataArray[getElementIndexByValue("score_c",headArray)]);
			gameData.setRb_show("1");
			gameData.setSource_type("HG");
			gameDatas.add(gameData);
			
		}
		int count = gameDataStrs.size();
		saveGameDatas(gameDatas);
		System.out.println(count);
		return 0;
	}

	@Override
	public int getBkTodayMatchesDatas() {
		return 0;
	}

	@Override
	public int getBkRbMatchesDatas() {
		return 0;
	}
	
	private UrlData getUrlData(String wtype) {
		UrlData urlData = jdbcTemplate.queryForObject(
				"select * from web_crawel_site where ID = ?",
				new Object[]{wtype},
				new RowMapper<UrlData>() {
				public UrlData mapRow(ResultSet rs, int rowNum) throws SQLException {
					UrlData urlData = new UrlData();
					urlData.setWeb_type(rs.getString("web_type"));
					urlData.setWeb_site(rs.getString("web_site"));
					urlData.setUsername(rs.getString("username"));
					urlData.setPassword(rs.getString("password"));
					urlData.setUid(rs.getString("uid"));
					urlData.setLanguage(rs.getString("language"));
					return urlData;
				}
			}
		);
		return urlData;
	}
	
	private int getElementIndexByValue(String val,List<String> vals){
		for(int i=0;i<vals.size();i++){
			if(val.equals(vals.get(i))){
				 return i;
			}
		}
		return -1;
	}
	
	private int saveGameDatas(List<WebFootballMatchesData> gameDatas) {
		List<WebFootballMatchesData> batchUpdateDatas = new ArrayList<WebFootballMatchesData>();
		List<WebFootballMatchesData> batchInsertDatas = new ArrayList<WebFootballMatchesData>();
		for(WebFootballMatchesData gameData : gameDatas){
			String isExistSql = "select count(*) from web_football_matches_data where MID = ?";
			int rowRecord = jdbcTemplate.queryForObject(isExistSql,Integer.class,gameData.getMid());
			if(0==rowRecord){
				//批量插入
				batchInsertDatas.add(gameData);
				
			}else if(1==rowRecord){
				//批量更新
				batchUpdateDatas.add(gameData);
			}
		}
		if(batchInsertDatas.size()>0){
			int[] icount = batchInsert(batchInsertDatas);
			System.out.println("----------------------"+icount.length);
		}
		if(batchUpdateDatas.size()>0){
			int[] ucount = batchUpdate(batchUpdateDatas);
			System.out.println("*******************************"+ucount.length);
		}
		return 0;
	}
	
	//批量更新
	private int[] batchUpdate(final List<WebFootballMatchesData> batchUpdateDatas){
		String uSql = "update web_football_matches_data set MB_Win_Rate = ?,TG_Win_Rate = ?,M_Flat_Rate = ?,ShowTypeR = ?,M_LetB = ?,MB_LetB_Rate = ?,"
				+ "TG_LetB_Rate = ?,MB_Dime = ?,TG_Dime = ?,MB_Dime_Rate = ?,TG_Dime_Rate = ?,MB_Win_Rate_H = ?,TG_Win_Rate_H = ?,M_Flat_Rate_H = ?,"
				+ "ShowTypeHR = ?,M_LetB_H = ?,MB_LetB_Rate_H = ?,TG_LetB_Rate_H = ?,MB_Dime_H = ?,TG_Dime_H = ?,MB_Dime_Rate_H = ?,TG_Dime_Rate_H = ?,"
				+ "S_Single_Rate = ?,S_Double_Rate = ?,R_Show = ?, MB_Win_Rate_RB = ?,TG_Win_Rate_RB = ?,M_Flat_Rate_RB = ?,ShowTypeRB = ?,M_LetB_RB = ?,MB_LetB_Rate_RB = ?,"
				+ "TG_LetB_Rate_RB = ?,MB_Dime_RB = ?,TG_Dime_RB= ?,MB_Dime_Rate_RB = ?,TG_Dime_Rate_RB = ?,MB_Win_Rate_RB_H = ?,TG_Win_Rate_RB_H = ?,"
				+ "M_Flat_Rate_RB_H = ?,ShowTypeHRB = ?,M_LetB_RB_H = ?,MB_LetB_Rate_RB_H = ?,TG_LetB_Rate_RB_H = ?,MB_Dime_RB_H = ?,TG_Dime_RB_H = ?,"
				+ "MB_Dime_Rate_RB_H = ?,TG_Dime_Rate_RB_H = ?,S_Single_Rate_RB= ?,S_Double_Rate_RB = ?,RB_Show = ?,MB_Inball = ?,TG_Inball = ?,MB_Inball_HR = ?,"
				+ "TG_Inball_HR = ?,MB_Ball = ?,TG_Ball = ?,MB_Card = ?,TG_Card = ?,MB_Red = ?,TG_Red = ?,Hot = ?,isOpen = ?,isFinish = ?,isCancel = ?,"
				+ "isChecked = ?,isCheckout = ?,now_play = ?,source_type = ? where MID = ?";
		int[] updateCounts = jdbcTemplate.batchUpdate(uSql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				//今日赛事
				ps.setString(1, batchUpdateDatas.get(i).getMb_win_rate());
				ps.setString(2, batchUpdateDatas.get(i).getTg_win_rate());
				ps.setString(3, batchUpdateDatas.get(i).getM_flat_rate());
				ps.setString(4, batchUpdateDatas.get(i).getShowtyper());
				ps.setString(5, batchUpdateDatas.get(i).getM_letb());
				ps.setString(6, batchUpdateDatas.get(i).getMb_letb_rate());
				ps.setString(7, batchUpdateDatas.get(i).getTg_letb_rate());
				ps.setString(8, batchUpdateDatas.get(i).getMb_dime());
				ps.setString(9, batchUpdateDatas.get(i).getTg_dime());
				ps.setString(10, batchUpdateDatas.get(i).getMb_dime_rate());
				ps.setString(11, batchUpdateDatas.get(i).getTg_dime_rate());
				ps.setString(12, batchUpdateDatas.get(i).getMb_win_rate_h());
				ps.setString(13, batchUpdateDatas.get(i).getTg_win_rate_h());
				ps.setString(14, batchUpdateDatas.get(i).getM_flat_rate_h());
				ps.setString(15, batchUpdateDatas.get(i).getShowtypehr());
				ps.setString(16, batchUpdateDatas.get(i).getM_letb_h());
				ps.setString(17, batchUpdateDatas.get(i).getMb_letb_rate_h());
				ps.setString(18, batchUpdateDatas.get(i).getTg_letb_rate_h());
				ps.setString(19, batchUpdateDatas.get(i).getMb_dime_h());
				ps.setString(20, batchUpdateDatas.get(i).getTg_dime_h());
				ps.setString(21, batchUpdateDatas.get(i).getMb_dime_rate_h());
				ps.setString(22, batchUpdateDatas.get(i).getTg_dime_rate_h());
				ps.setString(23, batchUpdateDatas.get(i).getS_single_rate());
				ps.setString(24, batchUpdateDatas.get(i).getS_double_rate());
				ps.setString(25, batchUpdateDatas.get(i).getR_show());
				//滚球数据
				ps.setString(26, batchUpdateDatas.get(i).getMb_win_rate_rb());
				ps.setString(27, batchUpdateDatas.get(i).getTg_win_rate_rb());
				ps.setString(28, batchUpdateDatas.get(i).getM_flat_rate_rb());
				ps.setString(29, batchUpdateDatas.get(i).getShowtyperb());
				ps.setString(30, batchUpdateDatas.get(i).getM_letb_rb());
				ps.setString(31, batchUpdateDatas.get(i).getMb_letb_rate_rb());
				ps.setString(32, batchUpdateDatas.get(i).getTg_letb_rate_rb());
				ps.setString(33, batchUpdateDatas.get(i).getMb_dime_rb());
				ps.setString(34, batchUpdateDatas.get(i).getTg_dime_rb());
				ps.setString(35, batchUpdateDatas.get(i).getMb_dime_rate_rb());
				ps.setString(36, batchUpdateDatas.get(i).getTg_dime_rate_rb());
				ps.setString(37, batchUpdateDatas.get(i).getMb_win_rate_rb_h());
				ps.setString(38, batchUpdateDatas.get(i).getTg_win_rate_rb_h());
				ps.setString(39, batchUpdateDatas.get(i).getM_flat_rate_rb_h());
				ps.setString(40, batchUpdateDatas.get(i).getShowtypehrb());
				ps.setString(41, batchUpdateDatas.get(i).getM_letb_rb_h());
				ps.setString(42, batchUpdateDatas.get(i).getMb_letb_rate_rb_h());
				ps.setString(43, batchUpdateDatas.get(i).getTg_letb_rate_rb_h());
				ps.setString(44, batchUpdateDatas.get(i).getMb_dime_rb_h());
				ps.setString(45, batchUpdateDatas.get(i).getTg_dime_rb_h());
				ps.setString(46, batchUpdateDatas.get(i).getMb_dime_rate_rb_h());
				ps.setString(47, batchUpdateDatas.get(i).getTg_dime_rate_rb_h());
				ps.setString(48, batchUpdateDatas.get(i).getS_single_rate_rb());
				ps.setString(49, batchUpdateDatas.get(i).getS_double_rate_rb());
				ps.setString(50, batchUpdateDatas.get(i).getRb_show());
				ps.setString(51, batchUpdateDatas.get(i).getMb_inball());
				ps.setString(52, batchUpdateDatas.get(i).getTg_inball());
				ps.setString(53, batchUpdateDatas.get(i).getMb_inball_hr());
				ps.setString(54, batchUpdateDatas.get(i).getTg_inball_hr());
				ps.setString(55, batchUpdateDatas.get(i).getMb_ball());
				ps.setString(56, batchUpdateDatas.get(i).getTg_ball());
				ps.setString(57, batchUpdateDatas.get(i).getMb_card());
				ps.setString(58, batchUpdateDatas.get(i).getTg_card());
				ps.setString(59, batchUpdateDatas.get(i).getMb_red());
				ps.setString(60, batchUpdateDatas.get(i).getTg_red());
				ps.setString(61, batchUpdateDatas.get(i).getHot());
				ps.setString(62, batchUpdateDatas.get(i).getIsopen());
				ps.setString(63, batchUpdateDatas.get(i).getIsfinish());
				ps.setString(64, batchUpdateDatas.get(i).getIscancel());
				ps.setString(65, batchUpdateDatas.get(i).getIschecked());
				ps.setString(66, batchUpdateDatas.get(i).getIscheckout());
				ps.setString(67, batchUpdateDatas.get(i).getNow_play());
				ps.setString(68, batchUpdateDatas.get(i).getSource_type());
				ps.setInt(69, batchUpdateDatas.get(i).getMid());
			}
			
			@Override
			public int getBatchSize() {
				return batchUpdateDatas.size();
			}
		});
		return updateCounts;
	}
	
	//批量插入数据
	private int[] batchInsert(final List<WebFootballMatchesData> batchInsertDatas){
		String uSql = "insert into web_football_matches_data set MID = ?,Type = ?, MB_MID = ?, TG_MID = ?,MB_Team_cn = ?,TG_Team_cn = ?,M_Date = ?, M_Time = ?,"
				+ "M_Start = ?,M_League_cn = ?, MB_Win_Rate = ?,TG_Win_Rate = ?,M_Flat_Rate = ?,ShowTypeR = ?,M_LetB = ?,MB_LetB_Rate = ?,"
				+ "TG_LetB_Rate = ?,MB_Dime = ?,TG_Dime = ?,MB_Dime_Rate = ?,TG_Dime_Rate = ?,MB_Win_Rate_H = ?,TG_Win_Rate_H = ?,M_Flat_Rate_H = ?,"
				+ "ShowTypeHR = ?,M_LetB_H = ?,MB_LetB_Rate_H = ?,TG_LetB_Rate_H = ?,MB_Dime_H = ?,TG_Dime_H = ?,MB_Dime_Rate_H = ?,TG_Dime_Rate_H = ?,"
				+ "S_Single_Rate = ?,S_Double_Rate = ?,R_Show = ?, MB_Win_Rate_RB = ?,TG_Win_Rate_RB = ?,M_Flat_Rate_RB = ?,ShowTypeRB = ?,M_LetB_RB = ?,MB_LetB_Rate_RB = ?,"
				+ "TG_LetB_Rate_RB = ?,MB_Dime_RB = ?,TG_Dime_RB= ?,MB_Dime_Rate_RB = ?,TG_Dime_Rate_RB = ?,MB_Win_Rate_RB_H = ?,TG_Win_Rate_RB_H = ?,"
				+ "M_Flat_Rate_RB_H = ?,ShowTypeHRB = ?,M_LetB_RB_H = ?,MB_LetB_Rate_RB_H = ?,TG_LetB_Rate_RB_H = ?,MB_Dime_RB_H = ?,TG_Dime_RB_H = ?,"
				+ "MB_Dime_Rate_RB_H = ?,TG_Dime_Rate_RB_H = ?,S_Single_Rate_RB= ?,S_Double_Rate_RB = ?,RB_Show = ?,MB_Inball = ?,TG_Inball = ?,MB_Inball_HR = ?,"
				+ "TG_Inball_HR = ?,MB_Ball = ?,TG_Ball = ?,MB_Card = ?,TG_Card = ?,MB_Red = ?,TG_Red = ?,Hot = ?,isOpen = ?,isFinish = ?,isCancel = ?,"
				+ "isChecked = ?,isCheckout = ?,now_play = ?,source_type = ?";
		int[] updateCounts = jdbcTemplate.batchUpdate(uSql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, batchInsertDatas.get(i).getMid());
				ps.setString(2, batchInsertDatas.get(i).getType());
				ps.setInt(3, batchInsertDatas.get(i).getMb_mid());
				ps.setInt(4, batchInsertDatas.get(i).getTg_mid());
				ps.setString(5, batchInsertDatas.get(i).getMb_team_cn());
				ps.setString(6, batchInsertDatas.get(i).getTg_team_cn());
				ps.setDate(7, (java.sql.Date) batchInsertDatas.get(i).getM_date());
				ps.setString(8, batchInsertDatas.get(i).getM_time());
				ps.setTimestamp(9, (Timestamp) batchInsertDatas.get(i).getM_start());
				ps.setString(10, batchInsertDatas.get(i).getM_league_cn());
				
				ps.setString(11, batchInsertDatas.get(i).getMb_win_rate());
				ps.setString(12, batchInsertDatas.get(i).getTg_win_rate());
				ps.setString(13, batchInsertDatas.get(i).getM_flat_rate());
				ps.setString(14, batchInsertDatas.get(i).getShowtyper());
				ps.setString(15, batchInsertDatas.get(i).getM_letb());
				ps.setString(16, batchInsertDatas.get(i).getMb_letb_rate());
				ps.setString(17, batchInsertDatas.get(i).getTg_letb_rate());
				ps.setString(18, batchInsertDatas.get(i).getMb_dime());
				ps.setString(19, batchInsertDatas.get(i).getTg_dime());
				ps.setString(20, batchInsertDatas.get(i).getMb_dime_rate());
				ps.setString(21, batchInsertDatas.get(i).getTg_dime_rate());
				ps.setString(22, batchInsertDatas.get(i).getMb_win_rate_h());
				ps.setString(23, batchInsertDatas.get(i).getTg_win_rate_h());
				ps.setString(24, batchInsertDatas.get(i).getM_flat_rate_h());
				ps.setString(25, batchInsertDatas.get(i).getShowtypehr());
				ps.setString(26, batchInsertDatas.get(i).getM_letb_h());
				ps.setString(27, batchInsertDatas.get(i).getMb_letb_rate_h());
				ps.setString(28, batchInsertDatas.get(i).getTg_letb_rate_h());
				ps.setString(29, batchInsertDatas.get(i).getMb_dime_h());
				ps.setString(30, batchInsertDatas.get(i).getTg_dime_h());
				ps.setString(31, batchInsertDatas.get(i).getMb_dime_rate_h());
				ps.setString(32, batchInsertDatas.get(i).getTg_dime_rate_h());
				ps.setString(33, batchInsertDatas.get(i).getS_single_rate());
				ps.setString(34, batchInsertDatas.get(i).getS_double_rate());
				ps.setString(35, batchInsertDatas.get(i).getR_show());
				
				ps.setString(36, batchInsertDatas.get(i).getMb_win_rate_rb());
				ps.setString(37, batchInsertDatas.get(i).getTg_win_rate_rb());
				ps.setString(38, batchInsertDatas.get(i).getM_flat_rate_rb());
				ps.setString(39, batchInsertDatas.get(i).getShowtyperb());
				ps.setString(40, batchInsertDatas.get(i).getM_letb_rb());
				ps.setString(41, batchInsertDatas.get(i).getMb_letb_rate_rb());
				ps.setString(42, batchInsertDatas.get(i).getTg_letb_rate_rb());
				ps.setString(43, batchInsertDatas.get(i).getMb_dime_rb());
				ps.setString(44, batchInsertDatas.get(i).getTg_dime_rb());
				ps.setString(45, batchInsertDatas.get(i).getMb_dime_rate_rb());
				ps.setString(46, batchInsertDatas.get(i).getTg_dime_rate_rb());
				ps.setString(47, batchInsertDatas.get(i).getMb_win_rate_rb_h());
				ps.setString(48, batchInsertDatas.get(i).getTg_win_rate_rb_h());
				ps.setString(49, batchInsertDatas.get(i).getM_flat_rate_rb_h());
				ps.setString(50, batchInsertDatas.get(i).getShowtypehrb());
				ps.setString(51, batchInsertDatas.get(i).getM_letb_rb_h());
				ps.setString(52, batchInsertDatas.get(i).getMb_letb_rate_rb_h());
				ps.setString(53, batchInsertDatas.get(i).getTg_letb_rate_rb_h());
				ps.setString(54, batchInsertDatas.get(i).getMb_dime_rb_h());
				ps.setString(55, batchInsertDatas.get(i).getTg_dime_rb_h());
				ps.setString(56, batchInsertDatas.get(i).getMb_dime_rate_rb_h());
				ps.setString(57, batchInsertDatas.get(i).getTg_dime_rate_rb_h());
				ps.setString(58, batchInsertDatas.get(i).getS_single_rate_rb());
				ps.setString(59, batchInsertDatas.get(i).getS_double_rate_rb());
				ps.setString(60, batchInsertDatas.get(i).getRb_show());
				
				//比分更新，默认null
				ps.setString(61, batchInsertDatas.get(i).getMb_inball());
				ps.setString(62, batchInsertDatas.get(i).getTg_inball());
				ps.setString(63, batchInsertDatas.get(i).getMb_inball_hr());
				ps.setString(64, batchInsertDatas.get(i).getTg_inball_hr());
				ps.setString(65, batchInsertDatas.get(i).getMb_ball());
				ps.setString(66, batchInsertDatas.get(i).getTg_ball());
				ps.setString(67, batchInsertDatas.get(i).getMb_card());
				ps.setString(68, batchInsertDatas.get(i).getTg_card());
				ps.setString(69, batchInsertDatas.get(i).getMb_red());
				ps.setString(70, batchInsertDatas.get(i).getTg_red());
				
				
				ps.setString(71, batchInsertDatas.get(i).getHot());
				ps.setString(72, batchInsertDatas.get(i).getIsopen());
				ps.setString(73, batchInsertDatas.get(i).getIsfinish());
				ps.setString(74, batchInsertDatas.get(i).getIscancel());
				ps.setString(75, batchInsertDatas.get(i).getIschecked());
				ps.setString(76, batchInsertDatas.get(i).getIscheckout());
				ps.setString(77, batchInsertDatas.get(i).getNow_play());
				ps.setString(78, batchInsertDatas.get(i).getSource_type());
			}
			
			@Override
			public int getBatchSize() {
				return batchInsertDatas.size();
			}
		});
		return updateCounts;
	}
}
