package hlt.extended;

public class SingleOrder {

    final static public int TO_BE_REMOVED = -1;
    final static public int DOCK_PLANET = 0;
    final static public int ATTACK_SHIP = 1;

    private int planetId;
    private int shipId;
    private int orderType;
    private int enemyShipId;

    public int getShipId() {
        return shipId;
    }

    public int getEnemyShipId() { return enemyShipId; }

    public int getPlanetId() {
        return planetId;
    }

    public int getOrderType() {
        return orderType;
    }

    public boolean hasOrderForPlanetId(int planetId) {
        return this.planetId == planetId;
    }

    public void setOrderToDockPlanet(int planetId, int shipId) {
        this.planetId = planetId;
        this.shipId = shipId;
        this.orderType = DOCK_PLANET;
    }

    public void reset() {
        planetId = -1;
        shipId = -1;
        orderType = TO_BE_REMOVED;
    }

    public void setOrderToAttackEnemyShip(int enemyShipId, Integer freeShipId) {
        this.enemyShipId = enemyShipId;
        this.shipId = freeShipId;
        this.orderType = ATTACK_SHIP;
    }
}
