package osmData;

public class NodeLink {
    private final long id;
    //координаты точки
    private final double lat;
    //координаты точки
    private final double lon;
    public NodeLink(long id, double lat, double lon){
        this.id=id;
        this.lat=lat;
        this.lon=lon;
    }
    //True-данная точка является перекрестком
    private boolean isCrossedRoad;
    public void setCrossedRoad(){
        this.isCrossedRoad=true;
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean isCrossedRoad() {
        return isCrossedRoad;
    }
}
