package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogOutServlet", urlPatterns = "/logout")
public class LogOutServlet extends AbstractServlet  {
    /**
     * Invalidates session and redirects the request to jsp page(Login)
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException Exception coming from an I/O error
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath()+INDEX_ROUTE);
    }


    /**
     *  Handles the HTTP get request, redirecting it to INDEX_ROUTE (index.jsp)
     * @param request  HTTP request
     * @param response HTTP response
     * @throws ServletException Exception coming from the servlet itself
     * @throws IOException Exception coming from an I/O problem
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        forwardTo(request, response, INDEX_ROUTE);
    }
}
