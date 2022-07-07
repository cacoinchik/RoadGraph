package graph;

import osmData.Way;

import java.util.ArrayList;
import java.util.List;

//Класс представляющий дорогу(грань графа)
public class Road {
    private final long id;
    //Точка начала дороги
    private final long startPoint;
    //Конечная точка дороги
    private final long finishPoint;
    //Длина дороги
    private double roadLength;
    //Ноды связные с дорогой
    private final List<Long> roadNodes = new ArrayList<>();

    public Road(long id, long startPoint, long finishPoint) {
        this.id = id;
        this.startPoint = startPoint;
        this.finishPoint = finishPoint;
    }

    //Получение нодов связанных с дорогой (без учета точек перекрестков)
    public void getRoadNodes(Way way) {
        for (int i = way.getWayNodes().indexOf(startPoint); i < way.getWayNodes().indexOf(finishPoint) + 1; i++) {
            roadNodes.add(way.getWayNodes().get(i));
        }
    }

    public long getId() {
        return id;
    }

    public long getStartPoint() {
        return startPoint;
    }

    public long getFinishPoint() {
        return finishPoint;
    }

    public void setRoadLength(double roadLength) {
        this.roadLength = roadLength;
    }

    public List<Long> getRoadNodes() {
        return roadNodes;
    }
}
