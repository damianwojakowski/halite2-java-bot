package hlt.extended;

import hlt.Planet;
import hlt.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FleetManager {
    Orders orders;
    PlanetsManager planetsManager;
    Map<Integer, Ship> allShips = new HashMap<>();
    Map<Integer, Ship> miningShips = new HashMap<>();
    Map<Integer, Ship> dockingShips = new HashMap<>();
    Map<Integer, Ship> warriorShips = new HashMap<>();
    Map<Integer, Ship> newShips = new HashMap<>();

    public void checkShipAndAddToNewShipsIfNotRegistered(List<Ship> ships) {
        for (Ship ship : ships) {
            if (!allShips.containsKey(ship.getId())) {
                allShips.put(ship.getId(), ship);
                newShips.put(ship.getId(), ship);
            }
        }
    }

    public void assignTasksForShips() {
        if (planetsManager.areFreePlanets()) {
            assignTasksToDockPlanets();
        }

        if (planetsManager.areMyPlanetsFull()) {
            assignTasksToAttackEnemies();
        }
    }

    private void assignTasksToAttackEnemies() {
    }

    private void assignTasksToDockPlanets() {
        for (Planet freePlanet : new ArrayList<Planet>(planetsManager.getFreePlanets().values())) {

        }
    }

    public void setPlanetsManager(PlanetsManager planetsManager) {
        this.planetsManager = planetsManager;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }
}
