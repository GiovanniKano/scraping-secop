package scraping.secop.WebSecop;

import org.apache.log4j.Logger;
import scraping.secop.SecopVO.Constantes;
import scraping.secop.SecopVO.DatosTablaVO;
import scraping.secop.Util.FilesUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Properties;

public class SendEmail {

    private static final Logger LOG = Logger.getLogger(SendEmail.class);

    public void email(DatosTablaVO datos, String path){
        String host="smtp.gmail.com";
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Constantes.USEREMAIL, Constantes.PASSWORDMAIL);
                    }
                });
        try {
            StringBuilder mensaje = new StringBuilder();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Constantes.USEREMAIL));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(Constantes.TOUSERMAIL));
            long size = new FilesUtils().getSizeOfDirectory(path);
            size = size / (1024*1024);
            LOG.info(size + "mb");
            Multipart multipart = new MimeMultipart();
            message.setSubject(datos.getDescripcion());
            BodyPart mensajeBody = new MimeBodyPart();
            mensaje.append("\n");
            mensaje.append("Nombre de la entidad:" + datos.getNombreEntidad()+ "\n");
            mensaje.append("\n");
            mensaje.append("Enlace de presentación: " + datos.getEnlace()+ "\n");
            mensaje.append("\n");
            mensaje.append("Descripción de presentación: " + datos.getDescripcion()+ "\n");
            mensaje.append("\n");
            mensaje.append("Valor estimado: " + datos.getValorEstimado()+ "\n");
            mensaje.append("\n");
            for(int x = 0; x < datos.getListaCodigosUBSPC().size(); x++){
                LOG.info(datos.getListaCodigosUBSPC().get(x));
                mensaje.append("Codigos UNSPC: " + datos.getListaCodigosUBSPC().get(x)+ "\n");
            }
            mensaje.append("Fecha publicación: " + datos.getFechaPresentacion() + "\n");
            if(size > 25){
                mensaje.append("El contenido tiene archivos pero su peso es mayor a lo permito por gmail. Su peso es: " + size +"mb" + "\n");
            }
            mensaje.append("\n");
            mensaje.append("\n");

            mensaje.append("\n");
            mensajeBody.setText(mensaje.toString());
            multipart.addBodyPart(mensajeBody);

            if(size < 25){
                List<File> files = new FilesUtils().getDocumentsFolder(path);
                for (File archivos : files) {
                    DataSource source = new FileDataSource(archivos.getAbsolutePath());
                    BodyPart body = new MimeBodyPart();
                    body.setDataHandler(new DataHandler(source));
                    body.setFileName(source.getName());
                    multipart.addBodyPart(body);
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            LOG.info("Mensaje enviado...");
        } catch (MessagingException ex){
            LOG.error("Ocurrió un error enviando el mensaje: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}