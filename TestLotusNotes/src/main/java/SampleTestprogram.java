

import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import lotus.domino.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SampleTestprogram {


    public static void main(String[] args) throws NotesException, IOException {
        ConnectionCore connection = new ConnectionCore();


        /*Host 		: ec2-52-41-5-201.us-west-2.compute.amazonaws.com
        database 	: mail/adminst.nsf    mail10.ntf
        password 	: Platform@2019
        username 	: Administrator/Platform
        Port		: 63148*/


        // Session session = connection.getSession("ec2-54-190-164-61.us-west-2.compute.amazonaws.com", "63148", "Administrator/Platform", "Platform@2019");
        Database db = connection.getConnection("ec2-52-41-5-201.us-west-2.compute.amazonaws.com",
                "63148",
                "Administrator/Platform",
                "Platform@2019",
                "mail10.ntf");


        System.out.println("=========================================================================================");
        System.out.println("Form List Started ");
        Timmer timmer = new Timmer();


        System.out.println("=========================================================================================");

        System.out.println("Non EmptyForm List Started ");
        timmer.getStartTime();
        Set<String> nonEmptyFormsTable = getFormList(db, new HashSet<String>());
        System.out.println("nonEmptyFormsTable size : " + nonEmptyFormsTable.size());
        EmlGenerator emlGenerator=new EmlGenerator();
        for (String tableName : nonEmptyFormsTable) {
            DocumentCollection dc = db.search("@Contains(form; \"" + tableName + "\")");
            System.out.println("Document Count " + dc.getCount());
            Document doc = dc.getFirstDocument();
            int count = 0;

            Map<String,String> attachmentEncoderValue=null;
            while (doc != null) {

                attachmentEncoderValue=new LinkedHashMap<>();
                System.out.println("===================Document Start======================");
                count = 0;
                for (Object att : doc.getParentDatabase().getParent().evaluate("@AttachmentNames", doc)) {
                    if (att == null || att.toString().isEmpty()) {
                        continue;
                    }
                    EmbeddedObject eb = doc.getAttachment(att.toString());
                    System.out.println("\t\t Attachment Name " + eb.getName());
                    File  file = new File("/Users/apple/Downloads"+ File.separator +eb.getName());
                    String cannonical=file.getAbsolutePath();
                    eb.extractFile(file.getCanonicalPath());
                    String encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(cannonical)));
                    attachmentEncoderValue.put(cannonical,encodedString);
                    count++;

                }
               /* List<Item> items = doc.getItems();
                for (Item item : items) {
                    if (item.getValueString() != null && !item.getValueString().isEmpty()) {
                        System.out.println( "\t\t\t\t"+item.getName() + "-----" + item.getValueString());
                    }else{
                        System.out.println( "\t\t\t\t"+item.getName()+"-----");
                    }
                }*/

               emlGenerator.start(doc,attachmentEncoderValue,"/Users/apple/Downloads");
                doc = dc.getNextDocument(doc);
            }
        }
        timmer.getEndTime();
        System.out.println("=========================================================================================");

    }



    static String isValid(String email) {

        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(email.matches(regex)){
            return email;
        }else{
            return "\""+email+"\""+" "+"<dummy@dummy.com>";
        }

    }
    private static Set<String> getViews(Vector<View> views) throws NotesException {
        Set<String> registerTable = new HashSet<String>();
        for (View f : views) {
            registerTable.add(f.getName());
        }
        return registerTable;
    }

    private static Set<String> getForms(Vector<Form> forms) throws NotesException {
        Set<String> registerTable = new HashSet<String>();
        for (Form f : forms) {
            registerTable.add(f.getName());
        }
        return registerTable;
    }

    public static Set<String> getFormList(Database db, Set<String> formatedList) {
        Set<String> registerTable = new HashSet<String>();
        Document doc = null;
        try {
            if (formatedList.isEmpty()) {
                int count = 0;
                DocumentCollection documentCollection = db.getAllDocuments();
                while ((doc = documentCollection.getNextDocument()) != null) {
                    String name = doc.getItemValueString("Form");
                    registerTable.add(name);
                    count++;
                }
            } else {
                registerTable = formatedList;
            }
        } catch (NotesException e) {
            System.out.println(e.getMessage());
        }
        return registerTable;
    }

    private String formatColumnName(String input) {
        String output = input.replace("\\s+", "-").replace("^", "_");
        if (output.startsWith("_") && output.endsWith("_") && output.length() > 2) {
            output = output.substring(1).substring(0, output.length() - 2);
        }
        return output.toUpperCase();
    }
}
