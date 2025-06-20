package com.mech.app.configfiles.database;

import java.sql.*;

public class AppConnection extends AppProperties {

    private Connection connection;
    protected PreparedStatement prepare;
    protected ResultSet resultSet;
    private static final String URL = itemProp().getProperty("spring.datasource.url");
    private static final String USERNAME = itemProp().getProperty("spring.datasource.username");
    private static final String PASSWORD = itemProp().getProperty("spring.datasource.password");

    protected Connection getCon(){
        try {

            //remote connection variables.
//            String URL = "jdbc:mysql://ucmasdb-mcbondprince-804a.b.aivencloud.com:19419/swedru_ucmas?ssl-mode=REQUIRED";
//            String USERNAME = "avnadmin";
//            String PASSWORD = "AVNS_g8aV9VspVcI9z_IhfM9";

             connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // connection.setAutoCommit(false);
        } catch (SQLException e) {
           e.printStackTrace();
        }
        return connection;
    }
}//end of class...
