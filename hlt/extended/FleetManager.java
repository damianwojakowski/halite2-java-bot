package hlt.extended;

import hlt.Planet;
import hlt.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FleetManager {
    private Orders orders;
    private PlanetsManager planetsManager;
    private Map<Integer, Ship> allShips = new HashMap<>();
    private Map<Integer, Ship> miningShips = new HashMap<>();
    private Map<Integer, Ship> dockingShips = new HashMap<>();
    private Map<Integer, Ship> warriorShips = new HashMap<>();
    private Map<Integer, Ship> freeShip = new HashMap<>();

    public void setPlanetsManager(PlanetsManager planetsManager) {
        this.planetsManager = planetsManager;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public void checkShipAndAddToNewShipsIfNotRegistered(List<Ship> ships) {
        for (Ship ship : ships) {
            if (!allShips.containsKey(ship.getId())) {
                allShips.put(ship.getId(), ship);
                freeShip.put(ship.getId(), ship);
            }
        }
    }

    public void assignTasksForShips() {
        //TODO: check if ships finished their task
            // assign to free ships if done
            // assign to warrior ships if no free planets

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
        int shipsPerPlanet = 3;
        for (Planet freePlanet : new ArrayList<Planet>(planetsManager.getFreePlanets().values())) {
            int shipsCounter = 0;

            for (Ship newShip : new ArrayList<Ship>(freeShip.values())) {
                if (shipsCounter > shipsPerPlanet) {
                    break;
                }

                orders.serOrderToDockPlanet(freePlanet.getId(), newShip.getId());

                shipsCounter++;
            }
        }
    }
}
