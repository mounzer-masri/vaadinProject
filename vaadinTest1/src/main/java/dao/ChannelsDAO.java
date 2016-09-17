package dao;

import models.Channel;
import models.City;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by mounzer.masri on 12.9.2016.
 */
class ChannelMapper implements RowMapper<Channel> {

    @Override
    public Channel mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Channel(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
public class ChannelsDAO {
    public static List<Channel> getChannelsLIst() throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        return  jdbcTemplate.query("SELECT * FROM test.channels ",   new ChannelMapper());
    }

    public static List<Channel> getChannelsByCustomer(int customer) throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        return  jdbcTemplate.query("SELECT * FROM test.channels where id  in (select  channel from test.customer_channels where customer = ? )", new Object[]{customer}, new ChannelMapper());
    }

    public static Channel getChannelById(Integer id) throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        List<Channel> channels =  jdbcTemplate.query("SELECT * FROM test.channels where id  = ? ", new Object[]{id}, new ChannelMapper());
        if(channels != null && channels.size() == 1) {
            return channels.get(0);
        }else {
            return null;
        }
    }

    public static void deleteCustomerChannles(Integer customerId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        jdbcTemplate.update("delete from test.customer_channels  where customer = ?",customerId);
    }

    public static void insertChannels(List<Channel> channels, Integer customerId){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        SimpleJdbcInsert channelsInserter = new SimpleJdbcInsert(jdbcTemplate).withTableName("customer_channels").usingGeneratedKeyColumns("id");
        for (Channel channel : channels) {
            Map<String, Object> channelParameters = new HashMap<>();
            channelParameters.put("customer", customerId);
            channelParameters.put("channel", channel.getId());
            channelsInserter.execute(channelParameters);
        }
    }
}
