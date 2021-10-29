package com.p3solutions.rtf_to_txt.process;

import com.p3solutions.rtf_to_txt.beans.InputBean;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class StartProcess {

    private InputBean inputBean;
    private int counter = 0;


    public StartProcess(InputBean inputBean) {
        this.inputBean = inputBean;
    }


    public void start() {
        File file = new File(inputBean.getInputPath());
        File[] listOfFiles = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("xml");
            }
        });
        for (File f : listOfFiles) {

            while (counter == inputBean.getMpp()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("processing " + f.getName());
                        processXml(f);
                        new File(inputBean.getInputPath() + File.separator + "processedFile"+File.separator).mkdir();
                        Files.move(f.toPath(), Paths.get(inputBean.getInputPath() + File.separator + "processedFile"+File.separator+f.getName()), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("processed " + f.getName());
                    } catch (XMLStreamException | IOException | BadLocationException | TikaException e) {
                        e.printStackTrace();
                        System.out.println("Failed to process " + f.getName() + "Excepion.." + e.getMessage());
                    }
                    counter--;
                }
            });
            counter++;
            t.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sleeper();


        }
    }

    private void sleeper() {
        while (counter != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processXml(File f) throws XMLStreamException, IOException, BadLocationException, TikaException {

        new File(inputBean.getOutputPath() + File.separator).mkdir();

        boolean rtfFlag = false;

        File of = new File(inputBean.getOutputPath() + File.separator + f.getName());
        of.createNewFile();
        Writer fileWriter = new FileWriter(of,StandardCharsets.UTF_8);
        fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        InputStream file = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(file);
        XMLEventReader eventReader = factory.createXMLEventReader(isr);
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String startValue = startElement.getName().getLocalPart();
                    switch (startValue) {
                        case "ROW":
                            Iterator attributes = startElement.getAttributes();
                            StringBuffer attribute = new StringBuffer();
                            while (attributes.hasNext()) {
                                attribute.append(attributes.next().toString());
                            }
                            fileWriter.write("<" + startValue + " " + attribute + ">");
                            break;
                        case "TEXT_DATA":
                        case "TEXT_DATA_LONG":
                            fileWriter.write("<" + startValue + ">");
                            rtfFlag = true;
                            break;
                        default:
                            fileWriter.write("<" + startValue + ">");
                            break;


                    }

                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    if (rtfFlag) {
                        Tika tika = new Tika();
                        Metadata metadata = new Metadata();
                        metadata.add(Metadata.CONTENT_ENCODING, StandardCharsets.UTF_8.name());
                        InputStream stream = TikaInputStream.get(characters.getData().toString().getBytes(StandardCharsets.UTF_8.name()), metadata);
                        String filecontent = tika.parseToString(stream,metadata);
                        fileWriter.write(filecontent.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;"));
                    } else {
                        fileWriter.write(characters.getData());

                    }


                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    String endValue = endElement.getName().getLocalPart();
                    switch (endValue) {
                        case "TEXT_DATA":
                        case "TEXT_DATA_LONG":
                            rtfFlag = false;
                            fileWriter.write("</" + endValue + ">");
                            break;
                        default:
                            fileWriter.write("</" + endValue + ">");
                            break;

                    }
            }
        }
        try {
            file.close();
            isr.close();
            eventReader.close();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
