
import java.util.Base64;
import java.util.List;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DxlExporter;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.domino.Stream;

public class TestMain {
	public static void main(String[] args) throws NotesException {

		try {

			String iorString = NotesFactory.getIOR("10.1.212.92:63148");
			Session session = NotesFactory.createSessionWithIOR(iorString, "Rani Angamuthu/Contractor/VF Corporation",
					"please change this password");

//			AgentContext agentContext = session.getAgentContext(); //not implemented

			Database db = null;
			try {
				db = session.getDatabase(session.getServerName(), "HR\\STDisabilityTracking.nsf", false);
				if (db == null) {
					System.out.println("db is null");
					db = null;
				} else {
					try {
						System.out.println("Title of " + db + " : \"" + db.getTitle() + "\"");
					} catch (NotesException e) {
						e.printStackTrace();
					}
				}
			} catch (NotesException e) {
				System.out.println("Connection Failed : " + e.getMessage());
				e.printStackTrace();
			}

			Document doc = db.getDocumentByUNID("DE206B05DF984DAB852578B800449682");
			DxlExporter exporter = session.createDxlExporter();
			System.out.println(exporter.getConvertNotesBitmapsToGIF());
			exporter.setConvertNotesBitmapsToGIF(true);
			System.out.println(exporter.getConvertNotesBitmapsToGIF());
			String exportDxl = exporter.exportDxl(doc);
			System.out.println(exportDxl);

//			List<Item> items = doc.getItems();
//			for (Item item : items) {
//			 System.out.println(item.getName());
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
