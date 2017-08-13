package org.crawler.service.impl;

import java.io.IOException;
import java.sql.Date;
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
import org.crawler.entity.WebSportData;
import org.crawler.service.GetDatasService;
import org.crawler.util.CrawlerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class GetDatasServiceImpl implements GetDatasService {
	@Autowired 
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired 
	private JdbcTemplate jdbcTemplate;

	/**
	 * 妯℃嫙鐢ㄦ埛鐧婚檰锛屽苟鏇存柊uid
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
			String uuidSql = "update web_crawel_site set uid = ?"; 
			jdbcTemplate.update(uuidSql, nuid);
			msg = "鏇存柊鎴愬姛";
		}
		return msg;
	}

	@Override
	/**
	 * 鑾峰彇瓒崇悆浠婃棩璧涗簨
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
//		'鐖卞皵鍏拌仈璧涙澂','10202','10201','鎴堝皵闊﹁仈','鐧诲厠灏斿厠','C','1','0.72','0.60','O2.5 / 3','U2.5 / 3','0.69',
//		'0.60','5.60','1.23','4.10','鍗�','鍙�','1.65','1.64','2842706','C','0.5','0.51','0.79','O1 / 1.5',
//		'U1 / 1.5','0.47','0.82','4.80','1.78','2.05','11','0','','N');
		List<WebSportData> gameDatas = new ArrayList<WebSportData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
			WebSportData gameData = new WebSportData();
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
		
//		parent.str_more='鐩存挱鎶曟敞';parent.GameHead=new Array('gid','timer','league','gnum_h','gnum_c','team_h','team_c','strong','ratio','ior_RH',
//		'ior_RC','ratio_o','ratio_u','ior_OUH','ior_OUC','no1','no2','no3','score_h','score_c','hgid','hstrong','hratio','ior_HRH','ior_HRC','hratio_o',
//		'hratio_u','ior_HOUH','ior_HOUC','redcard_h','redcard_c','lastestscore_h','lastestscore_c','ior_MH','ior_MC','ior_MN','ior_HMH','ior_HMC','ior_HMN',
//		'str_odd','str_even','ior_EOO','ior_EOE','eventid','hot','center_tv','play','datetime','retimeset','more','sort_team_h','i_timer',
//		'sort_dy','sort_tmax');
//		parent.GameFT.length=0;parent.GameFT[0]=new Array('2828720','72','缇庡浗鑱屼笟澶ц仈鐩�','70558','70557','鑲惃鏂煄浣撹偛浼�','浜氱壒鍏板ぇ鑱�','H','0','0.40','0.90',
//		'O1.5','U1.5','0.59','0.70','','','','1','0','2828721','','','','','','','','','0','0','H','','1.18','21.00','5.40','','','','鍗�','鍙�',
//		'1.54','2.35','2129678','','img','Y','08-06<br>08:16p','2H^27','3','鑲惃鏂煄浣撹偛浼�','87','1.18','87');
		List<String> headArray = map.get("headTitle");
		List<WebSportData> gameDatas = new ArrayList<WebSportData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
			WebSportData gameData = new WebSportData();
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
		UrlData urlData = getUrlData("3");
		String crawelUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();		
		Map<String,List<String>> map = new HashMap<String,List<String>>();		
		map = CrawlerUtil.getResult(crawelUrl, uid, lanaguage, "3",0);
		
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
//		'鐖卞皵鍏拌仈璧涙澂','10202','10201','鎴堝皵闊﹁仈','鐧诲厠灏斿厠','C','1','0.72','0.60','O2.5 / 3','U2.5 / 3','0.69',
//		'0.60','5.60','1.23','4.10','鍗�','鍙�','1.65','1.64','2842706','C','0.5','0.51','0.79','O1 / 1.5',
//		'U1 / 1.5','0.47','0.82','4.80','1.78','2.05','11','0','','N');
		List<WebSportData> gameDatas = new ArrayList<WebSportData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
			WebSportData gameData = new WebSportData();
			gameData.setMid(Integer.parseInt(dataArray[getElementIndexByValue("gid",headArray)]));
			gameData.setType("BK");
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
			gameData.setRatio_ouho(dataArray[getElementIndexByValue("ratio_ouho",headArray)]);
			gameData.setRatio_ouhu(dataArray[getElementIndexByValue("ratio_ouhu",headArray)]);
			gameData.setRatio_ouco(dataArray[getElementIndexByValue("ratio_ouco",headArray)]);
			gameData.setRatio_oucu(dataArray[getElementIndexByValue("ratio_oucu",headArray)]);
			gameData.setIor_ouho(dataArray[getElementIndexByValue("ior_OUHO",headArray)]);
			gameData.setIor_ouhu(dataArray[getElementIndexByValue("ior_OUHU",headArray)]);
			gameData.setIor_ouco(dataArray[getElementIndexByValue("ior_OUCO",headArray)]);
			gameData.setIor_oucu(dataArray[getElementIndexByValue("ior_OUCU",headArray)]);
			gameData.setIsmaster(dataArray[getElementIndexByValue("isMaster",headArray)]);
			
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
	public int getBkRbMatchesDatas() {
		UrlData urlData = getUrlData("3");
		String crawelUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();
		Map<String,List<String>> map = new HashMap<String,List<String>>();		
		map = CrawlerUtil.getResult(crawelUrl, uid, lanaguage, "4",0);
		
//		parent.str_more='鐩存挱鎶曟敞';parent.GameHead=new Array('gid','timer','league','gnum_h','gnum_c','team_h','team_c','strong','ratio','ior_RH',
//		'ior_RC','ratio_o','ratio_u','ior_OUH','ior_OUC','no1','no2','no3','score_h','score_c','hgid','hstrong','hratio','ior_HRH','ior_HRC','hratio_o',
//		'hratio_u','ior_HOUH','ior_HOUC','redcard_h','redcard_c','lastestscore_h','lastestscore_c','ior_MH','ior_MC','ior_MN','ior_HMH','ior_HMC','ior_HMN',
//		'str_odd','str_even','ior_EOO','ior_EOE','eventid','hot','center_tv','play','datetime','retimeset','more','sort_team_h','i_timer',
//		'sort_dy','sort_tmax');
//		parent.GameFT.length=0;parent.GameFT[0]=new Array('2828720','72','缇庡浗鑱屼笟澶ц仈鐩�','70558','70557','鑲惃鏂煄浣撹偛浼�','浜氱壒鍏板ぇ鑱�','H','0','0.40','0.90',
//		'O1.5','U1.5','0.59','0.70','','','','1','0','2828721','','','','','','','','','0','0','H','','1.18','21.00','5.40','','','','鍗�','鍙�',
//		'1.54','2.35','2129678','','img','Y','08-06<br>08:16p','2H^27','3','鑲惃鏂煄浣撹偛浼�','87','1.18','87');
		List<String> headArray = map.get("headTitle");
		List<WebSportData> gameDatas = new ArrayList<WebSportData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		List<String> gameDataStrs = map.get("gameData");
		for(String gameDataStr : gameDataStrs){
			String[] dataArray = gameDataStr.replaceAll("'", "").split(",");
			
			String[] timeArray = dataArray[getElementIndexByValue("score_info",headArray)].split("<br>");
			long time = 0;
			long detailTime = 0;
//			try {
//				time = dateFormat.parse(year+"-"+timeArray[0]).getTime();
//				detailTime = timeFormat.parse(year+"-"+timeArray[0]+" "+timeArray[1].substring(0,timeArray[1].length()-1)+":00").getTime();
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			Date date = new java.sql.Date(time);
			Timestamp dateTime = new Timestamp(detailTime);
			WebSportData gameData = new WebSportData();
			gameData.setMid(Integer.parseInt(dataArray[getElementIndexByValue("gid",headArray)]));
			gameData.setType("BK");
			gameData.setMb_mid(Integer.parseInt(dataArray[getElementIndexByValue("gnum_h",headArray)]));
			gameData.setTg_mid(Integer.parseInt(dataArray[getElementIndexByValue("gnum_c",headArray)]));
			gameData.setMb_team_cn(dataArray[getElementIndexByValue("team_h",headArray)]);
			gameData.setTg_team_cn(dataArray[getElementIndexByValue("team_c",headArray)]);
			
			gameData.setM_league_cn(dataArray[getElementIndexByValue("league",headArray)]);
//			gameData.setM_date(date);
//			gameData.setM_time(timeArray[1]);
//			gameData.setM_start(dateTime);
			gameData.setM_type("1");	
			gameData.setShowtyperb(dataArray[getElementIndexByValue("strong",headArray)]);
			gameData.setM_letb_rb(dataArray[getElementIndexByValue("ratio",headArray)]);
			gameData.setMb_letb_rate_rb(dataArray[getElementIndexByValue("ior_RH",headArray)]);
			gameData.setTg_letb_rate_rb(dataArray[getElementIndexByValue("ior_RC",headArray)]);
			gameData.setMb_dime_rb(dataArray[getElementIndexByValue("ratio_o",headArray)]);
			gameData.setTg_dime_rb(dataArray[getElementIndexByValue("ratio_u",headArray)]);
			gameData.setMb_dime_rate_rb(dataArray[getElementIndexByValue("ior_OUH",headArray)]);
			gameData.setTg_dime_rate_rb(dataArray[getElementIndexByValue("ior_OUC",headArray)]);
			
//			gameData.setMb_win_rate_rb(dataArray[getElementIndexByValue("ior_MH",headArray)]);
			gameData.setTg_win_rate_rb(dataArray[getElementIndexByValue("ior_MC",headArray)]);
			
			gameData.setRatio_ouho(dataArray[getElementIndexByValue("ratio_ouho",headArray)]);
			gameData.setRatio_ouhu(dataArray[getElementIndexByValue("ratio_ouhu",headArray)]);
			gameData.setRatio_ouco(dataArray[getElementIndexByValue("ratio_ouco",headArray)]);
			gameData.setRatio_oucu(dataArray[getElementIndexByValue("ratio_oucu",headArray)]);
			gameData.setIor_ouho(dataArray[getElementIndexByValue("ior_OUHO",headArray)]);
			gameData.setIor_ouhu(dataArray[getElementIndexByValue("ior_OUHU",headArray)]);
			gameData.setIor_ouco(dataArray[getElementIndexByValue("ior_OUCO",headArray)]);
			gameData.setIor_oucu(dataArray[getElementIndexByValue("ior_OUCU",headArray)]);
			gameData.setIsmaster(dataArray[getElementIndexByValue("isMaster",headArray)]);
			
			
//			gameData.setMb_ball(dataArray[getElementIndexByValue("score_h",headArray)]);
//			gameData.setTg_ball(dataArray[getElementIndexByValue("score_c",headArray)]);
			gameData.setRb_show("1");
			gameData.setSource_type("HG");
			gameDatas.add(gameData);
			
		}
		int count = gameDataStrs.size();
		saveGameDatas(gameDatas);
		System.out.println(count);
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
	
	private int saveGameDatas(List<WebSportData> gameDatas) {
		List<WebSportData> batchUpdateDatas = new ArrayList<WebSportData>();
		List<WebSportData> batchInsertDatas = new ArrayList<WebSportData>();
		for(WebSportData gameData : gameDatas){
			String isExistSql = "select count(*) from web_sport_data where MID = ?";
			int rowRecord = jdbcTemplate.queryForObject(isExistSql,Integer.class,gameData.getMid());
			if(0==rowRecord){
				//鎵归噺鎻掑叆
				batchInsertDatas.add(gameData);
				
			}else if(1==rowRecord){
				//鎵归噺鏇存柊
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
	
	//鎵归噺鏇存柊
	private int[] batchUpdate(final List<WebSportData> batchUpdateDatas){
		String uSql = "update web_sport_data set MB_Win_Rate = :mb_win_rate,TG_Win_Rate = :tg_win_rate,M_Flat_Rate = :m_flat_rate,ShowTypeR = :showtyper,M_LetB = :m_letb,MB_LetB_Rate = :mb_letb_rate,"
				+ "TG_LetB_Rate = :tg_letb_rate,MB_Dime = :mb_dime,TG_Dime = :tg_dime,MB_Dime_Rate = :mb_dime_rate,TG_Dime_Rate = :tg_dime_rate,MB_Win_Rate_H = :mb_win_rate_h,TG_Win_Rate_H = :tg_win_rate_h,M_Flat_Rate_H = :m_flat_rate_h,"
				+ "ShowTypeHR = :showtypehr,M_LetB_H = :m_letb_h,MB_LetB_Rate_H = :mb_letb_rate_h,TG_LetB_Rate_H = :tg_letb_rate_h,MB_Dime_H = :mb_dime_h,TG_Dime_H = :tg_dime_h,MB_Dime_Rate_H = :mb_dime_rate_h,TG_Dime_Rate_H = :tg_dime_rate_h,"
				+ "S_Single_Rate = :s_single_rate,S_Double_Rate = :s_double_rate,R_Show = :r_show, MB_Win_Rate_RB = :mb_win_rate_rb,TG_Win_Rate_RB = :tg_win_rate_rb,M_Flat_Rate_RB = :m_flat_rate_rb,ShowTypeRB = :showtyperb,M_LetB_RB = :m_letb_rb,MB_LetB_Rate_RB = :mb_letb_rate_rb,"
				+ "TG_LetB_Rate_RB = :tg_letb_rate_rb,MB_Dime_RB = :mb_dime_rb,TG_Dime_RB= :tg_dime_rb,MB_Dime_Rate_RB = :mb_dime_rate_rb,TG_Dime_Rate_RB = :tg_dime_rate_rb,MB_Win_Rate_RB_H = :mb_win_rate_rb_h,TG_Win_Rate_RB_H = :tg_win_rate_rb_h,"
				+ "M_Flat_Rate_RB_H = :m_flat_rate_rb_h,ShowTypeHRB = :showtypehrb,M_LetB_RB_H = :m_letb_rb_h,MB_LetB_Rate_RB_H = :mb_letb_rate_rb_h,TG_LetB_Rate_RB_H = :tg_letb_rate_rb_h,MB_Dime_RB_H = :mb_dime_rb_h,TG_Dime_RB_H = :tg_dime_rb_h,"
				+ "MB_Dime_Rate_RB_H = :mb_dime_rate_rb_h,TG_Dime_Rate_RB_H = :tg_dime_rate_rb_h,S_Single_Rate_RB= :s_single_rate_rb,S_Double_Rate_RB = :s_double_rate_rb,ratio_ouho = :ratio_ouho,ratio_ouhu = :ratio_ouhu,ratio_ouco = :ratio_ouco,"
				+ "ratio_oucu = :ratio_oucu,ior_OUHO = :ior_ouho,ior_OUHU = :ior_ouhu,ior_OUCO = :ior_ouco,ior_OUCU = :ior_oucu,isMaster = :ismaster,RB_Show = :rb_show,MB_Inball = :mb_inball,TG_Inball = :tg_inball,MB_Inball_HR = :mb_inball_hr,"
				+ "TG_Inball_HR = :tg_inball_hr,MB_Ball = :mb_ball,TG_Ball = :tg_ball,MB_Card = :mb_card,TG_Card = :tg_card,MB_Red = :mb_red,TG_Red = :tg_red,Hot = :hot,isOpen = :isopen,isFinish = :isfinish,isCancel = :iscancel,"
				+ "isChecked = :ischecked,isCheckout = :ischeckout,now_play = :now_play,source_type = :source_type where MID = :mid";
		
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(batchUpdateDatas.toArray());
		
		int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(uSql, batch);
		return updateCounts;

	}
	
	//鎵归噺鎻掑叆鏁版嵁
	private int[] batchInsert(final List<WebSportData> batchInsertDatas){
		String uSql = "insert into web_sport_data set MID = :mid,Type = :type, MB_MID = :mb_mid, TG_MID = :tg_mid,MB_Team_cn = :mb_team_cn,TG_Team_cn = :tg_team_cn,M_Date = :m_date, M_Time = :m_time,"
				+ "M_Start = :m_start,M_League_cn = :m_league_cn, MB_Win_Rate = :mb_win_rate,TG_Win_Rate = :tg_win_rate,M_Flat_Rate = :m_flat_rate,ShowTypeR = :showtyper,M_LetB = :m_letb,MB_LetB_Rate = :mb_letb_rate,"
				+ "TG_LetB_Rate = :tg_letb_rate,MB_Dime = :mb_dime,TG_Dime = :tg_dime,MB_Dime_Rate = :mb_dime_rate,TG_Dime_Rate = :tg_dime_rate,MB_Win_Rate_H = :mb_win_rate_h,TG_Win_Rate_H = :tg_win_rate_h,M_Flat_Rate_H = :m_flat_rate_h,"
				+ "ShowTypeHR = :showtypehr,M_LetB_H = :m_letb_h,MB_LetB_Rate_H = :mb_letb_rate_h,TG_LetB_Rate_H = :tg_letb_rate_h,MB_Dime_H = :mb_dime_h,TG_Dime_H = :tg_dime_h,MB_Dime_Rate_H = :mb_dime_rate_h,TG_Dime_Rate_H = :tg_dime_rate_h,"
				+ "S_Single_Rate = :s_single_rate,S_Double_Rate = :s_double_rate,R_Show = :r_show, MB_Win_Rate_RB = :mb_win_rate_rb,TG_Win_Rate_RB = :tg_win_rate_rb,M_Flat_Rate_RB = :m_flat_rate_rb,ShowTypeRB = :showtyperb,M_LetB_RB = :m_letb_rb,MB_LetB_Rate_RB = :mb_letb_rate_rb,"
				+ "TG_LetB_Rate_RB = :tg_letb_rate_rb,MB_Dime_RB = :mb_dime_rb,TG_Dime_RB= :tg_dime_rb,MB_Dime_Rate_RB = :mb_dime_rate_rb,TG_Dime_Rate_RB = :tg_dime_rate_rb,MB_Win_Rate_RB_H = :mb_win_rate_rb_h,TG_Win_Rate_RB_H = :tg_win_rate_rb_h,"
				+ "M_Flat_Rate_RB_H = :m_flat_rate_rb_h,ShowTypeHRB = :showtypehrb,M_LetB_RB_H = :m_letb_rb_h,MB_LetB_Rate_RB_H = :mb_letb_rate_rb_h,TG_LetB_Rate_RB_H = :tg_letb_rate_rb_h,MB_Dime_RB_H = :mb_dime_rb_h,TG_Dime_RB_H = :tg_dime_rb_h,"
				+ "MB_Dime_Rate_RB_H = :mb_dime_rate_rb_h,TG_Dime_Rate_RB_H = :tg_dime_rate_rb_h,S_Single_Rate_RB= :s_single_rate_rb,S_Double_Rate_RB = :s_double_rate_rb,ratio_ouho = :ratio_ouho,ratio_ouhu = :ratio_ouhu,ratio_ouco = :ratio_ouco,"
				+ "ratio_oucu = :ratio_oucu,ior_OUHO = :ior_ouho,ior_OUHU = :ior_ouhu,ior_OUCO = :ior_ouco,ior_OUCU = :ior_oucu,isMaster = :ismaster,RB_Show = :rb_show,MB_Inball = :mb_inball,TG_Inball = :tg_inball,MB_Inball_HR = :mb_inball_hr,"
				+ "TG_Inball_HR = :tg_inball_hr,MB_Ball = :mb_ball,TG_Ball = :tg_ball,MB_Card = :mb_card,TG_Card = :tg_card,MB_Red = :mb_red,TG_Red = :tg_red,Hot = :hot,isOpen = :isopen,isFinish = :isfinish,isCancel = :iscancel,"
				+ "isChecked = :ischecked,isCheckout = :ischeckout,now_play = :now_play,source_type = :source_type";
		
		SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(batchInsertDatas.toArray());
		
		int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(uSql, batch);
		return updateCounts;
	}
}
