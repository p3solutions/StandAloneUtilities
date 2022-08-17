import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Suriyanarayanan K
 * on 22/02/20 7:44 PM.
 */
public class EmlGenerator {



    public String start(Document doc, Map<String, String> attchemetEncoderValue, String outputPath) throws NotesException {
        String NEW_LINE="\n";
        String EML_FILE_VALUE = "";
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        //--boundary_6_f2f6fe08-f8de-48ce-acf5-1caf0b237008
        String BOUNDARY_UUID="_"+ UUID.randomUUID().toString();
        String BOUNDARY_START="--boundary"+BOUNDARY_UUID;
        String BOUNDARY_ELEMENT="--"+BOUNDARY_START;
        String BOUNDARY_END=BOUNDARY_ELEMENT+"--";

        String EML_ELEMENT_CONTENT_TYPE_START="Content-Type: application/octet-stream;";
        String EML_ELEMENT_CONTENT_TYPE_END="Content-Transfer-Encoding: base64\n" +
                "Content-Disposition: attachment";

        String EML_BODY_CONTENT_TYPE= "Content-Type: text/plain; charset=us-ascii\n" +
                "Content-Transfer-Encoding: quoted-printable";



        EML_FILE_VALUE+="X-Sender: "+isValid(doc.getFirstItem("From").getValueString())+NEW_LINE;
        EML_FILE_VALUE+= "X-Receiver: "+isValid(doc.getFirstItem("SendTo").getValueString())+NEW_LINE;
        EML_FILE_VALUE+="MIME-Version: 1.0"+NEW_LINE;
        EML_FILE_VALUE+="From: "+isValid(doc.getFirstItem("From").getValueString())+NEW_LINE;
        EML_FILE_VALUE+= "To: "+isValid(doc.getFirstItem("SendTo").getValueString())+NEW_LINE;
        EML_FILE_VALUE+="Date: "+new Date(System.currentTimeMillis())+NEW_LINE;
        EML_FILE_VALUE+="Subject: ";
        EML_FILE_VALUE+=doc.getFirstItem("Subject").getValueString()==null?" ":doc.getFirstItem("Subject").getValueString()+NEW_LINE;
        EML_FILE_VALUE+="Content-Type: multipart/mixed;"+NEW_LINE;
        EML_FILE_VALUE+="boundary="+BOUNDARY_START+NEW_LINE;

        //BODY
        EML_FILE_VALUE+=BOUNDARY_ELEMENT+NEW_LINE;
        EML_FILE_VALUE+=EML_BODY_CONTENT_TYPE+NEW_LINE;
        EML_FILE_VALUE+=getBodyEncoder(doc.getFirstItem("Subject"))+NEW_LINE;


        //ELEMENT
        for (Map.Entry<String, String> stringStringEntry : attchemetEncoderValue.entrySet()) {
            EML_FILE_VALUE+=BOUNDARY_ELEMENT+NEW_LINE;
            EML_FILE_VALUE+=EML_ELEMENT_CONTENT_TYPE_START+"name=\""+stringStringEntry.getKey()+"\""+NEW_LINE;
            EML_FILE_VALUE+=EML_ELEMENT_CONTENT_TYPE_END+NEW_LINE;
            EML_FILE_VALUE+=stringStringEntry.getValue()+NEW_LINE;
        }

        //END
        EML_FILE_VALUE+=BOUNDARY_END;
        System.out.println();
        System.out.println("EML FILE CONTENT");
        System.out.println();
        //System.out.println(EML_FILE_VALUE);

        File emlFile=new File(outputPath+File.separator+doc.getUniversalID()+".eml");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(emlFile, true));
            writer.append(EML_FILE_VALUE);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
     String isValid(String email) {

        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(email.matches(regex)){
            return email;
        }else{
            return "\""+email+"\""+" "+"<dummy@dummy.com>";
        }

    }
    private  String getBodyEncoder(Item body) throws NotesException {
        if(body.getValueString()!=null){
            return  Base64.getEncoder().encodeToString(body.getValueString().toString().getBytes());
        }
        else{
            return "";
        }
    }
}
