package vaadin;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import dao.ChannelsDAO;
import dao.CityDAO;
import dao.CustomerDAO;
import models.Channel;
import models.City;
import models.Customer;
import models.Gender;

import java.util.*;

/**
 * Created by mounzer.masri on 11.9.2016.
 */
public class CustomerForm extends FormLayout {
    TextField name = new TextField("Name :");
    TextField surname = new TextField("Surname :");
    ComboBox gender = new ComboBox("Gender :");
    DateField birthDay = new DateField("Birth day :");
    ComboBox city = new ComboBox("Birth City:");
    CheckBox isActive = new CheckBox("Active :");
    TwinColSelect channels = new TwinColSelect("Select Targets");
    Button btnSave = new Button("Save");
    Button btnCancel = new Button("Cancel");
    Button btnUpdate = new Button("Update");
    Button btnDelete = new Button("Delete");

    private String formMood = "INSERT";
    private Customer customer;

    public CustomerForm(String formMood) {
        this.formMood = formMood;
        name.setIcon(FontAwesome.USER);
        name.setRequired(true);
        name.addValidator(new NullValidator("Must be given", false));

        surname.setIcon(FontAwesome.USER);
        name.addValidator(new NullValidator("Must be given", false));
        surname.setRequired(true);

        gender.setNullSelectionAllowed(false);
        gender.addItems(0, 1);
        gender.setItemCaption(0, Gender.Male.name());
        gender.setItemCaption(1, Gender.Female.name());
        gender.setIcon(FontAwesome.USER);
        gender.setRequired(true);

        birthDay.setIcon(FontAwesome.USER);
        birthDay.addValidator(new NullValidator("Must be given", false));
        birthDay.setRequired(true);
        try {
            List<City> cities = CityDAO.getListCities();
            for (City myCityObj : cities) {
                city.addItem(myCityObj.getId());
                city.setItemCaption(myCityObj.getId(), myCityObj.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        city.setIcon(FontAwesome.USER);
        city.setNullSelectionAllowed(false);
        city.setRequired(true);

        isActive.setIcon(FontAwesome.USER);
        isActive.setRequired(true);

        try {
            List<Channel> channelsLIst = ChannelsDAO.getChannelsLIst();
            for (Channel myChannelObj : channelsLIst) {
                channels.addItem(myChannelObj.getId());
                channels.setItemCaption(myChannelObj.getId(), myChannelObj.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        channels.setIcon(FontAwesome.USER);
        channels.setRequired(true);

        btnUpdate.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Customer updatedCustomer = fillObject();
                CustomerDAO.updateCustomer(updatedCustomer);
                Notification.show(updatedCustomer.getName() + "updated");
            }
        });

        btnDelete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Customer deletedCustomer = fillObject();
                CustomerDAO.deleteCustomer(deletedCustomer);
                Notification.show(deletedCustomer.getName() + "deleted");
            }
        });
        btnSave.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                CustomerDAO.insertCustomer(fillObject());
                Notification.show(customer.getName());
            }
        });

        btnCancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                name.clear();
                surname.clear();
                gender.clear();
                birthDay.clear();
                city.clear();
                isActive.clear();
                channels.clear();
            }

        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        if (this.formMood.equals("INSERT")) {
            horizontalLayout.addComponent(btnSave);
            horizontalLayout.addComponent(btnCancel);
        } else {
            horizontalLayout.addComponent(btnUpdate);
            horizontalLayout.addComponent(btnDelete);
        }

        addComponents(name, surname, gender, birthDay, city, isActive, channels, horizontalLayout);
    }

    public Customer fillObject() {
        try {
            customer.setName(name.getValue());
            customer.setSurname(surname.getValue());
            customer.setGender((Integer) gender.getValue());
            customer.setBirthDay(birthDay.getValue());
            customer.setCity(CityDAO.getCityById((Integer) city.getValue()));
            customer.setIsActive(isActive.getValue());

            customer.setChannels(new ArrayList<Channel>());
            Collection<Integer> selectedChannels = (Collection<Integer>) channels.getValue();
            for(Integer chnlId : selectedChannels){
               customer.getChannels().add(ChannelsDAO.getChannelById(chnlId));
            }

            return customer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        name.setValue(customer.getName());
        surname.setValue(customer.getName());
        surname.setValue(customer.getSurname());
        gender.setValue(customer.getGender());
        birthDay.setValue(customer.getBirthDay());
        city.setValue(customer.getCity().getId());
        isActive.setValue(customer.isActive());

        List<Integer> integerList =  new ArrayList<Integer>();
        for (Channel channel : customer.getChannels()){
            integerList.add(channel.getId());
        }

        Collection<Integer> selectedChannels = integerList;
        channels.setValue(selectedChannels);
    }
}
