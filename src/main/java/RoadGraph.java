import graph.Graph;
import org.xml.sax.SAXException;
import parser.Parser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class RoadGraph {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Parser parser = new Parser();
        Graph graph = parser.start();
        graph.getWayMap().forEach((key, value) -> graph.getRoads(value));
        graph.getPoints();
        graph.getRoadLength();
        System.out.println(System.currentTimeMillis());
    }
}
