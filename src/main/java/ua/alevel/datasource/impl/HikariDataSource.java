package ua.alevel.datasource.impl;

import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.alevel.datasource.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class HikariDataSource implements DataSource {
    private static final Logger LOG = LoggerFactory.getLogger(HikariDataSource.class);
    private final HikariConfig config;
    private final com.zaxxer.hikari.HikariDataSource dataSource;

    public HikariDataSource(@Value("${db.url}") String url,
                                @Value("${db.user}") String userName,
                                @Value("${db.password}") String password) {
        config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(userName);
        config.setPassword(password);

        Properties properties = new Properties();

        try (InputStream propertiesStream = HikariDataSource.class.getClassLoader().getResourceAsStream("hikari.properties")) {

            properties.load(propertiesStream);

            config.setDataSourceProperties(properties);
        } catch (IOException e) {
            LOG.error("Properties error:", e);
        }

        dataSource = new com.zaxxer.hikari.HikariDataSource(config);
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public PreparedStatement createStatement (String SQL) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL);
        return statement;
    }
}
