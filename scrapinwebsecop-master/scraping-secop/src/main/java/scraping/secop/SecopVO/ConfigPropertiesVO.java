package scraping.secop.SecopVO;

public class ConfigPropertiesVO {

    private String userSecop;
    private String passwordSecop;
    private String userMail;
    private String passwordMail;
    private String userMailTo;
    private String codePath;
    private String driverPath;

    public String getDriverPath() {
        return driverPath;
    }

    public void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    public String getUserSecop() {
        return userSecop;
    }

    public String getPasswordSecop() {
        return passwordSecop;
    }

    public String getUserMail() {
        return userMail;
    }

    public String getPasswordMail() {
        return passwordMail;
    }

    public String getUserMailTo() {
        return userMailTo;
    }

    public String getCodePath() {
        return codePath;
    }

    public void setUserSecop(String userSecop) {
        this.userSecop = userSecop;
    }

    public void setPasswordSecop(String passwordSecop) {
        this.passwordSecop = passwordSecop;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public void setPasswordMail(String passwordMail) {
        this.passwordMail = passwordMail;
    }

    public void setUserMailTo(String userMailTo) {
        this.userMailTo = userMailTo;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }
}
