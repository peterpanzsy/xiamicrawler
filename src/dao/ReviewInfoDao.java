package dao;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.PreparedStatement;
import com.xiami.crawler.ReviewInfo;

public class ReviewInfoDao extends Dao{

	public ReviewInfoDao() throws ClassNotFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public int insertListenInfo(ReviewInfo rInfo){
		int status = -1;
		String insertData = "insert into xxreview(id, sid, reviewer, location, content, time)values(?,?,?,?,?,?)";
		try {
			PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(insertData);
			conn.setAutoCommit(false);
			pstmt.setString(1, rInfo.id);
			pstmt.setString(2, rInfo.sid);
			pstmt.setString(3, rInfo.reviewer); 
			pstmt.setString(4, rInfo.location);
			pstmt.setString(5, rInfo.content);
			pstmt.setString(6, rInfo.time);
			status = pstmt.executeUpdate();
			conn.commit();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Cannot insert info into table \"xiamireview\". ");
		}
		return status;
	}
	public ArrayList<Map<String,String>> getResult(String cols,String tab){
		String queryString = "select " + cols+ " from " + tab + " where id not in (select id from xxreview)";
		System.out.println(queryString);
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
