package cn.javaguide.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DbUtil {
    public static final String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    public static Connection getConnection(String url, String username, String password) throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName(MYSQL_JDBC_DRIVER);
            log.info("MySQL 数据库驱动加载成功");
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

}
