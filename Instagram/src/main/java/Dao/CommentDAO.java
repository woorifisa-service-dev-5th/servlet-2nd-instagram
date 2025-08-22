package Dao;

import Model.Comment;
import java.sql.*;
import java.util.*;

public class CommentDAO {

    // 댓글 삽입
    public Comment insert(Comment comment) {
        String sql = "INSERT INTO comment (post_id, member_id, parent_id, content) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, comment.getPostId());
            ps.setInt(2, comment.getMemberId());

            if (comment.getParentId() != null) {
                ps.setInt(3, comment.getParentId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setString(4, comment.getContent());
//            ps.executeUpdate();
            int rows = ps.executeUpdate();
            System.out.println("INSERT 결과 : " + rows);

            // 생성된 commentId 가져오기
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                comment.setCommentId(rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    // 특정 게시글의 댓글 목록 가져오기
    public List<Comment> findByPostId(int postId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * \r\n"
        		+ "FROM comment \r\n"
        		+ "WHERE post_id = ? \r\n"
        		+ "ORDER BY (parent_id IS NULL) DESC, parent_id ASC, comment_id ASC;\r\n"
        		+ "";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comment c = new Comment();
                c.setCommentId(rs.getInt("comment_id"));
                c.setPostId(rs.getInt("post_id"));
                c.setMemberId(rs.getInt("member_id"));

                int parentId = rs.getInt("parent_id");
                if (!rs.wasNull()) c.setParentId(parentId);

                c.setContent(rs.getString("content"));
                c.setChildrenCount(0); // 기본값 (추후 count 쿼리 가능)

                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
