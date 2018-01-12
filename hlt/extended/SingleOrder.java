package hlt.extended;

public class SingleOrder {

    final static public int DOCK_PLANET = 0;
    final static public int ATTACK_SHIP = 1;

    private int planetId;
    private int shipId;
    private int orderType;

    public boolean hasOrderForPlanetId(int planetId) {
        return this.planetId == planetId;
    }

    public void setOrderToDockPlanet(int planetId, int shipId) {
        this.planetId = planetId;
        this.shipId = shipId;
        this.orderType = DOCK_PLANET;
    }
}
