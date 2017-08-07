package org.crawler.service.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.crawler.entity.GameData;
import org.crawler.entity.UrlData;
import org.crawler.service.UrlDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class UrlDaoImpl implements UrlDao {
	@Autowired 
	private JdbcTemplate jdbcTemplate;

	@Override
	public UrlData getUrlData(String wtype) {
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
			});
		return urlData;
	}

	@Override
	public void updateUid(String uid,String wtype) {
		String uuidSql = "update web_crawel_site set uid = ? where web_type = ?"; 
		jdbcTemplate.update(uuidSql, uid, wtype);
	}

	@Override
	public List<UrlData> getDataUrls() {
		List<UrlData> urlDatas = jdbcTemplate.query(
			"select uid,web_site from web_crawel_site where web_type = ?",
				new Object[]{2},
				new RowMapper<UrlData>() {
				public UrlData mapRow(ResultSet rs, int rowNum) throws SQLException {
					UrlData urlData = new UrlData();
					urlData.setUid(rs.getString("uid"));
					urlData.setWeb_site(rs.getString("web_site"));
					return urlData;
				}
		});
		return urlDatas;
	}

	@Override
	public int saveGameDatas(List<GameData> gameDatas) {
		List<GameData> batchUpdateDatas = new ArrayList<GameData>();
		List<GameData> batchInsertDatas = new ArrayList<GameData>();
		for(GameData gameData : gameDatas){
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
	public int[] batchUpdate(final List<GameData> batchUpdateDatas){
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
				ps.setString(4, batchUpdateDatas.get(i).getShowTypeR());
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
				ps.setString(15, batchUpdateDatas.get(i).getShowTypeHR());
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
				ps.setString(29, batchUpdateDatas.get(i).getShowTypeRB());
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
				ps.setString(40, batchUpdateDatas.get(i).getShowTypeHRB());
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
				ps.setString(62, batchUpdateDatas.get(i).getIsOpen());
				ps.setString(63, batchUpdateDatas.get(i).getIsFinish());
				ps.setString(64, batchUpdateDatas.get(i).getIsCancel());
				ps.setString(65, batchUpdateDatas.get(i).getIsChecked());
				ps.setString(66, batchUpdateDatas.get(i).getIsCheckout());
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
	public int[] batchInsert(final List<GameData> batchInsertDatas){
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
				ps.setDate(7, (Date) batchInsertDatas.get(i).getM_date());
				ps.setString(8, batchInsertDatas.get(i).getM_time());
				ps.setTimestamp(9, (Timestamp) batchInsertDatas.get(i).getM_start());
				ps.setString(10, batchInsertDatas.get(i).getM_league_cn());
				
				ps.setString(11, batchInsertDatas.get(i).getMb_win_rate());
				ps.setString(12, batchInsertDatas.get(i).getTg_win_rate());
				ps.setString(13, batchInsertDatas.get(i).getM_flat_rate());
				ps.setString(14, batchInsertDatas.get(i).getShowTypeR());
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
				ps.setString(25, batchInsertDatas.get(i).getShowTypeHR());
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
				ps.setString(39, batchInsertDatas.get(i).getShowTypeRB());
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
				ps.setString(50, batchInsertDatas.get(i).getShowTypeHRB());
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
				ps.setString(72, batchInsertDatas.get(i).getIsOpen());
				ps.setString(73, batchInsertDatas.get(i).getIsFinish());
				ps.setString(74, batchInsertDatas.get(i).getIsCancel());
				ps.setString(75, batchInsertDatas.get(i).getIsChecked());
				ps.setString(76, batchInsertDatas.get(i).getIsCheckout());
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
