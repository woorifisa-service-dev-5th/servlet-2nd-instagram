package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeDAO {

    // toggle: 있으면 삭제, 없으면 추가
    public void toggleCommentLike(int memberId, int commentId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM comment_like WHERE member_id = ? AND comment_id = ?";
            PreparedStatement ps = conn.prepareStatement(checkSql);
            ps.setInt(1, memberId);
            ps.setInt(2, commentId);
            ResultSet rs = ps.executeQuery();

            boolean exists = false;
            if (rs.next()) exists = rs.getInt(1) > 0;
            rs.close();
            ps.close();

            if (exists) {
                removeCommentLike(memberId, commentId, conn);
            } else {
                addCommentLike(memberId, commentId, conn);
            }
        }
    }

    private void addCommentLike(int memberId, int commentId, Connection conn) throws SQLException {
        String insertSql = "INSERT INTO comment_like (member_id, comment_id) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(insertSql);
        ps.setInt(1, memberId);
        ps.setInt(2, commentId);
        ps.executeUpdate();
        ps.close();

        String updateSql = "UPDATE comment SET like_count = like_count + 1 WHERE comment_id = ?";
        ps = conn.prepareStatement(updateSql);
        ps.setInt(1, commentId);
        ps.executeUpdate();
        ps.close();
    }

    private void removeCommentLike(int memberId, int commentId, Connection conn) throws SQLException {
        String deleteSql = "DELETE FROM comment_like WHERE member_id = ? AND comment_id = ?";
        PreparedStatement ps = conn.prepareStatement(deleteSql);
        ps.setInt(1, memberId);
        ps.setInt(2, commentId);
        ps.executeUpdate();
        ps.close();

        String updateSql = "UPDATE comment SET like_count = like_count - 1 WHERE comment_id = ?";
        ps = conn.prepareStatement(updateSql);
        ps.setInt(1, commentId);
        ps.executeUpdate();
        ps.close();
    }

    public int getCommentLikeCount(int commentId) throws SQLException {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT like_count FROM comment WHERE comment_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, commentId);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            if (rs.next()) {
                count = rs.getInt("like_count");
            }
            rs.close();
            ps.close();
            return count;
        }
    }
}
