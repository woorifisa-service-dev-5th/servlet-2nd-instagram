package Servlet;

import Dao.CommentDAO;
import Model.Comment;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
	private CommentDAO commentDAO = new CommentDAO();
	private Gson gson = new Gson();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 댓글 작성
		int postId = Integer.parseInt(req.getParameter("postId"));
		int memberId = Integer.parseInt(req.getParameter("memberId"));
		String content = req.getParameter("content");

		Comment c = new Comment();
//        c.setPostId(postId);
//        c.setMemberId(memberId);
		c.setContent(content);
		c.setPostId(1); // 테스트용 글 번호 (임시)
		c.setMemberId(1); // 테스트용 사용자 (임시)

		Comment saved = commentDAO.insert(c);

		// JSON 응답
		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(gson.toJson(saved));
		out.flush();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 특정 게시글의 댓글 조회
		int postId = Integer.parseInt(req.getParameter("postId"));
		List<Comment> comments = commentDAO.findByPostId(postId);

		// JSON 응답
		resp.setContentType("application/json; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.print(gson.toJson(comments));
		out.flush();
	}
}
