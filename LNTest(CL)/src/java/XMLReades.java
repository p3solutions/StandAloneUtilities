import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class XMLReades {

	public static Map<String, List<String>> getBase64EncodedColumnString(String xmlDocument)
			throws XMLStreamException, IOException {

		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream targetStream = IOUtils.toInputStream(xmlDocument);

		InputStreamReader isr = new InputStreamReader(targetStream);
		XMLEventReader eventReader = factory.createXMLEventReader(isr);

		boolean itemFlag = false;
		boolean richtextFlag = false;
		boolean pictureFlag = false;
		boolean gifFlag = false;
		String columnName = "";
		StringBuffer encodedString = new StringBuffer();

		Map<String, List<String>> cns = new HashMap<String, List<String>>();

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				String startElement = event.asStartElement().getName().getLocalPart();

				switch (startElement) {
				case "item":
					itemFlag = true;
					Iterator<Attribute> attributes = event.asStartElement().getAttributes();
					while (attributes.hasNext()) {
						Attribute x = attributes.next();
						if (x.getName().toString().equals("name"))
							columnName = x.getValue();
					}
					break;

				case "richtext":
					richtextFlag = true;
					break;

				case "picture":
					pictureFlag = true;
					break;

				case "gif":
					gifFlag = true;
					break;

				default:
					break;
				}
				break;
			case XMLStreamConstants.CHARACTERS:
				Characters characters = event.asCharacters();

				if (itemFlag && richtextFlag && pictureFlag && gifFlag) {
					encodedString.append(characters.getData());
				}

				break;
			case XMLStreamConstants.END_ELEMENT:
				String endElement = event.asEndElement().getName().getLocalPart();
				switch (endElement) {
				case "item":
					columnName = "";
					itemFlag = false;
					break;

				case "richtext":
					richtextFlag = false;
					break;

				case "picture":
					pictureFlag = false;
					break;

				case "gif":
					gifFlag = false;
					List<String> values;
					if (cns.containsKey(columnName)) {
						values = cns.get(columnName);
						values.add(encodedString.toString());
						cns.put(columnName, values);
					} else {
						values = new ArrayList<String>();
						values.add(encodedString.toString());
						cns.put(columnName, values);
					}
					encodedString.delete(0, encodedString.length() - 1);
					break;

				default:
					break;
				}
				break;
			}
		}
		eventReader.close();
		isr.close();
		return cns;

	}

	public static void main(String[] args) throws XMLStreamException {

		String splitter = File.separator.replace("\\", "\\\\");
		String s = "Admin\\Training/skil needs";

		String[] aa = s.split(splitter);
		System.out.println(aa);
		if (s.contains("\\")) {
			System.out.println(s.split(splitter)[1]);
		} else {
			System.out.println(s);
		}

//		try {
//			Map<String, List<String>> base64EncodedColumnString = XMLReades
//					.getBase64EncodedColumnString(XMLContent.xmlContent);
//
//			for (Entry<String, List<String>> string : base64EncodedColumnString.entrySet()) {
//				System.out.println(string.getKey()+" "+string.getValue().size());
//				for (String p : string.getValue()) {
//					Base64.Decoder mimeDecode = Base64.getMimeDecoder();
//					byte[] decode = mimeDecode.decode(p.getBytes());
//					FileUtils.writeByteArrayToFile(new File("C:\\\\Users\\\\91735\\\\Desktop\\\\a\\" + string.getKey()
//							+ UUID.randomUUID().toString() + ".gif"), decode);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

}
