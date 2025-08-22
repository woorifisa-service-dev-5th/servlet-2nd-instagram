package Dao;

import java.sql.*;

public class LikeDAO {

    // 좋아요 토글 (있으면 삭제, 없으면 추가)
    public void toggleCommentLike(int memberId, int postId, int commentId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            // 트랜잭션 시작
            conn.setAutoCommit(false);

            String checkSql = "SELECT COUNT(*) FROM likes WHERE member_id = ? AND post_id = ? AND comment_id = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, memberId);
                psCheck.setInt(2, postId);
                psCheck.setInt(3, commentId);
                ResultSet rs = psCheck.executeQuery();

                boolean exists = false;
                if (rs.next()) exists = rs.getInt(1) > 0;

                if (exists) {
                    // 좋아요 삭제
                    String deleteSql = "DELETE FROM likes WHERE member_id = ? AND post_id = ? AND comment_id = ?";
                    try (PreparedStatement psDel = conn.prepareStatement(deleteSql)) {
                        psDel.setInt(1, memberId);
                        psDel.setInt(2, postId);
                        psDel.setInt(3, commentId);
                        psDel.executeUpdate();
                    }

                    // 댓글 좋아요 수 감소
                    String updateSql = "UPDATE comment SET like_count = like_count - 1 WHERE comment_id = ?";
                    try (PreparedStatement psUpd = conn.prepareStatement(updateSql)) {
                        psUpd.setInt(1, commentId);
                        psUpd.executeUpdate();
                    }
                } else {
                    // 좋아요 추가
                    String insertSql = "INSERT INTO likes (member_id, post_id, comment_id) VALUES (?, ?, ?)";
                    try (PreparedStatement psIns = conn.prepareStatement(insertSql)) {
                        psIns.setInt(1, memberId);
                        psIns.setInt(2, postId);
                        psIns.setInt(3, commentId);
                        psIns.executeUpdate();
                    }

                    // 댓글 좋아요 수 증가
                    String updateSql = "UPDATE comment SET like_count = like_count + 1 WHERE comment_id = ?";
                    try (PreparedStatement psUpd = conn.prepareStatement(updateSql)) {
                        psUpd.setInt(1, commentId);
                        psUpd.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // 댓글 좋아요 수 조회
    public int getCommentLikeCount(int commentId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT like_count FROM comment WHERE comment_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, commentId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getInt("like_count");
                }
                return 0;
            }
        }
    }
}
