import emailservices.MailService;

public class EmailTester {
    public static void main(String[] args) {

        MailService mailService = new MailService();
        mailService.sendJobStatusMail();

    }
}
