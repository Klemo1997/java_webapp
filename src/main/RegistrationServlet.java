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
                throw new Exception("user_exists");
            }

            if (!user.checkPasswords(request.getParameter("user_password"),request.getParameter("user_password_check"))) {
                throw new Exception("password_not_match");
            }

            if (!user.isPasswordSecure()) {
                throw new Exception("password_insecure");
            }

            //spravit salt a nahradit
            user.registerUser();
            // Setneme userovi priecinky
            if (!user.setDirectories()) {
                throw new Exception("Failed while creating user directories");
            }

            response.sendRedirect("login.jsp?registration=success");

        } catch (Exception e) {
            String errorType = null;
            switch(e.getMessage()) {
                case "user_exists":
                    errorType = "userexists";
                    break;
                case "password_not_match":
                    errorType = "passmismatch";
                    break;
                case "password_insecure":
                    errorType = "passinsec";
                    break;
                case "password_in_dictionary":
                    errorType = "passindict";
                    break;
                default:
                    errorType = "1";
            }
            response.sendRedirect("registration.jsp?error=" + errorType);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}
