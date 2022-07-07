package graph;

//Класс представляющий перекрестки(вершина графа)
public class Point {
    private final long id;
    //Координаты перекрестка
    private final double lat;
    //Координаты перекрестка
    private final double lon;

    public long getId() {
        return id;
    }

    public Point(long id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

}
