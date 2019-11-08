package main;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class DbTestServlet extends HttpServlet
{
    private final String APP_ROOT = "C:/Users/matus/IdeaProjects/java_webapp";

    protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        doPost(req,res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        File file = new File("/test.log");
        PrintStream ps = new PrintStream(file);
        PrintWriter pw=res.getWriter();
        res.setContentType("text/html");

        String tb = req.getParameter("table");
        pw.println("TRYING.....\n");
        try
        {
            //nefunguje zatial, mozte skusit rozbehat
            String localhostURL = "jdbc:mysql://localhost/";

            final String DB_PASSWORD = ""; //JumpUpAndDown
//            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con= DriverManager.getConnection(localhostURL,"root",DB_PASSWORD);
            Statement st=con.createStatement();
            pw.println("connection established successfully...!!");

            ResultSet rs=st.executeQuery("Select * from users");


            while(rs.next())
            {
                pw.println("<tr><td>"+rs.getInt(1)+"</td><td>"+rs.getString(2)+"</td>"+
                        "<td>"+rs.getString(3)+"</td></tr>");
            }
            pw.println("</table>");
            pw.close();
        }
        catch (Exception e){
            e.printStackTrace(ps);
        }
        ps.close();
    }}
