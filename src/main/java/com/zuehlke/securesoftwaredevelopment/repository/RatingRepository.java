package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.domain.Comment;
import com.zuehlke.securesoftwaredevelopment.domain.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RatingRepository {

    private static final Logger LOG = LoggerFactory.getLogger(RatingRepository.class);


    private DataSource dataSource;

    public RatingRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createOrUpdate(Rating rating) {
        String query = "SELECT bookId, userId, rating FROM ratings WHERE bookId = ? AND userID = ?";
        String query2 = "update ratings SET rating = ? WHERE bookId = ? AND userId = ?";
        String query3 = "insert into ratings(bookId, userId, rating) values (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(query);
        ) {
            checkStatement.setInt(1, rating.getBookId());
            checkStatement.setInt(2, rating.getUserId());
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next()) {
                PreparedStatement preparedStatement = connection.prepareStatement(query2);
                preparedStatement.setInt(1, rating.getRating());
                preparedStatement.setInt(2, rating.getBookId());
                preparedStatement.setInt(3, rating.getUserId());
                preparedStatement.executeUpdate();
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement(query3);
                preparedStatement.setInt(1, rating.getBookId());
                preparedStatement.setInt(2, rating.getUserId());
                preparedStatement.setInt(3, rating.getRating());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Rating> getAll(String bookId) {
        List<Rating> ratingList = new ArrayList<>();
        String query = "SELECT bookId, userId, rating FROM ratings WHERE bookId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, bookId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Rating rating = new Rating(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                ratingList.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratingList;
    }
}
