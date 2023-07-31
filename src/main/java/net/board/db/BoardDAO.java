package net.board.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
    private DataSource ds;

    public BoardDAO() {
        try {
            Context init = new InitialContext();
            ds = (DataSource) init.lookup("java:comp/env/jdbc/OracleDB");
        } catch (Exception ex) {
            System.out.println("DB 연결 실패 : " + ex);
        }
    }

    // 글의 갯수 구하기
    public int getListCount() {
        String sql = "select count(*) from board";
        int x = 0;
        try (Connection con = ds.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql);) {

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    x = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("getListCount() 에러: " + ex);
        }
        return x;
    }

    public List<BoardBean> getBoardList(int page, int limit) {

    	/*
    	 page : 페이지
    	 limit : 페이지 당 목록의 수
    	 board_re_ref desc, board_re_seq asc에 의해 정렬한 것을 
    	 조건절에 맞는 rnum의 범위 만큼 가져오는 쿼리문입니다.
    	 */
    	 String board_list_sql = "select * "
    			 			  + " from (select rownum rnum, j.* "
    						  + " from (select board.*, nvl(cnt, 0) as cnt "
    						  + " 		from board left outer join (select comment_board_num, count(*) cnt "
    						  + "									from comm"
    						  + "									group by comment_board_num)"
    						  + "		on board_num=comment_board_num"
    						  + "		order by BOARD_RE_REF desc,"
    						  + "	BOARD_RE_SEQ asc) j "
    						  + "	where rownum<= ? "
    						  + "	) "
    						  + " 	where rnum>=? and rnum<=?";	
    	 
    	 List<BoardBean> list = new ArrayList<BoardBean>();
    	 //한 페이지당 10개씩 목록인 경우 1페이지, 2페이지, 3페이지, 4페이지 ... 
    	 int startrow = (page - 1) * limit + 1; //읽기 시작할 row 번호(1 11 21 31 ...
    	 int endrow = startrow + limit - 1;     //읽을 마지막 row 번호(10 20 30 40 ...
    	 try (Connection con = ds.getConnection();
    		  PreparedStatement pstmt = con.prepareStatement(board_list_sql);) {
    		   pstmt.setInt(1, endrow);
    		   pstmt.setInt(2, startrow);
    		   pstmt.setInt(3, endrow);
    		
    		   try(ResultSet rs = pstmt.executeQuery()) {
    			   
    			   //DB에서 가져온 데이터를 BoardBean에 담습니다.
    			   while (rs.next()) {
    				   BoardBean board = new BoardBean();
    				   board.setBoard_num(rs.getInt("BOARD_NUM"));
    				   board.setBoard_name(rs.getString("BOARD_NAME"));
    				   board.setBoard_subject(rs.getString("BOARD_SUBJECT"));
    				   board.setBoard_content(rs.getString("BOARD_CONTENT"));
    				   board.setBoard_file(rs.getString("BOARD_FILE"));
    				   board.setBoard_re_ref(rs.getInt("BOARD_RE_REF"));
    				   board.setBoard_re_lev(rs.getInt("BOARD_RE_LEV"));
    				   board.setBoard_re_seq(rs.getInt("BOARD_RE_SEQ"));
    				   board.setBoard_readcount(rs.getInt("BOARD_READCOUNT"));
    				   board.setBoard_date(rs.getString("BOARD_DATE"));
    				   board.setCnt(rs.getInt("cnt"));
    				   list.add(board); //값을 담은 객체를 리스트에 저장합니다.
    			   }
    		   } catch (SQLException e) {
    			   e.printStackTrace();
    		   }
    	 } catch (Exception ex) {
    		 ex.printStackTrace();
    		 System.out.println("getBoardList() 에러 : " + ex);
    	 }
        return list;
    }

    public boolean boardInsert(BoardBean board) {
		int result=0;
		String max_sql = "(select nvl(max(board_num),0)+1 from board)";
		
		// 원문글의 BOARD_RE_REF 필드는 자신의 글번호 입니다.
		String sql = "insert into board " 
				+ "(BOARD_NUM,     BOARD_NAME,  BOARD_PASS,    BOARD_SUBJECT,"
				+ " BOARD_CONTENT, BOARD_FILE,  BOARD_RE_REF," 
				+ " BOARD_RE_LEV,  BOARD_RE_SEQ,BOARD_READCOUNT)"
				+ " values(" + max_sql + ",?,?,?," 
				+ "        ?,?," +   max_sql  + "," 
				+ "        ?,?,?)";
try (Connection con = ds.getConnection(); 
        	 PreparedStatement pstmt = con.prepareStatement(sql);) {

			// 새로운 글을 등록하는 부분입니다.
			pstmt.setString(1, board.getBoard_name());
			pstmt.setString(2, board.getBoard_pass());
			pstmt.setString(3, board.getBoard_subject());
			pstmt.setString(4, board.getBoard_content());
			pstmt.setString(5, board.getBoard_file());

			// 원문의 경우 BOARD_RE_LEV, BOARD_RE_SEQ 필드 값은 0 입니다.
			pstmt.setInt(6, 0); // BOARD_RE_LEV 필드
			pstmt.setInt(7, 0); // BOARD_RE_SEQ 필드
			pstmt.setInt(8, 0); // BOARD_READCOUNT 필드

			result = pstmt.executeUpdate();
			if (result == 1) {
				System.out.println("데이터 삽입이 모두 완료되었습니다.");
				return true;
			}
		} catch (Exception ex) {
			System.out.println("boardInsert() 에러: " + ex);
			ex.printStackTrace();
		} 
		return false;
	}// boardInsert()메서드 end
    

    public void setReadCountUpdate(int num) {
        String sql = "UPDATE board "
        		+ " SET BOARD_READCOUNT = BOARD_READCOUNT + 1 "
        		+ " WHERE BOARD_NUM = ?";
        try (Connection con = ds.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);) {
            
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
            
        } catch (Exception ex) {
            System.out.println("setReadCountUpdate() 에러 : " + ex);
            ex.printStackTrace();
        }
    } //setReadCountUpdate() 메서드 end


    public BoardBean getDetail(int num) {
    	BoardBean board = null;
        String sql = "SELECT * FROM board WHERE BOARD_NUM = ?";
        
        try (Connection con = ds.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);) {
            
            pstmt.setInt(1, num);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    board = new BoardBean();
                    board.setBoard_num(rs.getInt("BOARD_NUM"));
                    board.setBoard_name(rs.getString("BOARD_NAME"));
                    board.setBoard_pass(rs.getString("BOARD_PASS"));
                    board.setBoard_subject(rs.getString("BOARD_SUBJECT"));
                    board.setBoard_content(rs.getString("BOARD_CONTENT"));
                    board.setBoard_file(rs.getString("BOARD_FILE"));
                    board.setBoard_re_ref(rs.getInt("BOARD_RE_REF"));
                    board.setBoard_re_lev(rs.getInt("BOARD_RE_LEV"));
                    board.setBoard_re_seq(rs.getInt("BOARD_RE_SEQ"));
                    board.setBoard_readcount(rs.getInt("BOARD_READCOUNT"));
                    board.setBoard_date(rs.getString("BOARD_DATE"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        } catch (Exception ex) {
            System.out.println("getDetail() 에러 : " + ex);
            ex.printStackTrace();
        }

		return board;
	}//getDetail()메서드 end

    public boolean isBoardWriter(int num, String pass) {
        boolean result = false;
        String board_sql = "select BOARD_PASS from board where BOARD_NUM=?";
        try (Connection con = ds.getConnection();
             PreparedStatement pstmt = con.prepareStatement(board_sql);) {
            pstmt.setInt(1, num);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if (pass.equals(rs.getString("BOARD_PASS"))) {
                        result = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException ex) {
            System.out.println("isBoardWriter() 에러 : " + ex);
        }
        return result; // Return the correct result value
    }

	public boolean boardModify(BoardBean modifyboard) {
	        String board_sql = "update board "
	        				 + "set BOARD_SUBJECT=?, BOARD_CONTENT=?, BOARD_FILE=? "
	        				 + "where BOARD_NUM=?";
	        try (Connection con = ds.getConnection();
	                PreparedStatement pstmt = con.prepareStatement(board_sql);) {
	               
	               pstmt.setString(1, modifyboard.getBoard_subject());
	               pstmt.setString(2, modifyboard.getBoard_content());
	               pstmt.setString(3, modifyboard.getBoard_file());
	               pstmt.setInt(4, modifyboard.getBoard_num());

	               int result = pstmt.executeUpdate();
	               if (result == 1) {
	            	   System.out.println("성공 업데이트");
	                  return true;
					}
				} catch (Exception ex) {
					System.out.println("boardModify() 에러 : " + ex);
				}
				return false;
			}//boardModify()메서드 end

	public int boardReply(BoardBean board) {
	    int num = 0;
	    /*
	     답변을 달 원문 그룹 번호입니다.
	     답변을 달게 되면 답변 글은 이 번호와 같은 관련글 번호를 갖게 처리되면서 같은 그룹에 속하게 됩니다.
	     글 목록에서 보여줄때 하나의 그룹으로 묶여서 출력됩니다.
	     */
	    int re_ref = board.getBoard_re_ref();
	    /*
	     답글의 깊이를 의미합니다.
	     원문에 대한 답글이 출력될 때 한 번 들여쓰기 처리가 되고 답글에 대한 답글은 들여쓰기가 두 번 처리되게 합니다.
	     원문인 경우에는 이 값이 0이고 원문의 답글은 1, 답글의 답글은 2가 됩니다.
	     */
	    int re_lev = board.getBoard_re_lev();
	    
	    //같은 관련 글 중에서 해당 글이 출력되는 ㅅ순서입니다.
	    String max_sql = "(select nvl(max(board_num),0)+1 from board)";
	    
	    int re_seq = board.getBoard_re_seq();
	    try (Connection con = ds.getConnection();){
	    		
	    		//트랜잭션을 이용하기 위해서 setAutoCommit을 false로 설정합니다.
	    		con.setAutoCommit(false);
	    		
	    		try {
	    			reply_update(con, re_ref, re_seq);
	    			
	    			//등록할 답변 글의 BOARD_RE_LEV, BOARD_RE_SEQ 값을 원문 글보다 1씩 증가시킵니다.
	    			num = reply_insert(con, board, re_ref, re_lev+1, re_seq + 1);
	    			con.commit();
	    			con.setAutoCommit(true);
	    		}
	    		catch(Exception e) {
	    			
	    			if (con != null) {
	    				try {
	    					con.rollback();
	    				} catch (Exception ex) {
	    					ex.printStackTrace();
	    				}
	    			}
	    		}
	    		
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    	System.out.println("boardReply() 에러 : " + ex);
	    }
	    return num;
	}


			private void reply_update(Connection con, int re_ref, int re_seq) throws Exception {
				//BOARD_RE_REF, BOARD_RE_SEQ 값을 확인하여 원문 글에 답글이 달려있다면
				//달린 답글들의 BOARD_RE_SEQ값을 1씩 증가시킵니다.
				//현재 글을 이미 달린 답글보다 앞에 출력되게 하기 위해서 입니다.
				
				String sql = "update board "
							+ "set 	BOARD_RE_SEQ = BOARD_RE_SEQ + 1 "
							+ "where BOARD_RE_REF = ? "
							+ "and 	BOARD_RE_SEQ > ?";
				
				try(PreparedStatement pstmt = con.prepareStatement(sql);) {
					pstmt.setInt(1, re_ref);
					pstmt.setInt(2, re_seq);
					pstmt.executeUpdate();
					
				}

			}
			
			private int reply_insert(Connection con, BoardBean board,
							int re_ref, int re_lev, int re_seq)throws Exception {
				int num = 0;
				String board_max_sql = "(select max(board_num)+1 from board)";
				try(PreparedStatement pstmt = con.prepareStatement(board_max_sql);) {
					try(ResultSet rs = pstmt.executeQuery()) {
						if(rs.next()) {
							num = rs.getInt(1);
						}
					}
				}
				
				String sql = "insert into board "
						+ "(BOARD_NUM, BOARD_NAME, BOARD_PASS, BOARD_SUBJECT,"
						+ "BOARD_CONTENT, BOARD_FILE, BOARD_RE_REF,"
						+ "BOARD_RE_LEV, BOARD_RE_SEQ, BOARD_READCOUNT) "
						+ " values(? ,?,?, ?,"
						+ "    ?,?,?,"
						+ "    ?,?,?)";
				try(PreparedStatement pstmt = con.prepareStatement(sql);) {
					pstmt.setInt(1, num);
					pstmt.setString(2, board.getBoard_name());
					pstmt.setString(3, board.getBoard_pass());
					pstmt.setString(4, board.getBoard_subject());
					pstmt.setString(5, board.getBoard_content());
					pstmt.setString(6, ""); //답변에는 파일을 업로드하지 않습니다.
					pstmt.setInt(7, re_ref);   
					pstmt.setInt(8, re_lev);  
					pstmt.setInt(9, re_seq);  
					pstmt.setInt(10, 0); //BOARD_READCOUNT(조회수)는 0
					pstmt.executeUpdate();
					
					
				}
				return num;
			}

			public boolean boardDelecte(int num) {
				 String select_sql = "select BOARD_RE_REF, BOARD_RE_LEV, BOARD_RE_SEQ "
				            + "from board" // Corrected: Changed "form" to "from"
				            + " where BOARD_NUM=?";
				 String board_delete_sql = "delete from board"
				            + " where BOARD_RE_REF = ?"
				            + " and BOARD_RE_LEV >=?"
				            + " and BOARD_RE_SEQ >=?"
				            + " and BOARD_RE_SEQ <=("
				            + "     nvl((SELECT min(board_re_seq)-1"
				            + "           FROM BOARD" // Corrected: Removed unnecessary spaces
				            + "           WHERE BOARD_RE_REF=?"
				            + "           AND BOARD_RE_LEV>?"
				            + "           AND BOARD_RE_SEQ>?) , "
				            + "         (SELECT max(board_re_seq)"
				            + "          FROM BOARD" // Corrected: Removed unnecessary spaces
				            + "          WHERE BOARD_RE_REF=? ))"
				            + " )";
						
						
				boolean result_check = false;
				
				try(Connection con = ds.getConnection();
						PreparedStatement pstmt = con.prepareStatement(select_sql); ) {//1
					
						pstmt.setInt(1, num);
						try(ResultSet rs = pstmt.executeQuery();){ //2
							if(rs.next()) {
								try(PreparedStatement pstmt2 = con.prepareStatement(board_delete_sql)) {//3
									pstmt2.setInt(1, rs.getInt("BOARD_RE_REF"));
									pstmt2.setInt(2, rs.getInt("BOARD_RE_LEV"));
									pstmt2.setInt(3, rs.getInt("BOARD_RE_SEQ"));
									pstmt2.setInt(4, rs.getInt("BOARD_RE_REF"));
									pstmt2.setInt(5, rs.getInt("BOARD_RE_LEV"));
									pstmt2.setInt(6, rs.getInt("BOARD_RE_SEQ"));
									pstmt2.setInt(7, rs.getInt("BOARD_RE_REF"));
									int count = pstmt2.executeUpdate();
									if(count >=1)
										result_check =true; //삭제가 안된 경우에는 false를 반환합니다.
									
								}//try 3
							}//if(rs.next()) {
						}//try 2
						
				  } catch (SQLException ex) {
					  System.out.println("boardDelete() 에러: " + ex);
				        ex.printStackTrace();
				    }
				return result_check;
				    
				    // Assuming you will add the rest of the code here...
				    // return the appropriate result.
				}//boardDelete()메서드 end
}