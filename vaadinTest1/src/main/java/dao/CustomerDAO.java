package dao;

import models.Channel;
import models.City;
import models.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mounzer.masri on 10.9.2016.
 */
class CustomerMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet resultSet, int i) throws SQLException {
        Customer customer = new Customer();
        customer.setId(resultSet.getInt("id"));
        customer.setName(resultSet.getString("name"));
        customer.setSurname(resultSet.getString("surname"));
        customer.setGender(resultSet.getInt("gender"));
        customer.setBirthDay(resultSet.getDate("birthday"));
        customer.setCity(new City(resultSet.getInt("city"), ""));
        customer.setIsActive(resultSet.getBoolean("is_active"));
        return customer;
    }
}

public class CustomerDAO {
    public static Customer getListCustomerById(Integer id) throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        List<Customer> customerList =  jdbcTemplate.query("select * from customers where id = ?", new Object[]{id} , new CustomerMapper());
        processCustomerFileds(customerList);
        return customerList.get(0);
    }

    public static List<Customer> getListCustomers(String name) throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        List<Customer> customerList =  jdbcTemplate.query("select * from customers where name like ?", new Object[]{"%" + name + "%"} , new CustomerMapper());
        processCustomerFileds(customerList);

        return customerList;
    }

    public static List<Customer> getListCustomers() throws  SQLException{
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        List<Customer> customerList =  jdbcTemplate.query("select * from customers" , new CustomerMapper());
        processCustomerFileds(customerList);
      return customerList;
    }

    private static void processCustomerFileds(List<Customer> customerList){
        try {
            for (Customer customer : customerList) {
                customer.setChannels(ChannelsDAO.getChannelsByCustomer(customer.getId()));
                CityDAO.fillCityObj(customer.getCity());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void insertCustomer(Customer customer) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        SimpleJdbcInsert customerInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("customers").usingGeneratedKeyColumns("id");

        final Map<String, Object> customerParameters = new HashMap<>();
        customerParameters.put("name", customer.getName());
        customerParameters.put("surname", customer.getSurname());
        customerParameters.put("gender",customer.getGender());
        customerParameters.put("birthday",customer.getBirthDay());
        customerParameters.put("city",customer.getCity());
        customerParameters.put("is_active", customer.isActive());
        final Number key = customerInsert.executeAndReturnKey(customerParameters);
        final long pk = key.longValue();
        System.out.println(pk);
        customer.setId(key.intValue());

        SimpleJdbcInsert channelsInserter = new SimpleJdbcInsert(jdbcTemplate).withTableName("customer_channels").usingGeneratedKeyColumns("id");
        for (Channel channel : customer.getChannels()) {
            Map<String, Object> channelParameters = new HashMap<>();
            channelParameters.put("customer", customer.getId());
            channelParameters.put("channel", channel.getId());
            channelsInserter.execute(channelParameters);
        }
    }

    public static void updateCustomer(Customer customer) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        jdbcTemplate.update("update customers set name = ?, surname = ?, gender = ? , birthday = ?, city = ?, is_active = ?  where id = ?",customer.getName(), customer.getSurname(), customer.getGender(), customer.getBirthDay(), customer.getCity().getId(), customer.isActive(), customer.getId());

        ChannelsDAO.deleteCustomerChannles(customer.getId());
        ChannelsDAO.insertChannels(customer.getChannels(), customer.getId());
    }

    public static void deleteCustomer(Customer customer) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSourceFactory.getMysqlDataSource(), false);
        jdbcTemplate.update("delete from customers where id = ?", customer.getId());
    }
}
