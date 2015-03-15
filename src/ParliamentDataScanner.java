import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import db.DbConnector;
import table.DbTable;
import table.ResultDbTable;
import table.SessionDbTable;

import java.io.IOException;
import java.sql.SQLException;


public class ParliamentDataScanner {

    private static Logger log = Logger.getLogger(ParliamentDataScanner.class);

    public static void main(String[] args) {

        if (args.length != 4) {
            return;
        }

        log.info("Starting ParliamentDataScanner...");
        log.info("*Args* db name: " + args[0] + ", term: " + args[1] + ", first session: " + args[2] + ", last session: " + args[3]);
        String dbName = args[0];
        int term = Integer.parseInt(args[1]);
        int firstSession = Integer.parseInt(args[2]);
        int lastSession = Integer.parseInt(args[3]);

        try {
            DbConnector db = new DbConnector(dbName);
            DbTable resultDbTable = new ResultDbTable(db);
            resultDbTable.createTable();
            DbTable sessionDbTable = new SessionDbTable(db);
            sessionDbTable.createTable();
            for (int session = firstSession; session <= lastSession; session++) {
                boolean tableExists = true;
                int vote = 0;
                while (tableExists) {
                    vote++;
                    String url = buildUrl(term, session, vote);
                    Document doc = Jsoup.connect(url).timeout(0).get();
                    Elements tables = doc.select("table");
                    if (tables.size() > 0) {
                        log.info("Parsing page: " + url);
                        for (Element table : tables) {
                            boolean isSessionDataAdded = false;
                            for (Element row : table.select("tr")) {
                                Elements columns = row.select("td");
                                if (columns.size() == 7) {
                                    if (!isSessionDataAdded) {
                                        String dateHour = doc.select("small").first().text();
                                        String topic = doc.select("big").first().text();
                                        Elements subtopics = doc.select("font[color$=#AE0808]");
                                        String subtopic = "";
                                        if (subtopics.size() > 0) {
                                            subtopic = doc.select("font[color$=#AE0808]").first().text();
                                        }
                                        sessionDbTable.insertRow(buildSessionRow(term, session, vote, dateHour, topic, subtopic));
                                        isSessionDataAdded = true;
                                    }
                                    resultDbTable.insertRow(buildResultRow(term, session, vote, columns));
                                }
                            }
                        }
                    } else {
                        tableExists = false;
                    }
                }
            }
            log.info("All data saved successfully! Goodbye!");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String buildUrl(int term, int meeting, int vote) {
        return "http://sejm.gov.pl/Sejm" + String.valueOf(term) + ".nsf/agent.xsp?symbol=glosowania&NrKadencji=" +
                String.valueOf(term) + "&NrPosiedzenia=" + String.valueOf(meeting) + "&NrGlosowania=" +
                String.valueOf(vote);
    }

    public static String buildResultRow(int term, int meeting, int vote, Elements row) {
        StringBuilder sb = new StringBuilder(String.valueOf(term));
        sb.append(", ").append(String.valueOf(meeting)).append(", ").append(String.valueOf(vote)).append(", ");
        boolean firstEntry = true;
        for (Element entry : row) {
            if (!firstEntry) {
                sb.append(", ").append(entry.text());
            } else {
                sb.append("\"").append(entry.text()).append("\"");
                firstEntry = false;
            }
        }
        return sb.toString().replace("-", "0");
    }

    public static String buildSessionRow(int term, int meeting, int vote, String dateHour, String topic, String subtopic) {
        StringBuilder sb = new StringBuilder(String.valueOf(term));
        sb.append(", ").append(String.valueOf(meeting)).append(", ").append(String.valueOf(vote)).append(", ");
        sb.append("\"").append(dateHour).append("\", \"").append(topic.replace("\"", "~")).append("\"").append(", ");
        sb.append("\"").append(subtopic.replace("\"", "~")).append("\"");
        return sb.toString();
    }
}