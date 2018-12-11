package newupload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">

        InputStream fileContent = filePart.getInputStream();
        filePart.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(fileContent));
        PrintWriter out = response.getWriter();
        int contador = 1;
        StringBuilder sb = new StringBuilder();
        while(br.ready()) {
            String colunas[] = br.readLine().split(";");
            sb.append("<p>");
            sb.append(colunas[0]);
            sb.append("</p>");
        }
        
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        
        out.write("<html>");
        out.write("<body>");
        out.write(sb.toString());
        out.write("</body>");
        out.write("</html>");
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
        System.out.println("Post");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
        System.out.println("Get");
    }
}
