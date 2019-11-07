package scraping.secop.Util;

import org.apache.log4j.Logger;
import scraping.secop.SecopVO.ConfigPropertiesVO;

import java.io.*;
import java.util.Properties;

public class ConfigProperties {

    private static final Logger LOG = Logger.getLogger(ConfigProperties.class);

    private InputStream input;


    public ConfigPropertiesVO loadConfig(ConfigPropertiesVO config) throws IOException {
        try{
            Properties prop = new Properties();
            String propFile = "\\config.properties";

            String path = new File("").getAbsolutePath();
            input = new FileInputStream(path + propFile);
            if(input != null){
                prop.load(input);
            }else{
                LOG.error("El archivo properties " + propFile +" no se ha encontrado.");
            }

            config.setUserSecop(prop.getProperty("user-secop"));
            config.setPasswordSecop(prop.getProperty("password-secop"));
            config.setUserMail(prop.getProperty("user-mail"));
            config.setPasswordMail(prop.getProperty("password-mail"));
            config.setUserMailTo(prop.getProperty("user-mail-to"));
            config.setCodePath(prop.getProperty("code-path"));
            config.setDriverPath(prop.getProperty("driver-path"));
        }
        catch (IOException ex){
            LOG.error("Ha ocurrido un error leyendo el .properties: " + ex.getMessage());
        }
        finally {
            input.close();
        }
        return config;
    }

}
