package vaadin;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.*;
import dao.ChannelsDAO;
import dao.CustomerDAO;
import models.Channel;
import models.Customer;
import models.Gender;
import org.vaadin.peter.contextmenu.ContextMenu;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mounzer.masri on 12.9.2016.
 */
public class CustomerListLayout extends CustomLayout {
    private VerticalLayout verticalLayout = new VerticalLayout();
    private Grid grdchannels = new Grid();
    private Button btnRefresh = new Button("Refresh");
    private TextField txtNameFilter = new TextField();
    private Integer lastSelectedCustomer = 0;
    private Table table = new Table();
    private CustomerForm customerForm = new CustomerForm("UPDATE");
    private VerticalLayout verticalLayoutCustomerForm  = new VerticalLayout(customerForm);
    private PopupView popupViewCustomerForm = new PopupView(null, verticalLayoutCustomerForm);

    public CustomerListLayout() {

        btnRefresh.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                txtNameFilter.clear();
                updateCustomerList();
            }
        });

        txtNameFilter.setInputPrompt("Filter by name ");
        txtNameFilter.addValueChangeListener(e -> {
            updateCustomerList(txtNameFilter.getValue());
        });
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.addComponents(txtNameFilter, btnRefresh);

        Label lblChannels = new Label("Channels");

        ContextMenu contextMenu = new ContextMenu();
        ContextMenu.ContextMenuItem contextMenuItemEdit = contextMenu.addItem("Edit");
        ContextMenu.ContextMenuItem contextMenuItemDelete = contextMenu.addItem("Delete");

        contextMenuItemEdit.addItemClickListener(new ContextMenu.ContextMenuItemClickListener() {
            @Override
            public void contextMenuItemClicked(ContextMenu.ContextMenuItemClickEvent contextMenuItemClickEvent) {
                try {
                    Customer selectedCustomerObj = CustomerDAO.getListCustomerById(lastSelectedCustomer);
                    customerForm.setCustomer(selectedCustomerObj);
                    popupViewCustomerForm.setPopupVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        contextMenuItemDelete.addItemClickListener(new ContextMenu.ContextMenuItemClickListener() {
            @Override
            public void contextMenuItemClicked(ContextMenu.ContextMenuItemClickEvent contextMenuItemClickEvent) {
                try {
                    Customer deletedCustomer = CustomerDAO.getListCustomerById(lastSelectedCustomer);
                    CustomerDAO.deleteCustomer(deletedCustomer);
                    Notification.show(deletedCustomer.getName() + "deleted");
                    updateCustomerList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        contextMenu.setAsContextMenuOf(table);

        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {

                if (itemClickEvent.getButton() == MouseEventDetails.MouseButton.RIGHT) {
                    lastSelectedCustomer = Integer.valueOf(itemClickEvent.getItemId().toString());
                } else if (itemClickEvent.getButton() == MouseEventDetails.MouseButton.LEFT) {
                    grdchannels.setVisible(true);
                    updateCustomerChannelsList(Integer.valueOf(itemClickEvent.getItemId().toString()));
                }


            }
        });

        popupViewCustomerForm.addPopupVisibilityListener(new PopupView.PopupVisibilityListener() {
            @Override
            public void popupVisibilityChange(PopupView.PopupVisibilityEvent popupVisibilityEvent) {
                if(!popupVisibilityEvent.isPopupVisible()){
                    updateCustomerList();
                }
            }
        });
        verticalLayout.addComponents(filterLayout, table, lblChannels, grdchannels, popupViewCustomerForm);
        addComponents(verticalLayout);
        updateCustomerList();
    }

    public void updateCustomerChannelsList(int customer) {
        try {
            List<Channel> channels = ChannelsDAO.getChannelsByCustomer(customer);
            grdchannels.setContainerDataSource(new BeanItemContainer<>(Channel.class, channels));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomerList(String nameFilter) {
        try {
            List<Customer> customerList = CustomerDAO.getListCustomers(nameFilter);
            fillCustomersTableData(customerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fillCustomersTableData(List<Customer> customerList) {
        table.removeAllItems();
        table.removeContainerProperty("id");
        table.removeContainerProperty("name");
        table.removeContainerProperty("surname");
        table.removeContainerProperty("isActive");
        table.removeContainerProperty("gender");
        table.removeContainerProperty("gender");
        table.removeContainerProperty("birthday");
        table.removeContainerProperty("city");

        table.addContainerProperty("id", Integer.class, null);
        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("surname", String.class, null);
        table.addContainerProperty("isActive", Boolean.class, null);
        table.addContainerProperty("gender", String.class, null);
        table.addContainerProperty("birthday", String.class, null);
        table.addContainerProperty("city", String.class, null);

        for (Customer customer : customerList) {
            table.addItem(new Object[]{customer.getId(), customer.getName(), customer.getSurname(), customer.isActive(), Gender.values()[customer.getGender()].toString(), customer.getBirthDay().toString(), customer.getCity().getName()}, customer.getId());
        }
        table.refreshRowCache();

    }

    public void updateCustomerList() {
        try {
            List<Customer> customerList = CustomerDAO.getListCustomers();
            fillCustomersTableData(customerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
