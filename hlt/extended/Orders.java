package hlt.extended;

import hlt.GameMap;
import hlt.Planet;
import hlt.Ship;

import javax.net.ssl.SNIHostName;
import java.util.ArrayList;
import java.util.List;

public class Orders {
    private PlanetsManager planetsManager;
    private GameMap gameMap;
    private List<SingleOrder> orders = new ArrayList<>();
    int playerId;

    public void setPlanetsManager(PlanetsManager planetsManager) {
        this.planetsManager = planetsManager;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

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

    public void removeCompletedOrders() {
        List<SingleOrder> ordersToBeRemoved = new ArrayList<>();

        for (SingleOrder singleOrder : orders) {
            if (singleOrder.getOrderType() == SingleOrder.TO_BE_REMOVED) {
                ordersToBeRemoved.add(singleOrder);
            }
        }

        orders.removeAll(ordersToBeRemoved);
    }

    public void validateOrders() {
        for (SingleOrder singleOrder : orders) {
            if (singleOrder.getOrderType() == SingleOrder.DOCK_PLANET) {
                Planet planet = planetsManager.getPlanetById(singleOrder.getPlanetId());
                if (planet == null || planet.isOwned() && planet.getOwner() != playerId) {
                    singleOrder.reset();
                }
            } else if (singleOrder.getOrderType() == SingleOrder.ATTACK_SHIP) {
                boolean shipAlive = false;
                for (Ship ship : gameMap.getAllShips()) {
                    if (ship.getId() == singleOrder.getEnemyShipId() && ship.getHealth() > 0) {
                        shipAlive = true;
                    }
                }
                if (!shipAlive) {
                    singleOrder.reset();
                }
            } else if (singleOrder.getOrderType() == SingleOrder.CONQUER_PLANET) {
                Planet planet = planetsManager.getPlanetById(singleOrder.getPlanetId());
                if (planet == null || planet.getOwner() == playerId && planet.isFull()) {
                    singleOrder.reset();
                }
            }

            if (gameMap.getShip(playerId, singleOrder.getShipId()) == null) {
                singleOrder.reset();
            }
        }
    }

    public void serOrderToAttackShip(int enemyShipId, Integer freeShipId) {
        SingleOrder singleOrder = new SingleOrder();
        singleOrder.setOrderToAttackEnemyShip(enemyShipId, freeShipId);
        orders.add(singleOrder);
    }

    public void setOrderToConquerPlanet(int planetId, int freeShipId) {
        SingleOrder singleOrder = new SingleOrder();
        singleOrder.setOrderToConquerPlanet(planetId, freeShipId);
        orders.add(singleOrder);
    }
}
