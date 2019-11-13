package main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FileDetailServ extends HttpServlet
{
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //todo : z requestu si vytiahneme URL fileu,
        // zisitme ci patri nam alebo nie, ci na nho mame permission stiahnut alebo nie
        // vsetky jeho permissions, comments, aj notifications, atd....
    }

}
