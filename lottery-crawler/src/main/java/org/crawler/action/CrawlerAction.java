package org.crawler.action;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.crawler.entity.GameData;
import org.crawler.entity.UrlData;
import org.crawler.service.UrlDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlerAction {
	@Autowired
	private UrlDao urlDao;
	//模拟登录获取uid
	public void LoginAction() throws ClientProtocolException, IOException{
		UrlData urlData = urlDao.getUrlData("1");
		String loginUsername = urlData.getUsername();
		String password = urlData.getPassword();
		String loginUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();
		
		Response reponse = Request.Post(loginUrl)
		.bodyForm(Form.form().add("uid",uid).add("langx",lanaguage).add("mac","")
		.add("ver","").add("JE","").add("username",loginUsername).add("password",password)
		.add("checkbox","on").build()).execute();
		
		String uidString = reponse.returnContent().asString();
//		System.out.println(uidString);
		String pattern = "top.uid = \'(\\w+)\'";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(uidString);
		if(m.find()){
//			System.out.println(m.group(1));
			urlDao.updateUid(m.group(1), "2");
		}
		
	}
	
	//足球今日赛事数据爬取
	public String CrawelData() throws ClientProtocolException, IOException, ParseException{
		UrlData urlData = urlDao.getUrlData("2");
		String crawelUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();
		
		String dataUrl = crawelUrl+"?uid="+uid+"&rtype=r&langx="+lanaguage+"&mtype=3&page_no=0&league_id=&hot_game=undefined&sort_type=undefined&zbreload=1&l=ALL";
		
		Response rep = Request.Get(dataUrl).execute();
		String dataString = rep.returnContent().asString();
		
		String pattern1 = "parent.GameHead=new Array\\((.+)\\);";
		Pattern r1 = Pattern.compile(pattern1);
		Matcher m1 = r1.matcher(dataString);
//		System.out.println(m1.find());
		String[] headArray = null;
		if(m1.find()){
//			System.out.println(m1.group(1));
			headArray = m1.group(1).replaceAll("'", "").split(",");
		}
//		System.out.println(dataString);
//		StringTokenizer st = new StringTokenizer(m1.group(1), ",");
//		String[] data = new String[st.countTokens()];
//		int i=0;
//		while(st.hasMoreTokens()){
//			data[i++] = st.nextToken();
//		}
//		parent.GameHead=new Array('gid','datetime','league','gnum_h','gnum_c','team_h','team_c','strong',
//		'ratio','ior_RH','ior_RC','ratio_o','ratio_u','ior_OUH','ior_OUC','ior_MH','ior_MC','ior_MN',
//		'str_odd','str_even','ior_EOO','ior_EOE','hgid','hstrong','hratio','ior_HRH','ior_HRC','hratio_o',
//		'hratio_u','ior_HOUH','ior_HOUC','ior_HMH','ior_HMC','ior_HMN','more','eventid','hot','play');
//		parent.GameFT[0]=new Array('2842706','08-07<br>11:00a<br><font color=red>Running Ball</font>',
//		'爱尔兰联赛杯','10202','10201','戈尔韦联','登克尔克','C','1','0.72','0.60','O2.5 / 3','U2.5 / 3','0.69',
//		'0.60','5.60','1.23','4.10','单','双','1.65','1.64','2842706','C','0.5','0.51','0.79','O1 / 1.5',
//		'U1 / 1.5','0.47','0.82','4.80','1.78','2.05','11','0','','N');

		String pattern2 = "parent.GameFT\\[\\d+\\]=new Array\\((.*)\\);";
		Pattern r2 = Pattern.compile(pattern2);		
		Matcher m2 = r2.matcher(dataString);
		int count = 0;
		List<GameData> gameDatas = new ArrayList<GameData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		while(m2.find()){
			count++;
//			System.out.println(m2.group(1));
//			System.out.println(m2.group(1).split(",")[1]);
//			StringTokenizer stz = new StringTokenizer(m2.group(1),",");
			String[] dataArray = m2.group(1).replaceAll("'", "").split(",");
			String[] timeArray = dataArray[getElementIndexByValue("datetime",headArray)].split("<br>");
			long time = dateFormat.parse(year+"-"+timeArray[0]).getTime();
			Date date = new java.sql.Date(time);
			long detailTime = timeFormat.parse(year+"-"+timeArray[0]+" "+timeArray[1].substring(0,timeArray[1].length()-1)+":00").getTime();
			Timestamp dateTime = new Timestamp(detailTime);
			GameData gameData = new GameData();
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
			
			
//			parent.GameHead=new Array('gid','datetime','league','gnum_h','gnum_c','team_h','team_c','strong',
//			'ratio','ior_RH','ior_RC','ratio_o','ratio_u','ior_OUH','ior_OUC','ior_MH','ior_MC','ior_MN',
//			'str_odd','str_even','ior_EOO','ior_EOE','hgid','hstrong','hratio','ior_HRH','ior_HRC','hratio_o',
//			'hratio_u','ior_HOUH','ior_HOUC','ior_HMH','ior_HMC','ior_HMN','more','eventid','hot','play');
//			parent.GameFT[0]=new Array('2842706','08-07<br>11:00a<br><font color=red>Running Ball</font>',
//			'爱尔兰联赛杯','10202','10201','戈尔韦联','登克尔克','C','1','0.72','0.60','O2.5 / 3','U2.5 / 3','0.69',
//			'0.60','5.60','1.23','4.10','单','双','1.65','1.64','2842706','C','0.5','0.51','0.79','O1 / 1.5',
//			'U1 / 1.5','0.47','0.82','4.80','1.78','2.05','11','0','','N');

			gameData.setShowTypeR(dataArray[getElementIndexByValue("strong",headArray)]);
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
			gameData.setShowTypeHR(dataArray[getElementIndexByValue("hstrong",headArray)]);
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
//			gameData.setHot(dataArray[36]);
			gameData.setSource_type("HG");
//			gameData.setHgid(Integer.parseInt(dataArray[22]));
//			gameData.setMore(dataArray[34]);
//			gameData.setEventid(dataArray[35]);
//			gameData.setPlay(dataArray[37]);
			gameDatas.add(gameData);
			
		}
		urlDao.saveGameDatas(gameDatas);
		System.out.println(count);
		return null;
	}
	
	//足球滚球赛事爬取
	public String CrawelRbData() throws ClientProtocolException, IOException, ParseException{
		UrlData urlData = urlDao.getUrlData("2");
		String crawelUrl = urlData.getWeb_site();
		String lanaguage = urlData.getLanguage();
		String uid = urlData.getUid();
		
		String dataUrl = crawelUrl+"?uid="+uid+"&rtype=re&langx="+lanaguage+"&mtype=3&delay=&league_id=";
		
		Response rep = Request.Get(dataUrl).execute();
		String dataString = rep.returnContent().asString();
//		System.out.println(dataString);
		String pattern1 = "parent.GameHead=new Array\\((.+)\\);";
		Pattern r1 = Pattern.compile(pattern1);
		Matcher m1 = r1.matcher(dataString);
//		System.out.println(m1.find());
		String[] headArray = null;
		if(m1.find()){
//			System.out.println(m1.group(1));
			headArray = m1.group(1).replaceAll("'", "").split(",");
		}
//		System.out.println(dataString);
		String pattern2 = "parent.GameFT\\[\\d+\\]=new Array\\((.*)\\);";
		Pattern r2 = Pattern.compile(pattern2);		
		Matcher m2 = r2.matcher(dataString);
		int count = 0;
		List<GameData> gameDatas = new ArrayList<GameData>();
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		while(m2.find()){
			count++;
			String[] dataArray = m2.group(1).replaceAll("'", "").split(",");
			
			String[] timeArray = dataArray[getElementIndexByValue("datetime",headArray)].split("<br>");
			long time = dateFormat.parse(year+"-"+timeArray[0]).getTime();
			Date date = new java.sql.Date(time);
			long detailTime = timeFormat.parse(year+"-"+timeArray[0]+" "+timeArray[1].substring(0,timeArray[1].length()-1)+":00").getTime();
			Timestamp dateTime = new Timestamp(detailTime);
			GameData gameData = new GameData();
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
			
//			parent.str_more='直播投注';parent.GameHead=new Array('gid','timer','league','gnum_h','gnum_c','team_h','team_c','strong','ratio','ior_RH',
//			'ior_RC','ratio_o','ratio_u','ior_OUH','ior_OUC','no1','no2','no3','score_h','score_c','hgid','hstrong','hratio','ior_HRH','ior_HRC','hratio_o',
//			'hratio_u','ior_HOUH','ior_HOUC','redcard_h','redcard_c','lastestscore_h','lastestscore_c','ior_MH','ior_MC','ior_MN','ior_HMH','ior_HMC','ior_HMN',
//			'str_odd','str_even','ior_EOO','ior_EOE','eventid','hot','center_tv','play','datetime','retimeset','more','sort_team_h','i_timer',
//			'sort_dy','sort_tmax');
//			parent.GameFT.length=0;parent.GameFT[0]=new Array('2828720','72','美国职业大联盟','70558','70557','肯萨斯城体育会','亚特兰大联','H','0','0.40','0.90',
//			'O1.5','U1.5','0.59','0.70','','','','1','0','2828721','','','','','','','','','0','0','H','','1.18','21.00','5.40','','','','单','双',
//			'1.54','2.35','2129678','','img','Y','08-06<br>08:16p','2H^27','3','肯萨斯城体育会','87','1.18','87');

			
			gameData.setShowTypeRB(dataArray[getElementIndexByValue("strong",headArray)]);
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
			gameData.setShowTypeHRB(dataArray[getElementIndexByValue("hstrong",headArray)]);
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
//			gameData.setHot(dataArray[36]);
			gameData.setRb_show("1");
			gameData.setSource_type("HG");
//			gameData.setHgid(Integer.parseInt(dataArray[22]));
//			gameData.setMore(dataArray[34]);
//			gameData.setEventid(dataArray[35]);
//			gameData.setPlay(dataArray[37]);
			gameDatas.add(gameData);
			
		}
		urlDao.saveGameDatas(gameDatas);
		System.out.println(count);
		return null;
	}
	
	private int getElementIndexByValue(String val,String[] vals){
		for(int i=0;i<vals.length;i++){
			if(val.equals(vals[i])){
				 return i;
			}
		}
		return -1;
	}
}
