package ex.pr.handler;

import ex.pr.Constants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GetClientTokenServlet extends HttpServlet {



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        String token = request.getParameter("token");
        System.out.println(token);

        Connection connection = null;
        try {
            Class.forName(Constants.DEFAULT_DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DEFAULT_URL, Constants.USERNAME, Constants.PASSWORD);
            Statement st = connection.createStatement();

           /* st.executeUpdate("INSERT INTO TokenList " +
                    "VALUES ('123'");*/

            String sql = "INSERT INTO TokenList.dbo.table_tokens" + " "+
                    "VALUES ('"+token+"');";

            st.executeUpdate(sql);

            if(connection != null){
                System.out.println("great :), connection succeeded");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
