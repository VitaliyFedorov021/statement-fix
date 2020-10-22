package ua.alevel.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.alevel.dao.CustomerDao;
import ua.alevel.datasource.DataSource;
import ua.alevel.dto.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class CustomerDaoImpl implements CustomerDao {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerDaoImpl.class);
    private static final String SQL = "INSERT INTO customer (login, pass, firstName, lastName) VALUES(?, ?, ?, ?)";
    @Autowired
    private DataSource dataSource;

    @Override
    public boolean insertListOfCustomers(List<Customer> customers) {
        PreparedStatement ps = null;
        try {
            ps = dataSource.createStatement(SQL);
            for (Customer customer : customers) {
                ps.clearParameters();
                ps.setString(1, customer.getLogin());
                ps.setString(2, customer.getPass());
                ps.setString(3, customer.getFirstName());
                ps.setString(4, customer.getLastName());
                ps.addBatch();
                ps.executeBatch();
                return true;
            }
        } catch (SQLException e) {
            LOG.error("Insert error: " + e);
        } finally {
            try {
                Connection connection = ps.getConnection();
                ps.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

}
