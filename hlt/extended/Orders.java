package hlt.extended;

import java.util.ArrayList;
import java.util.List;

public class Orders {

    private List<SingleOrder> orders = new ArrayList<>();

    public boolean areOrdersSetForPlanet(Integer planetId) {
        final boolean[] hasOrderForPlanetId = {false};
        orders.forEach(order -> {
            if (order.hasOrderForPlanetId(planetId)) {
                hasOrderForPlanetId[0] = true;

            }
        });
        return hasOrderForPlanetId[0];
    }

    public List<SingleOrder> getOrders() {
        return this.orders;
    }

    public void serOrderToDockPlanet(int planetId, int shipId) {
        SingleOrder singleOrder = new SingleOrder();
        singleOrder.setOrderToDockPlanet(planetId, shipId);
        orders.add(singleOrder);
    }

    public boolean areOrdersSetForShip(int shipId) {
        for (SingleOrder singleOrder : orders) {
            if (singleOrder.getShipId() == shipId) {
                return true;
            }
        }
        return false;
    }
}
