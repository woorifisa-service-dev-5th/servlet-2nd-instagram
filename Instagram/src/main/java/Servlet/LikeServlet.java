package Servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Dao.LikeDAO;

@WebServlet("/like")
public class LikeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int memberId = 1; // 테스트용 (실제는 세션에서 가져와야 함)
        int commentId = Integer.parseInt(request.getParameter("commentId"));

        LikeDAO dao = new LikeDAO();
        try {
			dao.toggleCommentLike(memberId, commentId);
		} catch (SQLException e) {
			e.printStackTrace();
		} // 토글 처리

        int updatedLikeCount = 0;
		try {
			updatedLikeCount = dao.getCommentLikeCount(commentId);
		} catch (SQLException e) {
			e.printStackTrace();
		} // 새 count 가져옴

        // JSON 응답
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"likeCount\":" + updatedLikeCount + "}");
    }
}
