package Servlet;

import Dao.LikeDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/like")
public class LikeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int memberId = 1; // 테스트용, 실제는 세션에서 받아와야 함
        int commentId = Integer.parseInt(req.getParameter("commentId"));
        System.out.println(commentId);
        
        int postId = Integer.parseInt(req.getParameter("postId"));
        System.out.println(postId);
        
        LikeDAO likeDAO = new LikeDAO();
        int updatedLikeCount = 0;

        try {
            likeDAO.toggleCommentLike(memberId, postId, commentId);
            updatedLikeCount = likeDAO.getCommentLikeCount(commentId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // JSON 반환
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"likeCount\":" + updatedLikeCount + "}");
    }
}
