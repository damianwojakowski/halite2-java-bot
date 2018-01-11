package hlt.extended;

public class SingleOrder {

    final static public Integer DOCK_PLANET = 0;

    public int planetId;
    public boolean shipId;
    public String orderType;

    public boolean hasOrderForPlanetId(int planetId) {
        return this.planetId == planetId;
    }
}
