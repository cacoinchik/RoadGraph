package graph;

import osmData.NodeLink;
import osmData.Way;

import java.util.*;

public class Graph {
    //Мар для хранения отобранных дорог
    private final Map<Long, Way> wayMap = new HashMap<>();

    //Мар для хранения отобранных нодов
    private final Map<Long, NodeLink> nodeMap = new HashMap<>();

    //Мар для хранения дорог(граней графа)
    private final Map<Long, Road> roadMap = new HashMap<>();

    //хранит в себе все перектерски(вершины)
    private final Map<Long, Point> pointMap = new HashMap<>();

    //Все ноды связанные с отобранными дорогами
    private final Set<Long> allWayNodesId = new HashSet<>();

    int roadId = 0;

    long startPointId;

    //Получение всех дорог с учетом перекрестков
    public void getRoads(Way way) {
        int count = 0;
        for (int i = 0; i < way.getWayNodes().size(); i++) {
            if (nodeMap.get(way.getWayNodes().get(i)).isCrossedRoad()) {
                count++;
            }
        }
        startPointId = nodeMap.get(way.getWayNodes().get(0)).getId();
        if (count == 2) {
            Road road = new Road(roadId, startPointId, nodeMap.get(way.getWayNodes().get(way.getWayNodes().size() - 1)).getId());
            roadId++;
            roadMap.put(road.getId(), road);
            road.getRoadNodes(way);
        } else {
            way.getWayNodes().stream().skip(1).forEach(x -> {
                if (nodeMap.get(x).isCrossedRoad()) {
                    Road road = new Road(roadId, startPointId, nodeMap.get(x).getId());
                    roadId++;
                    startPointId = nodeMap.get(x).getId();
                    roadMap.put(road.getId(), road);
                    road.getRoadNodes(way);
                }
            });
        }
    }

    //Получние перекрестков
    public void getPoints() {
        roadMap.forEach((key, value) -> {
            Point startPoint = new Point(value.getStartPoint(), nodeMap.get(value.getStartPoint()).getLat(), nodeMap.get(value.getStartPoint()).getLon());
            pointMap.put(startPoint.getId(), startPoint);
            Point finishPoint = new Point(value.getFinishPoint(), nodeMap.get(value.getFinishPoint()).getLat(), nodeMap.get(value.getFinishPoint()).getLon());
            pointMap.put(finishPoint.getId(), finishPoint);
        });
    }

    public Map<Long, Way> getWayMap() {
        return wayMap;
    }

    public Map<Long, NodeLink> getNodeMap() {
        return nodeMap;
    }


    public void getRoadLength() {
        roadMap.forEach((key, value) -> {
            double weight = 0;
            NodeLink node = nodeMap.get(value.getStartPoint());
            for (int i = 1; i < value.getRoadNodes().size(); i++) {
                weight = weight + CalculateDistance(node, nodeMap.get(value.getRoadNodes().get(i)));
                node = nodeMap.get(value.getRoadNodes().get(i));
            }
            value.setRoadLength(weight);
        });
    }

    private double CalculateDistance(NodeLink node, NodeLink nodeOSM1) {
        double radius = 6378137;
        double degreeConvert = Math.PI / 180;

        double x1 = Math.cos(degreeConvert * node.getLat()) * Math.cos(degreeConvert * node.getLon());
        double x2 = Math.cos(degreeConvert * nodeOSM1.getLat()) * Math.cos(degreeConvert * nodeOSM1.getLon());

        double y1 = Math.cos(degreeConvert * node.getLat()) * Math.sin(degreeConvert * node.getLon());
        double y2 = Math.cos(degreeConvert * nodeOSM1.getLat()) * Math.sin(degreeConvert * nodeOSM1.getLon());

        double z1 = Math.sin(degreeConvert * node.getLat());
        double z2 = Math.sin(degreeConvert * nodeOSM1.getLat());

        return radius * Math.acos(x1 * x2 + y1 * y2 + z1 * z2);
    }


    public Set<Long> getAllWayNodesId() {
        return allWayNodesId;
    }
}
