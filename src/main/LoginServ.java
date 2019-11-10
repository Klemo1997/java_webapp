package main;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoginServ")
public class LoginServ extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            User user = new User();
            user.setUser(request.getParameter("user_login"), request.getParameter("user_password"));

            if (!user.verify()) {
                throw new Exception("Wrong data for login");
            }

            HttpSession session = request.getSession();

            session.setAttribute("userId", String.valueOf(user.getId()));

            //setting session to expiry in 10 mins
            session.setMaxInactiveInterval(10*60);

            Cookie userName = new Cookie("user", user.getUserName());
            Cookie userId   = new Cookie("userId", String.valueOf(user.getId()));

            userName.setMaxAge(10*60);
            userId.setMaxAge(10*60);

            response.addCookie(userName);
            response.addCookie(userId);

            response.sendRedirect("index.jsp");

        } catch (Exception e) {
            response.sendRedirect("login.jsp?error=1");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
