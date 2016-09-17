package vaadin;

import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import dao.DataSourceFactory;
import elemental.html.EmbedElement;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.*;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mounzer.masri on 17.9.2016.
 */
public class ReportLayout  extends VerticalLayout {
    private ComboBox cmboReports = new ComboBox("Reports");
    private Button btnGenerate = new Button("Generate");
    private  Embedded embeddedPdf = new Embedded() ;
    public ReportLayout(){
        cmboReports.addItems(0, 1);
        cmboReports.setItemCaption(0, "Male Costomers Report");
        cmboReports.setItemCaption(1, "Istanbul Costomers Report");
        embeddedPdf.setSizeFull();
        addComponents(cmboReports, btnGenerate,embeddedPdf);
        btnGenerate.addClickListener(e -> {
            if((Integer)(cmboReports.getValue()) == 0){
                generateReport(0);
            }else{
                generateReport(1);
            }

        });

    }

    private void displayReport ()
    {
        File reportFile = new File("customerReport.pdf");
        while (!reportFile.exists()){
            System.out.println("not exist");
        }

        embeddedPdf.setSource(new FileResource(reportFile));
        embeddedPdf.setMimeType("application/pdf");
        embeddedPdf.setType(Embedded.TYPE_BROWSER);


    }
    private void generateReport(int reportId){
        try {
            File reportFile = new File("customerReport.pdf");
            reportFile.delete();
            String srcPath = ReportLayout.class.getResource("").getPath();
            String reportSrcFile = "";

            if(reportId == 0){
                reportSrcFile = srcPath + "customersReport.jrxml";
            }else {
                reportSrcFile = srcPath +  "customersReportIstanbul.jrxml";
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
            DataSource dataSource = DataSourceFactory.getMysqlDataSource();
            Connection conn =  dataSource.getConnection();
            Map<String, Object> parameters = new HashMap<String, Object>();

            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);

            JRPdfExporter exporter = new JRPdfExporter();
            ExporterInput exporterInput = new SimpleExporterInput(print);
            exporter.setExporterInput(exporterInput);
            OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput("customerReport.pdf");
            exporter.setExporterOutput(exporterOutput);
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            displayReport();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
