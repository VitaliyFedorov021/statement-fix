package ua.alevel.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.alevel.dao.CategoryDao;
import ua.alevel.datasource.DataSource;
import ua.alevel.dto.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class CategoryDaoImpl implements CategoryDao {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryDaoImpl.class);
    private static final String SQL = "INSERT INTO category (name) VALUES(?)";
    @Autowired
    private DataSource dataSource;

    @Override
    public boolean insertListOfCategories(List<Category> categoryList) {
        PreparedStatement ps = null;
        try {
            ps = dataSource.createStatement(SQL);
            for (Category category : categoryList) {
                ps.clearParameters();
                ps.setString(1, category.getName());
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException e) {
            LOG.error("Insert error: " + e);
        } finally {
            try {
                Connection connection = ps.getConnection();
                ps.close();
                connection.close();
            } catch (SQLException throwables) {
                LOG.error("Connection error " + throwables);
            }

        }
        return false;
    }
}
