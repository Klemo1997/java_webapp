package main;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Date;

@WebServlet(name = "LoginServ")
public class LoginServ extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        int badLoginCount = session.getAttribute("badLoginAttempt") != null
                ? (int)session.getAttribute("badLoginAttempt")
                : 0;

        try {
            User user = new User();
            user.setUser(request.getParameter("user_login").trim(), request.getParameter("user_password"));

            boolean isLocked = badLoginCount + 1 >= 3;

            if (session.getAttribute("unlockTime") != null) {
                isLocked = isLocked && (Long)new Date().getTime() < (Long)session.getAttribute("unlockTime");
            }

            if (isLocked) {
                throw new Exception("attempts_exceeded");
            }

            if (!user.verify()) {
                throw new Exception("verification_failed");
            }


            session.setAttribute("userId", String.valueOf(user.getId()));

            //setting session to expiry in 10 mins
            session.setMaxInactiveInterval(10*60);

            Cookie userName = new Cookie("user", user.getUserName());
            Cookie userId   = new Cookie("userId", String.valueOf(user.getId()));

            userName.setMaxAge(10*60);
            userId.setMaxAge(10*60);

            response.addCookie(userName);
            response.addCookie(userId);

            response.sendRedirect("index.jsp?success=loggedin");

        } catch (Exception e) {
            // Prekrocene pokusy o prihlasenie, odstavili sme usera na 5 minut
            if (e.getMessage().equals("attempts_exceeded")) {
                if (session.getAttribute("unlockTime") == null) {
                    session.setAttribute("unlockTime", session.getLastAccessedTime() + session.getMaxInactiveInterval() * 1000);
                }
                response.sendRedirect("login.jsp?error=attemptsexceeded");
                return;
            } else if (e.getCause() != null && e.getCause().toString().contains("ConnectException")) {
                // Nenasla sa databaza, vyhodime error
                response.sendRedirect("login.jsp?error=nodatabase");
                return;
            }
            // Zle prihlasovacie udaje
            if (e.getMessage().equals("verification_failed")) {
                session.setMaxInactiveInterval(5*60);
                session.setAttribute("badLoginAttempt", ++badLoginCount);
                response.sendRedirect("login.jsp?error=badlogin");
                return;
            }
            // Ostatne errory
            response.sendRedirect("login.jsp?error=1");
        }

    }
}
