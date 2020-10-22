package ua.alevel.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DataSource {
    PreparedStatement createStatement (String SQL) throws SQLException;
}
