package com.example.agriweb.services;

import com.example.agriweb.models.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserCsvExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormatter.format(new Date());
        String fileName = "user_"+ timestamp + ".csv";

        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachement; filename="+fileName;
        response.setHeader(headerKey, headerValue);
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"User ID", "Username", "E-mail", "Roles", "Enabled"};
        String[] fieldMapping = {"idUser", "username", "email", "roles", "enabled"};
        csvWriter.writeHeader(csvHeader);

        for (User user : listUsers){
            csvWriter.write(user, fieldMapping);
        }

        csvWriter.close();

    }
}
