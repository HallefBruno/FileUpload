package newupload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
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
        int contador = 0;
        StringBuilder sb = new StringBuilder();
        
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>Nome");sb.append("</th>");
        sb.append("<th>Idade");sb.append("</th>");
        sb.append("</tr>");
        
        List<Pessoa> addPessoa = new LinkedList<>();
        Pessoa oEPessoa = new Pessoa();
        while(br.ready()) {
            String colunas[] = br.readLine().split(";");
            if(contador>0) {
                oEPessoa.nome = colunas[0];
                oEPessoa.idade = Integer.valueOf(colunas[1]);
                addPessoa.add(oEPessoa);
                oEPessoa = new Pessoa();
            }
            contador++;
        }
        addPessoa.forEach((p) -> {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(p.getNome());
            sb.append("</td>");
            sb.append("</tr>");
            
        });
        addPessoa.forEach((p) -> {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(p.getIdade());
            sb.append("</td>");
            sb.append("</tr>");
        });
        sb.append("</table>");
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        
        out.println("<html>");
        out.println("<body>");
        out.println(sb.toString());
        out.println("</body>");
        out.println("</html>");
        
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
    
    public class Pessoa {
        
        private String nome;
        private int idade;
        
        public Pessoa(String nome, int idade) {
            this.nome = nome;
            this.idade = idade;
        }

        public Pessoa() {
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public int getIdade() {
            return idade;
        }

        public void setIdade(int idade) {
            this.idade = idade;
        }
        
    }
}
