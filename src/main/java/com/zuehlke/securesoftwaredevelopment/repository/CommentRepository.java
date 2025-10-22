package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.config.Entity;
import com.zuehlke.securesoftwaredevelopment.domain.Comment;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepository {
    private static final Logger LOG = LoggerFactory.getLogger(CommentRepository.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(CommentRepository.class);

    private DataSource dataSource;

    public CommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(Comment comment) {
        String query = "insert into comments(bookId, userId, comment) values (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setInt(1, comment.getBookId());
            statement.setInt(2, comment.getUserId());
            statement.setString(3, comment.getComment());
            statement.executeUpdate();
            auditLogger.auditChange(new Entity(
                "comment.add",
                "user: " + comment.getUserId() + ", book: " + comment.getBookId(),
                "---",
                comment.getComment()));
        LOG.info("Comment added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error("Error adding comment", e);
        }
    }

    public List<Comment> getAll(String bookId) {
        List<Comment> commentList = new ArrayList<>();
        String query = "SELECT bookId, userId, comment FROM comments WHERE bookId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    commentList.add(new Comment(rs.getInt(1), rs.getInt(2), rs.getString(3)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentList;
    }
}
