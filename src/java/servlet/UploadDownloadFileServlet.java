package servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet("/UploadDownloadFileServlet")
public class UploadDownloadFileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ServletFileUpload uploader = null;

    @Override
    public void init() throws ServletException {
        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
        File filesDir = (File) getServletContext().getAttribute("FILES_DIR_FILE");
        fileFactory.setRepository(filesDir);
        this.uploader = new ServletFileUpload(fileFactory);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String fileName = request.getParameter("fileName");
        if (fileName == null || fileName.equals("")) {
            throw new ServletException("File Name can't be null or empty");
        }

        File file = new File(request.getServletContext().getAttribute("FILES_DIR") + File.separator + fileName);

        System.out.println("File location on server::" + file.getAbsolutePath());
        ServletContext ctx = getServletContext();
        try (InputStream fis = new FileInputStream(file)) {
            String mimeType = ctx.getMimeType(file.getAbsolutePath());
            response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (ServletOutputStream os = response.getOutputStream()) {
                byte[] bufferData = new byte[1024];
                int read = 0;
                while ((read = fis.read(bufferData)) != -1) {
                    os.write(bufferData, 0, read);
                }
                os.flush();
            }
        }
        System.out.println("File downloaded at client successfully");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new ServletException("Content type is not multipart/form-data");
        }
        
        

        configuraCabecalho(request, response);
        PrintWriter out = response.getWriter();
        out.write("<html><head><meta charset=UTF-8></head><body>");
        try {
            List<FileItem> fileItemsList = uploader.parseRequest(request);
            Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
            while (fileItemsIterator.hasNext()) {
                FileItem fileItem = fileItemsIterator.next();

                System.out.println("FieldName=" + fileItem.getFieldName());
                System.out.println("FileName=" + fileItem.getName());
                System.out.println("ContentType=" + fileItem.getContentType());
                System.out.println("Size in bytes=" + fileItem.getSize());

                String fullPath = fileItem.getName();
                String filename = fullPath.substring(fullPath.lastIndexOf(File.separator) + 1);
                
                renomearArquivoComCaracterEspeciais(request.getServletContext().getAttribute("FILES_DIR").toString(), fileItem.getName());
                
                String caminhoReal = request.getServletContext().getAttribute("FILES_DIR").toString();
                String nomeDoNovoArquivo = retornaNome(caminhoReal);
                File file = new File(caminhoReal + File.separator + filename);
                
                //out.write("UploadDownloadFileServlet?fileName= + filename");

                //System.out.println("Absolute Path at server=" + file.getAbsolutePath());
                
                fileItem.write(file);
                File renome = new File(caminhoReal+"/"+nomeDoNovoArquivo);
                file.renameTo(renome);
                
                out.write("File " + nomeDoNovoArquivo + " uploaded successfully.");
                out.write("<br>");
                out.write("<a href=\"UploadDownloadFileServlet?fileName=" + nomeDoNovoArquivo + "\">Download " + nomeDoNovoArquivo + "</a>");
            }
        } catch (FileUploadException e) {
            out.write("Exception in uploading file." + e);
        } catch (Exception e) {
            out.write("Exception in uploading file." + e);
        }
        out.write("</body></html>");
    }

    private void configuraCabecalho(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Progma", "no-cache");
        response.setHeader("Expires", "-1");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Header", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
    }
    
    private void renomearArquivoComCaracterEspeciais(String caminho,String dados) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(caminho+"/"+"teste.txt"), "ISO-8859-1"))) {
            out.write(dados);
        }
    }
    
    private String retornaNome(String caminho) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        String nome = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(caminho+"/"+"teste.txt"), "UTF-8"))) {
            while (br.ready()) {
                nome = br.readLine();
            }
        }
        return nome;
    }

}
