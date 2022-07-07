package osmData;

import java.util.ArrayList;
import java.util.List;

//Класс для хранения информации из нода "way"
public class Way {
    private final long id;
    //Лист со ссылками на ноды
    private final List<Long> wayNodes=new ArrayList<>();
    public Way(long id){
        this.id=id;
    }

    public List<Long> getWayNodes() {
        return wayNodes;
    }

    public long getId() {
        return id;
    }
}
