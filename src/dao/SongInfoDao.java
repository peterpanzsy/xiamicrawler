package dao;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.PreparedStatement;
import com.xiami.crawler.SongInfo;

public class SongInfoDao extends Dao {

	public SongInfoDao() throws ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int insertSongInfo(SongInfo sInfo){
		int status = -1;
		//String insertData = "insert into xiamisonginfoc(id, sid, sname, singer, album, lyricist, composer, arranger)values(?,?,?,?,?,?,?,?)";
		String insertData = "insert into xxsonginfoc(id, sid, sname, singer, album)values(?,?,?,?,?)";
		try {
			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(insertData);
			conn.setAutoCommit(false);
			pstmt.setString(1, sInfo.id);
			pstmt.setString(2, sInfo.sid);
			pstmt.setString(3, sInfo.sname);
			pstmt.setString(4, sInfo.singer);
			pstmt.setString(5, sInfo.album);
			//pstmt.setString(6, sInfo.lyricist);
			//pstmt.setString(7, sInfo.composer);
			//pstmt.setString(8, sInfo.arranger);
			status = pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Cannot insert info into table \"xxsonginfoc\". ");
		}
		return status;
	}
	public int updateSongInfo(String sid, String id){
		int status = -1;
		String updateData = "update xxsonginfos set sid = \'" + sid + "\' where id = " + id;
		try {
			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(updateData);
			conn.setAutoCommit(false);
			status = pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Cannot update info in table \"xxsonginfos\". ");
		}
		return status;
	}
	public ArrayList<Map<String,String>> getResult(String cols,String tab){
		String queryString = "select " + cols+ " from " + tab + " where id not in (select id from xxsonginfoc)";
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(queryString);
			rs = pstmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int colsLen = metaData.getColumnCount();
			while(rs.next()){
				Map<String,String> map = new HashMap<String,String>();
				for(int i = 0; i < colsLen; i++){
					String colName = metaData.getColumnName(i+1);
					String colVal = rs.getString(colName);
					map.put(colName, colVal);
				}
				list.add(map);
			}
			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

}
