package scraping.secop.WebSecop;

import org.apache.log4j.Logger;
import scraping.secop.SecopVO.Constantes;
import scraping.secop.SecopVO.DatosTablaVO;

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

    public void email(List<DatosTablaVO> datosLista, List<String> nombresArchivos){
        String host="smtp.gmail.com";
        final String user="giovannykano@gmail.com";
        final String password = Constantes.PASSWORDMAIL;

        String to="giovanni.calle@yopmail.com";
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user,password);
                    }
                });
        try {
            StringBuilder mensaje = new StringBuilder();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            Multipart multipart = new MimeMultipart();
            message.setSubject("Proceso de scraping");
            BodyPart mensajeBody = new MimeBodyPart();
            mensaje.append("Mensaje para oddoo  \n");
            for (DatosTablaVO datos : datosLista) {
                mensaje.append("Nombre de la entidad:" + datos.getNombreEntidad()+ "\n");
                mensaje.append("Enlace de presentación: " + datos.getEnlace()+ "\n");
                mensaje.append("Descripción de presentación: " + datos.getDescripcion()+ "\n");
                mensaje.append("Valor estimado: " + datos.getValorEstimado()+ "\n");
                for(int x = 0; x < datos.getListaCodigosUBSPC().size(); x++){
                    mensaje.append("Codigos UNSPC: " + datos.getListaCodigosUBSPC().get(0)+ "\n");
                }
            }
            mensaje.append("\n");
            mensajeBody.setText(mensaje.toString());
            multipart.addBodyPart(mensajeBody);
            mensajeBody = new MimeBodyPart();
            int contador = 0;
            for (DatosTablaVO datos : datosLista) {
                LOG.info("Ruta: " + datos.getPath() + "\\" + nombresArchivos.get(contador));
                if(nombresArchivos.get(contador) != null){
                    DataSource source = new FileDataSource(datos.getPath() + "\\" + nombresArchivos.get(contador));
                    mensajeBody.setDataHandler(new DataHandler(source));
                    mensajeBody.setFileName(source.getName());
                }
                contador++;
            }
            multipart.addBodyPart(mensajeBody);
            message.setContent(multipart);
            Transport.send(message);
            LOG.info("Mensaje enviado...");
        } catch (MessagingException ex){
            LOG.error("Ocurrio un error enviando el mensaje: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
