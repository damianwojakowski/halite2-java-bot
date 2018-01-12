package hlt.extended;

import hlt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FleetManager {
    final ArrayList<Move> moveList = new ArrayList<>();

    private GameMap gameMap;
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

    public void assignOrdersForShips() {
        Log.log("assignOrdersForShips");
        //TODO: check if ships finished their task
            // assign to free ships if done
            // assign to warrior ships if no free planets

        if (planetsManager.areFreePlanets()) {
            Log.log("areFreePlanets");
            assignTasksToDockPlanets();
        }

        if (planetsManager.areMyPlanetsFull()) {
            Log.log("areMyPlanetsFull");
            assignTasksToAttackEnemies();
        }
    }

    private void assignTasksToAttackEnemies() {
    }

    private void assignTasksToDockPlanets() {
        Log.log("assignTasksToDockPlanets");
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

    public ArrayList<Move> generateMoveList() {
        moveList.clear();

        for (SingleOrder singleOrder : this.orders.getOrders()) {
            switch (singleOrder.getOrderType()) {
                case SingleOrder.DOCK_PLANET:
                    Ship ship = allShips.get(singleOrder.getShipId());
                    Planet planet = gameMap.getPlanet(singleOrder.getPlanetId());

                    if (ship.canDock(planet)) {
                        moveList.add(new DockMove(ship, planet));
                        break;
                    }

                    final ThrustMove newThrustMove = Navigation.navigateShipToDock(gameMap, ship, planet, Constants.MAX_SPEED);
                    if (newThrustMove != null) {
                        moveList.add(newThrustMove);
                    }

                    break;
            }
        }

        return moveList;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}
