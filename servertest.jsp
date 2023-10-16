<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<%@ page import = "java.sql.*" %>
<% request.setCharacterEncoding("UTF-8"); %>
<%
	
	String flag="";
	String id="";
	String pw="";
	String user="";
	String ISBN="";
	String title="";
	String author="";
	String company="";
	String finished="";
	String spinner="";
	
	Class.forName("com.mysql.jdbc.Driver");
	
	String url = "jdbc:mysql://localhost:3306/dbBook?serverTimezone=UTC";
	Connection conn = DriverManager.getConnection(url,"Member","apple");
	
	Statement stmt = conn.createStatement();
	
	try{
		flag = request.getParameter("flag");
		
		// -------------------------------------------------------------------------------------------- 중복 확인
		
		if(flag.equals("duplicate")){
			id = request.getParameter("id");
			ResultSet rs = stmt.executeQuery("select id from user");
			Boolean dup = false;
			while(rs.next()){
				
				String tmp = rs.getString(1);
				
				if(id.equals(tmp)){
					out.print("중복된 ID입니다.");
					dup = true;
				}
			}
			if(!dup){
				out.print("okay");
			}
			rs.close();
			conn.close();
		}
		
		// -------------------------------------------------------------------------------------------- 회원 가입
		
		else if(flag.equals("sign")){
			id = request.getParameter("id");
			pw = request.getParameter("pw");
			
			System.out.println(id);
			
			String strSQL ="INSERT INTO user(id, pw)";
			strSQL = strSQL + "VALUES (?, ?)";
			PreparedStatement pstmt = null;
			pstmt = conn.prepareStatement(strSQL);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			pstmt.executeUpdate();
			
			out.print("회원가입 되었습니다");
			
			pstmt.close();
			conn.close();
		}
		
		// -------------------------------------------------------------------------------------------- 로그인
		
		else if(flag.equals("login")){
			id = request.getParameter("id");
			pw = request.getParameter("pw");
			
			ResultSet rsid = stmt.executeQuery("select id from user");
			
			Boolean bid = false;
			Boolean bpw = false;
			while(rsid.next()){
				String tmpid = rsid.getString(1);
		
				if(id.equals(tmpid)){
					bid = true;
					
					ResultSet rspw = stmt.executeQuery("select pw from user");
					while(rspw.next()){
						String tmppw = rspw.getString(1);
						if(pw.equals(tmppw)){
							bpw = true; break;
						}
						else{
							bpw = false;
						}
					}
					rspw.close();
					break;
				}
				else{
					bid = false;
				}
			}
			
			if(bid){
				if(bpw){
					out.print("login");
				}
				else{
					out.print("pw");
				}
			}
			else{
				out.print("id");
			}
			
			rsid.close();
			conn.close();
		}
		
		// -------------------------------------------------------------------------------------------- 도서 등록
		
		else if(flag.equals("book")){
			user = request.getParameter("user");
			ISBN = request.getParameter("ISBN");
			title = request.getParameter("title");
			author = request.getParameter("author");
			company = request.getParameter("company");
			finished = request.getParameter("finished");
			spinner = request.getParameter("spinner");

			
			int count = 0;
			ResultSet rs = stmt.executeQuery("select max(num) from book");
			if(rs.next()){
				count = rs.getInt(1);	
				count++;
			}
			
			PreparedStatement pstmt = null;
			Boolean finishedbool = false;
			
			int spinnernum = 0;
			
			if(finished.equals("true")){
				finishedbool = true;
				spinnernum = Integer.parseInt(spinner);
			}
			
			String strSQL ="INSERT INTO book(num, name, isbn, title, author, company, finished, point)";
			strSQL = strSQL + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(strSQL);
			pstmt.setInt(1, count);
			pstmt.setString(2, user);
			pstmt.setString(3, ISBN);
			pstmt.setString(4, title);
			pstmt.setString(5, author);
			pstmt.setString(6, company);
			pstmt.setBoolean(7, finishedbool);
			pstmt.setInt(8, spinnernum);
			pstmt.executeUpdate();
			
			rs.close();
			pstmt.close();
			conn.close();
		}
		
		else if(flag.equals("browse")){
			id = request.getParameter("user");
			
			ResultSet rs = stmt.executeQuery("select name, title, author, company, point from book");
			Boolean dup = false;
			String book = "";
			while(rs.next()){
				
				String tmpname = rs.getString(1);
				if(tmpname.equals(id)){
					String tmp = rs.getString(2)+"--"+rs.getString(3)+"--"+rs.getString(4)+"--"+rs.getString(5)+ "==";
					book = book + tmp;
				}
			}
			
			rs.close();
			conn.close();
			
			out.print(book);
		}
		else if(flag.equals("delete")){
			title = request.getParameter("title");
			id = request.getParameter("user");
			
			out.print(title + id);
			
			String strSQL ="delete from book where name = ? and title=?";
			PreparedStatement pstmt = null;
			pstmt = conn.prepareStatement(strSQL);
			pstmt.setString(1, id);
			pstmt.setString(2, title);
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			
			//out.print("삭제되었습니다");
		}
	}
	catch(Exception e){
		System.out.println("받은 값 없음");
	}
	
	
%>    