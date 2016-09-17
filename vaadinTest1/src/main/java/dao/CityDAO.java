package dao;

import models.City;
import models.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mounzer.masri on 10.9.2016.
 */
class CityMapper implements RowMapper<City> {

    @Override
    public City mapRow(ResultSet resultSet, int i) throws SQLException {
        return new City(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
public class CityDAO {


    public static List<City> getListCities() throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        return  jdbcTemplate.query("select * from cities",new CityMapper());
    }

    public static City getCityById(int id) throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        List<City> cities =   jdbcTemplate.query("select * from cities where id = ?", new Object[]{id}, new CityMapper());
        return cities.get(0);
    }

    public static void  fillCityObj(City city){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        List<City> cities =   jdbcTemplate.query("select * from cities where id = ?", new Object[]{city.getId()}, new CityMapper());
        for (City myCity : cities){
            city.setName(myCity.getName());
            return;
        }
    }
}
