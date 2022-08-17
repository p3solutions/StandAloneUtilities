package emailservices;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;

import javax.mail.Message.RecipientType;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class MailService {

    Properties properties = new Properties();
    boolean propertiesread = false;
    boolean userFlag = false;
    String mailFrom;

    public synchronized boolean sendJobStatusMail() {

        loadProperties();
        Mailbean mbn = getMailProps();
        Email email = new Email();
        String mailCont = properties.getProperty("EMAIL.EXTRACTION.COMPLETED");
        email.setFromAddress("ARCHON", mbn.getSmtpFrom());
        email.setSubject("TEST EMAIL");

        Object[] replaceValues = new Object[]{"sathian"};
        email.setTextHTML(MessageFormat.format(mailCont, replaceValues));
        email.addRecipient("ARCHON", "chiranjeevi.m@platform3solutions.com", RecipientType.TO);
        return sendemail(email, mbn);
    }

    private void loadProperties() {
        if (propertiesread)
            return;
        try {
            InputStream fileInput = MailService.class.getResourceAsStream("/mail.properties");
            properties.load(fileInput);
            fileInput.close();
            propertiesread = true;
            System.out.println("Successfully fetched prop file " + fileInput);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean sendemail(Email email, Mailbean mbn) {
        try {
            new Mailer(mbn.getSmtpServer(), Integer.parseInt(mbn.getSmtpPort()), mbn.getSmtpFrom(), mbn.getSmtpPwd(),
                    TransportStrategy.SMTP_TLS).sendMail(email);
            System.out.println("Mail sent successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Mail sent failed Error: " + e.getMessage());
            e.printStackTrace();
            Mailbean.setSmtpException(e);
            return false;
        }
    }

    private Mailbean getMailProps() {

        Mailbean mailbean = new Mailbean();
        mailbean.setSmtpFrom("archon.p3@gmail.com");
        mailbean.setSmtpPort("587");
        mailbean.setSmtpServer("smtp.gmail.com");
        mailbean.setSmtpPwd("uafymwsfsnpfmosn");

//        mailbean.setSmtpFrom("OTIASRV_PROD@vfc.com");
//        mailbean.setSmtpPort("25");
//        mailbean.setSmtpServer("itxf2aln09.vfc.com");
//        mailbean.setSmtpPwd("");

        System.out.println(mailbean.toString());
        return mailbean;
    }

}