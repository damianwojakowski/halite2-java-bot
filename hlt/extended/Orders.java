package hlt.extended;

import java.util.List;

public class Orders {

    private List<SingleOrder> orders;

    public boolean areOrdersForPlanet(Integer planetId) {
        final boolean[] hasOrderForPlanetId = {false};
        orders.forEach(order -> {
            if (order.hasOrderForPlanetId(planetId)) {
                hasOrderForPlanetId[0] = true;

            }
        });
        return hasOrderForPlanetId[0];
    }
}
