package utils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * @author 李浩铭
 * @date 2020/7/15 10:55
 * @descroption
 */
public class JavaMailUtils {

    public static void main(String[] args) {
//
        Mail mail=new Mail();
        mail.setUser("18926147365@163.com");
        mail.setPassword("GHBQMGRRMWUOKFUY");
        mail.setContent("测试");
        mail.setTitle("测试");
        mail.setToAddree("18926147365@163.com");
        mail.setFromAddress("18926147365@163.com");
        mail.setFromName("李浩铭");
        try {
            sendMail2(mail,new File("D:\\1.csv"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送附件
     * */
    public static void sendMail2(Mail mail, File file)throws Exception{

        //1.创建邮件对象
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.163.com"); // 指定SMTP服务器
        properties.put("mail.smtp.auth", "true"); // 指定是否需要SMTP验证
        Session session = Session.getInstance(properties);
        MimeMessage message =new MimeMessage(session);


        /*2.设置发件人
         * 其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
         * */
        message.setFrom(new InternetAddress(mail.getFromAddress(),mail.getFromName(),"UTF-8"));
        /*3.设置收件人
        To收件人   CC 抄送  BCC密送*/
        message.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(mail.getToAddree()));

        /*4.设置标题*/
        message.setSubject(mail.getTitle(),"UTF-8");
        //message.setContent("Test Content:这是一封测试邮件...","text/html;charset=UTF-8");

        /*5.设置邮件正文*/


        //创建附件节点  读取本地文件,并读取附件名称
        MimeBodyPart file1 = new MimeBodyPart();
        DataHandler dataHandler2 = new DataHandler(new FileDataSource(file));
        file1.setDataHandler(dataHandler2);
        file1.setFileName(MimeUtility.encodeText(dataHandler2.getName()));


        MimeMultipart multipart = new MimeMultipart();
        //创建文本节点
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(mail.getContent(),"text/html;charset=UTF-8");
        //将文本和图片添加到multipart
        multipart.addBodyPart(text);
        multipart.addBodyPart(file1);
        multipart.setSubType("mixed");//混合关系

        message.setContent(multipart);

        message.setSentDate(new Date());
        message.saveChanges();
        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.163.com",mail.getUser(),mail.getPassword());
        transport.sendMessage(message,message.getAllRecipients());
        transport.close();
        Boolean isFlag = true;

        System.out.println("sendMailServlet-----end2");
    }



    /**
     * 普通邮件
     * */
    public static void sendMail(Mail mail) throws Exception{
        //1.创建邮件对象
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.163.com"); // 指定SMTP服务器
        properties.put("mail.smtp.auth", "true"); // 指定是否需要SMTP验证
        Session session = Session.getInstance(properties);
        MimeMessage mimeMessage =new MimeMessage(session);

        /*2.设置发件人
         * 其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
         * */
        mimeMessage.setFrom(new InternetAddress(mail.getFromAddress(),mail.getFromName(),"UTF-8"));
        /*3.设置收件人
        To收件人   CC 抄送  BCC密送*/
        mimeMessage.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(mail.getToAddree()));

        /*4.设置标题和内容*/
        mimeMessage.setSubject(mail.getTitle(),"UTF-8");
        mimeMessage.setContent(mail.getContent(),"text/html;charset=UTF-8");
        mimeMessage.setSentDate(new Date());

        /*5.保存邮件*/
        mimeMessage.saveChanges();

        Transport transport = session.getTransport("smtp"); //获取邮件传输对象GHBQMGRRMWUOKFUY
        transport.connect("smtp.163.com",mail.getUser(),mail.getPassword());
        transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());
        transport.close();

        System.out.println("sendMailServlet-----end");
    }


    public static class Mail{
        public Mail(){}
        private String title;

        private String content;

        private String user;//163邮箱

        private String password;//163专用密码

        private String fromAddress;//发件人邮箱

        private String fromName;//发件人名称

        private String toAddree;//收件人

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public String getFromName() {
            return fromName;
        }

        public void setFromName(String fromName) {
            this.fromName = fromName;
        }

        public String getToAddree() {
            return toAddree;
        }

        public void setToAddree(String toAddree) {
            this.toAddree = toAddree;
        }
    }
}
