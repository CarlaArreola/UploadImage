/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import configuration.ConnectionBD;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author swoke
 */
@WebServlet(name = "UploadImageServlet", urlPatterns = {"/upload_image_servlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50)
public class UploadImageServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "images";
    Connection conn;
    PreparedStatement ps;
    Statement statement;
    ResultSet rs;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UploadImageServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UploadImageServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    List<String> imagePaths = getImagePathsFromDatabase();
    

    request.setAttribute("imagePaths", imagePaths);
    request.getRequestDispatcher("/jsp/display_images.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String applicationPath = request.getServletContext().getRealPath("");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

        File uploadDir = new File(uploadFilePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Part part = request.getPart("image");
        String fileName = getFileName(part);

        String filePath = uploadFilePath + File.separator + fileName;
        part.write(filePath);
        String relativePath = UPLOAD_DIR + "/" + fileName;

        saveImagePathToDatabase(relativePath);

        response.getWriter().println("Imagen subida correctamente");
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 2, token.length() - 1);
            }
        }
        return "";
    }

    private void saveImagePathToDatabase(String imagePath) {
        try {
            ConnectionBD conexion = new ConnectionBD();
            conn = conexion.getConnectionBD();
            String sql = "INSERT INTO imagenes (ruta_imagen) VALUES (?)";
            ps = conn.prepareStatement(sql); 
            ps.setString(1, imagePath);

            int filasInsertadas = ps.executeUpdate();

            if (filasInsertadas > 0) {
                System.out.println("Imagen registrada");
            } else {
                System.out.println("No se registró la imagen");
            }

            ps.close();
            conn.close();
        } catch (SQLException e) { 
            System.out.println("Error al insertar imagen: " + e.getMessage());
        }
    }

    private List<String> getImagePathsFromDatabase() {
        List<String> imagePaths = new ArrayList<>();

        try {
            ConnectionBD conexion = new ConnectionBD();
            String sql = "SELECT ruta_imagen FROM imagenes";

            conn = conexion.getConnectionBD();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery(); 

            while (rs.next()) {
                imagePaths.add(rs.getString("ruta_imagen"));
            }

            rs.close();  
            ps.close(); 
            conn.close();  
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imagePaths;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
