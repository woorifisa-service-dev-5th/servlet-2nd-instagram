package Dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/dbtest1")
public class DBUitl extends HttpServlet {
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "1234";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/";
	private static final String DATABASE_SCHEMA = "instagramdb";

	@Override
	protected void doGet(HttpServletRequest requset, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			// 1. MySQL 드라이버 로드
			Class.forName("com.mysql.cj.jdbc.Driver");

			// 2. DB 연결 시도
			Connection conn = DriverManager.getConnection(DB_URL + DATABASE_SCHEMA, USER_NAME, PASSWORD);

			if (conn != null) {
				out.println("<h2>✅ DB 연결 성공!</h2>");
			} else {
				out.println("<h2>❌ DB 연결 실패</h2>");
			}

			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
			out.println("<h2>❌ 오류 발생: " + e.getMessage() + "</h2>");
		}
	}

}
