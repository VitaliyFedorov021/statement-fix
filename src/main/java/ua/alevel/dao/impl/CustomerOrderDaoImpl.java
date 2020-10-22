package ua.alevel.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.alevel.dao.CustomerOrderDao;
import ua.alevel.datasource.DataSource;
import ua.alevel.dto.CustomerOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CustomerOrderDaoImpl implements CustomerOrderDao {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerOrderDaoImpl.class);
    private static final String SQL_FOR_ALL = "SELECT * FROM customerOrder";
    private static final String SQL_FOR_AFTER_DATE = "SELECT * FROM customerOrder WHERE creationDate > ?";
    private static final String SQL_FOR_ID = "SELECT id FROM customerOrder";
    private static final String SQL_FOR_INSERT = "INSERT INTO customerOrder (customerName, employeeId, creationDate) VALUES(?, ?, ?)";
    private DataSource dataSource;

    @Autowired
    public CustomerOrderDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<CustomerOrder> selectAllCustomerOrders() {
        List<CustomerOrder> customerOrders = new ArrayList<>();
        PreparedStatement statement = null;
        try {
            statement = dataSource.createStatement(SQL_FOR_ALL);
            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                customerOrders.add(mapResultSetToCustomerOrder(resultSet));
            }
        } catch (SQLException e) {
            LOG.error("SQL error", e);
        }

        return customerOrders;
    }

    private CustomerOrder mapResultSetToCustomerOrder(ResultSet resultSet) {
        return null;
    }

    @Override
    public List<CustomerOrder> selectAllAfterDate(Date date) {
        List<CustomerOrder> result = new ArrayList<>();
        PreparedStatement ps = null;
        try {
            ps = dataSource.createStatement(SQL_FOR_AFTER_DATE);
            ps.setDate(1, new java.sql.Date(date.getTime()));

            final ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                result.add(mapResultSetToCustomerOrder(resultSet));

            }
        } catch (SQLException e) {
            LOG.error("SQL Error", e);
        } finally {
            try {
                Connection connection = ps.getConnection();
                ps.close();
                connection.close();
            } catch (SQLException e) {
                LOG.error("close error " + e);
            }
        }
        return result;
    }

    @Override
    public List<CustomerOrder> selectAllByEmployeeId(int employeeId) {
        return null;
    }

    @Override
    public List<CustomerOrder> selectAllByCustomerName(String customerName) {
        return null;
    }

    @Override
    public List<Integer> selectAllOrderIds() {
        List<Integer> result = new ArrayList<>();

        PreparedStatement statement = null;
        try {
            statement = dataSource.createStatement(SQL_FOR_ID);
            final ResultSet res = statement.executeQuery();
            while (res.next()) {
                result.add(res.getInt(1));
            }
        } catch (SQLException e) {
            LOG.error("SQL: ", e);
        } finally {
            try {
                Connection connection = statement.getConnection();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                LOG.error("close error " + e);
            }
        }

        return result;
    }

    @Override
    public boolean insertListOfCustomerOrders(List<CustomerOrder> customerOrders) {
        PreparedStatement ps = null;
        Connection connection = null;
        try {
            ps = dataSource.createStatement(SQL_FOR_INSERT);
            connection = ps.getConnection();
            connection.setAutoCommit(false);
            for (CustomerOrder customerOrder : customerOrders) {
                ps.clearParameters();
                ps.setString(1, customerOrder.getCustomerName());
                ps.setInt(2, customerOrder.getEmployeeId());
                ps.setDate(3, new java.sql.Date(customerOrder.getCreationDate().getTime()));
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
            LOG.error("Insert error: " + e);
        }
        finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                LOG.error("close error " + e);
            }
        }
        return false;
    }
}
