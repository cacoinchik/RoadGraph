package parser;

import graph.Graph;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import osmData.NodeLink;
import osmData.Way;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Parser {
    Graph graph;
    int count = 0;
    Set<String> roadTypes = new HashSet<>(Arrays.asList(
            "motorway", "trunk", "primary", "secondary", "tertiary", "unclassified", "residential", "motorway_link", "trunk_link",
            "primary_link", "secondary_link", "tertiary_link", "living_street", "service", "track"));

    //Запуск парсинга
    public Graph start() throws ParserConfigurationException, IOException, SAXException {
        graph = new Graph();
        Document document = readDocument();
        Node node = document.getFirstChild();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (!nodeList.item(i).getNodeName().equals("way")) {
                continue;
            } else if (nodeList.item(i).getNodeName().equals("relation")) {
                break;
            }
            checkWayNode(nodeList.item(i));
        }
        checkNodes(nodeList);
        return graph;
    }

    //Проверка нодов way
    private void checkWayNode(Node wayNode) {
        NodeList wayChildNodes = wayNode.getChildNodes();
        NamedNodeMap wayAttributes = wayNode.getAttributes();
        for (int i = 0; i < wayChildNodes.getLength(); i++) {
            if (wayChildNodes.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Node checkNode = wayChildNodes.item(i);
            NamedNodeMap tagAttributes = checkNode.getAttributes();
            if (checkNode.getNodeName().equals("tag")) {
                if (tagAttributes.getNamedItem("k").getNodeValue().equals("highway")) {
                    String vValue = tagAttributes.getNamedItem("v").getNodeValue();
                    boolean isRightRoad = roadTypes.add(vValue);
                    if (!isRightRoad) {
                        distribution(wayAttributes, wayChildNodes);
                    } else {
                        roadTypes.remove(vValue);
                    }
                    break;
                }
            }
        }
    }

    //Из нода найденной дороги вытягиваются ссылки и заносятсы в специальный лист, так же вытягивается id.
    private void distribution(NamedNodeMap attributes, NodeList tagList) {
        Way way = new Way(Long.parseLong(attributes.getNamedItem("id").getNodeValue()));
        int tagListLength = tagList.getLength();
        for (int i = 0; i < tagListLength; i++) {
            Node refNode = tagList.item(i);
            NamedNodeMap refAttributes = refNode.getAttributes();
            if (tagList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            if (refNode.getNodeName().equals("nd")) {
                String ref = refAttributes.getNamedItem("ref").getNodeValue();
                way.getWayNodes().add(Long.parseLong(ref));
            } else {
                graph.getWayMap().put(way.getId(), way);
                graph.getAllWayNodesId().addAll(way.getWayNodes());
                break;
            }
        }
    }

    //Проверка нодов
    private void checkNodes(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node checkNode = nodeList.item(i);
            if (checkNode.getNodeName().equals("node")) {
                NamedNodeMap attributes = checkNode.getAttributes();
                long nodeId = Long.parseLong(attributes.getNamedItem("id").getNodeValue());
                if (graph.getAllWayNodesId().contains(nodeId)) {
                    NodeLink nodeLink = new NodeLink(
                            nodeId,
                            Double.parseDouble(attributes.getNamedItem("lat").getNodeValue()),
                            Double.parseDouble(attributes.getNamedItem("lon").getNodeValue())
                    );
                    graph.getNodeMap().put(nodeLink.getId(), nodeLink);
                }
            }
        }
        graph.getWayMap().forEach((key, value) -> {
            long first = value.getWayNodes().get(0);
            long last = value.getWayNodes().get(value.getWayNodes().size() - 1);
            if (graph.getNodeMap().containsKey(first)) {
                graph.getNodeMap().get(first).setCrossedRoad();
            }
            if (graph.getNodeMap().containsKey(last)) {
                graph.getNodeMap().get(last).setCrossedRoad();
            }
        });
        graph.getAllWayNodesId().forEach(x -> {
            long id = x;
            count = 0;
            graph.getWayMap().forEach((key, value) -> {
                if (value.getWayNodes().contains(id)) {
                    count++;
                }
            });
            if (count > 1) {
                if (graph.getNodeMap().get(id) != null)
                    graph.getNodeMap().get(id).setCrossedRoad();
            }
        });
    }

    public Document readDocument() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        return builder.parse(new File("RU-NCH.osm"));
    }
}
