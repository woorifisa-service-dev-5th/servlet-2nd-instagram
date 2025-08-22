package Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Dao.FollowDAO;

@WebServlet("/follow")
public class FollowServlet extends HttpServlet {
    
    private FollowDAO followDAO = new FollowDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // 현재 사용자는 member_id가 1이라고 가정 (민영)
        int senderId = 1;
        
        // 팔로우할 대상 ID 가져오기
        String receiverIdStr = request.getParameter("receiver_id");
        String action = request.getParameter("action"); // "follow" or "unfollow"
        
        try {
            int receiverId = Integer.parseInt(receiverIdStr);
            
            // 자기 자신을 팔로우하는 것 방지
            if (senderId == receiverId) {
                out.write("{\"success\":false,\"message\":\"자기 자신은 팔로우 불가\"}");
                return;
            }
            
            boolean result = false;
            
            if ("follow".equals(action)) {
                // 이미 팔로우 중인지 확인
                if (followDAO.isFollowing(senderId, receiverId)) {
                    out.write("{\"success\":false,\"message\":\"이미 팔로우 중\"}");
                } else {
                    // 팔로우 추가
                    result = followDAO.follow(senderId, receiverId);
                    out.write("{\"success\":" + result + ",\"message\":\"팔로우 완료\"}");
                }
                
            } else if ("unfollow".equals(action)) {
                // 팔로우 중인지 확인
                if (!followDAO.isFollowing(senderId, receiverId)) {
                    out.write("{\"success\":false,\"message\":\"팔로우하지 않은 상태\"}");
                } else {
                    // 언팔로우
                    result = followDAO.unfollow(senderId, receiverId);
                    out.write("{\"success\":" + result + ",\"message\":\"언팔로우 완료\"}");
                }
            }
            
        } catch (Exception e) {
            out.write("{\"success\":false,\"message\":\"실패\"}");
        }
    }
    
    // 팔로우 상태 및 카운트 조회 (GET 요청)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        int senderId = 1; // 현재 사용자
        String receiverIdStr = request.getParameter("receiver_id");
        
        try {
            int receiverId = Integer.parseInt(receiverIdStr);
            
            // 팔로우 상태 확인
            boolean isFollowing = followDAO.isFollowing(senderId, receiverId);
            
            // 팔로워, 팔로잉 수 조회
            int followersCount = followDAO.getFollowersCount(receiverId);
            int followingsCount = followDAO.getFollowingsCount(receiverId);
            
            // JSON 응답
            // 비동기 처리
            out.write("{");
            out.write("\"isFollowing\":" + isFollowing + ",");
            out.write("\"followersCount\":" + followersCount + ",");
            out.write("\"followingsCount\":" + followingsCount);
            out.write("}");
            
        } catch (Exception e) {
            out.write("{\"error\":\"실패\"}");
        }
    }
}