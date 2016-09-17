package dao;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;

/**
 * Created by mounzer.masri on 10.9.2016.
 */
public class DataSourceFactory {
    public static DataSource getMysqlDataSource() {
        MysqlDataSource mysqlDS = null;
        mysqlDS = new MysqlDataSource();
      mysqlDS.setURL("jdbc:mysql://localhost:3306/test");
//        mysqlDS.setURL("jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        mysqlDS.setUser("root");
        mysqlDS.setPassword("12345");
        return mysqlDS;
    }
}
