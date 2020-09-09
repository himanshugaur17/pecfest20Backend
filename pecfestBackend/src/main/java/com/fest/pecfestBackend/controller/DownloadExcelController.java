package com.fest.pecfestBackend.controller;

import com.fest.pecfestBackend.service.EventUsersService;
import com.fest.pecfestBackend.service.ExcelFileExporter;
import com.fest.pecfestBackend.service.UserService;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class DownloadExcelController {

    @Autowired
    private ExcelFileExporter excelFileExporter;

    @Autowired
    private UserService userService;

    @Autowired
    private EventUsersService eventUsersService;

    @GetMapping("/download/users")
    public void downloadCsv(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");
        ByteArrayInputStream stream = excelFileExporter.contactListToExcelFile(userService.getUser().getData());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @GetMapping("/download/accommodationUsers")
    public void downloadAccommodationCsv(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=accommodation.xlsx");
        ByteArrayInputStream stream = excelFileExporter.contactListToExcelFile(userService.getUsersWithAccommodation().getData());
        IOUtils.copy(stream, response.getOutputStream());
    }

    @GetMapping("/download/participants/{id}")
    public void downloadEventUsersCsv(@PathVariable("id") String id,HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=participants.xlsx");
        if(eventUsersService.fetchEvent(id).getIsTeam()!=1) {
            ByteArrayInputStream stream = excelFileExporter.contactListToExcelFile(eventUsersService.fetchAllUsers(id));
            IOUtils.copy(stream, response.getOutputStream());
        }else{
            ByteArrayInputStream stream = excelFileExporter.contactTeamListToExcelFile(eventUsersService.fetchAllTeams(id));
            IOUtils.copy(stream, response.getOutputStream());
        }
    }

}
