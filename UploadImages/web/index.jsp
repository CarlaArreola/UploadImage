<%-- 
    Document   : index
    Created on : 15/10/2024, 07:37:06 PM
    Author     : carla
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <a href="${pageContext.request.contextPath}/jsp/upload_images.jsp"">Subir imagen</a>
        <a href="${pageContext.request.contextPath}/upload_image_servlet">Imagenes subidas</a>
    </body>
</html>
