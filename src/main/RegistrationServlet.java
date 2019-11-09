package main;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "RegServ")
public class RegistrationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            User user = new User();
            user.setUser(request.getParameter("user_login"), request.getParameter("user_password"));

            if (user.checkUser(user.getUserName())) {
                throw new Exception("User already exist");
            }
            if(!user.checkPasswords(request.getParameter("user_password"),request.getParameter("user_password_check"))){
                throw new Exception("Password does not match");
            }
            //spravit salt a nahradit
            user.registerUser();

            response.sendRedirect("index.jsp");

        } catch (Exception e) {
            response.sendRedirect("login.jsp?error=1");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
