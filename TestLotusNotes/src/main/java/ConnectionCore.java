import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;

public class ConnectionCore {
    public Session getSession(String hostname, String portno, String username, String password) {
        Session session = null;
        try {

            String iorString = NotesFactory.getIOR(hostname + ":" + portno);
            session = NotesFactory.createSessionWithIOR(iorString, username, password);
        } catch (NotesException e) {
            e.printStackTrace();
        }
        return session;
    }

    public Database getConnection(String hostname, String portno, String username, String password,
                                  String databasename) {
        Database db = null;
        try {
            Session session = getSession(hostname, portno, username, password);
            db = session.getDatabase(session.getServerName(), databasename,false);

            //   db = session.getDatabase(session.getServerName(), databasename.split("/")[1], false);
            if (db == null) {
                db = null;
            } else {
                try {
                    System.out.println("Title of " + db + " : \"" + db.getTitle() + "\"");
                    System.out.println("Access level: "+ db.getCurrentAccessLevel());
                    System.out.println("Documents "+db.getAllDocuments().getCount());
                } catch (NotesException e) {
                    e.printStackTrace();
                }
            }
        } catch (NotesException e) {

            e.printStackTrace();
        }
        return db;
    }
}

