// /Instagram/src/main/java/Dao/FollowDAO.java
package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
* DB 접근 클래스
* (팔로우 추가/삭제/조회)
*/

public class FollowDAO {

    // 팔로우하기
    public boolean follow(int senderId, int receiverId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            /*
    		 * DB 연동
    		 * PreparedStatement : SQL문을 미리 컴파일 시킴
    		 * 일반 Statement는 매번 할 때마다 처음부터 설명해주세요 이지만
    		 * PreparedStatement는 중간까지는 아니까 그 이후 부터 해주세요 => 속도 향상, 보안  good, 같은 종류의 작업을 여러번 할때 효율적
    		 */
            
            // 1. follow 테이블에 추가
            String sql = "INSERT INTO follow (to_user, from_user, status) VALUES (?, ?, 'active')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, receiverId); // 첫 번째 ?에 receiverID 받기
            pstmt.setInt(2, senderId);   // 두 번째 ?에 senderID 받기
            pstmt.executeUpdate();
            pstmt.close();
            
            // 2. sender의 followings 증가
            sql = "UPDATE member SET followings = followings + 1 WHERE member_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, senderId);
            pstmt.executeUpdate();
            pstmt.close();
            
            // 3. receiver의 followers 증가
            sql = "UPDATE member SET followers = followers + 1 WHERE member_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, receiverId);
            pstmt.executeUpdate();
            
            conn.commit(); // 모든 작업 성공 시 커밋
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // 실패 시 롤백
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // 자동 커밋 모드로 복원
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 언팔하기
    public boolean unfollow(int senderId, int receiverId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            // 1. follow 테이블에서 삭제
            String sql = "DELETE FROM follow WHERE to_user = ? AND from_user = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, receiverId);
            pstmt.setInt(2, senderId);
            int result = pstmt.executeUpdate();
            pstmt.close();
            
            if (result > 0) {
                // 2. sender의 followings 감소 (0 이하로 가지 않도록)
                sql = "UPDATE member SET followings = GREATEST(followings - 1, 0) WHERE member_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, senderId);
                pstmt.executeUpdate();
                pstmt.close();
                
                // 3. receiver의 followers 감소 (0 이하로 가지 않도록)
                sql = "UPDATE member SET followers = GREATEST(followers - 1, 0) WHERE member_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, receiverId);
                pstmt.executeUpdate();
                
                conn.commit(); // 모든 작업 성공 시 커밋
                return true;
            }
            
            conn.rollback();
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // 실패 시 롤백
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // 자동 커밋 모드로 복원
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 팔로우 상태 확인
    public boolean isFollowing(int senderId, int receiverId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE to_user = ? AND from_user = ? AND status = 'active'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, receiverId);
            pstmt.setInt(2, senderId);
            
            /*
            * 테이블을 가져와서
            * 1 이상 가지고 있으면 true를 주면서 이미 팔로우 되어 있는 상태 줌
            * 0이면 false를 주면서 팔로우 안 된 상태 줌
            */
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 팔로워 수 조회 (변경 없음)
    public int getFollowersCount(int memberId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE to_user = ? AND status = 'active'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // 첫번째 컬럼의 숫자 값을 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 팔로잉 수 조회 (변경 없음)
    public int getFollowingsCount(int memberId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE from_user = ? AND status = 'active'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}