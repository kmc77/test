package net.comment.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.comment.action.Comment;

public class CommentDAO {
	
	private DataSource ds;
	
	public CommentDAO() {
		
		try {
			Context init = new InitialContext();
			ds = (DataSource) init.lookup("java:comp/env/jdbc/OracleDB");
		} catch (Exception ex) {
			System.out.println("DB 연결 실패 : " + ex);
			return;
		}
	}
	   

	public int getListCount(int comment_board_num) {
		int x = 0;
		String sql = "select count(*) "
				   + " from comm "
				   + " where comment_board_num = ?";
		
		try (Connection con = ds.getConnection();
				 PreparedStatement pstmt = con.prepareStatement(sql);){
			
			pstmt.setInt(1, comment_board_num);
			try(ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					x = rs.getInt(1);
				}
			}
		} catch (SQLException ex) { // SQL 관련 예외 처리
		    ex.printStackTrace();
		    System.out.println("getListCount() 에러 : " + ex);
		}

		return x;
	}//getListCount() end
	

	public JsonArray getCommentList(int comment_board_num, int state) {
	    String sort = (state == 2) ? "desc" : "asc"; // 최신순일 때 정렬을 내림차순으로 설정

	    String sql = "select num, comm.id, content, reg_date, comment_re_lev, "
	            + "          comment_re_seq, "
	            + "          comment_re_ref, member.memberfile "
	            + "   from   comm join member "
	            + "   on     comm.id=member.id "
	            + "   where  comment_board_num = ? "
	            + "   order  by comment_re_ref, "
	            + "            comment_re_seq " + sort;


	        JsonArray array = new JsonArray();
	        try (Connection con = ds.getConnection();
	             PreparedStatement pstmt = con.prepareStatement(sql);){
	            
	            pstmt.setInt(1, comment_board_num);
	            try(ResultSet rs = pstmt.executeQuery()) {
	                
	                while (rs.next()) {
	                    JsonObject object = new JsonObject();
	                    object.addProperty("num", rs.getInt(1));
	                    object.addProperty("id", rs.getString(2));
	                    object.addProperty("content", rs.getString(3));
	                    object.addProperty("reg_date", rs.getString(4));
	                    object.addProperty("comment_re_lev", rs.getInt(5));
	                    object.addProperty("comment_re_seq", rs.getInt(6));
	                    object.addProperty("comment_re_ref", rs.getInt(7));
	                    object.addProperty("memberfile", rs.getString(8));

	                    array.add(object);
	                }
	            }
	        } catch (Exception ex) {
	            System.out.println("getCommentList() 에러 : " + ex);
	        }
	        return array; 
	    }//getCommentList() 메서드 end


	public int commentsInsert(Comment c) {
		 int result = 0;
		    String sql = "INSERT INTO comm "
		                    + " VALUES (comm_seq.NEXTVAL, ?, ?, sysdate, ?, 0, 0, com_seq.nextval)";

		    try (Connection con = ds.getConnection();
		         PreparedStatement pstmt = con.prepareStatement(sql);) {
		        
		    	//새로운 글을 등록하는 부분입니다.
		        pstmt.setString(1, c.getId());
		        pstmt.setString(2, c.getContent());
		        pstmt.setInt(3, c.getComment_board_num());
		        result = pstmt.executeUpdate();
		        if (result == 1) 
		        	System.out.println("데이터 삽입 완료되었습니다.");
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return result;
		}// commentsInsert()메서드 end
	
	public int commentsUpdate(Comment co) {
		int result = 0;
		String sql = "update comm "
				   + "set    content=? "
				   + "where  num=? ";
		
		 try (Connection con = ds.getConnection();
		         PreparedStatement pstmt = con.prepareStatement(sql);) {
		        pstmt.setString(1, co.getContent());
		        pstmt.setInt(2, co.getNum());
		        
		        result = pstmt.executeUpdate();
		        if (result == 1) 
		        	System.out.println();
		 	} catch (Exception e) {
		 		e.printStackTrace();
		 	}
		 return result;
	} //commentsUpdate() 메서드
}
