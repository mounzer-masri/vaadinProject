package vaadin;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import models.Customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Theme("mytheme")
@Widgetset("vaadin.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        TabSheet tabsheet = new TabSheet();

        ReportLayout reportLayout = new ReportLayout();

        CustomerForm form = new CustomerForm("INSERT");
        form.setMargin(true);

        CustomerListLayout customerListLayout = new CustomerListLayout();


        tabsheet.addTab(form,  "Add");
        tabsheet.addTab(customerListLayout, "Customer List");
        tabsheet.addTab(reportLayout, "Reports");
        tabsheet.setSizeFull();

        setContent(tabsheet);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {


    }
}