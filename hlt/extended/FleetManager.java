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

    private List<Integer> freeShipsList = new ArrayList<>();
    private List<Integer> dockingShipsList = new ArrayList<>();

    public void setPlanetsManager(PlanetsManager planetsManager) {
        this.planetsManager = planetsManager;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public void checkShipAndAddToNewShipsIfNotRegistered(List<Ship> ships) {
        freeShipsList.clear();

        for (Ship ship : ships) {
            if (!orders.areOrdersSetForShip(ship.getId())) {
                Log.log("Add free ship: " + ship.getId());
                freeShipsList.add(ship.getId());
            }

            allShips.put(ship.getId(), ship);
        }
    }

    public void assignOrdersForShips() {
        Log.log("* ORDERS: " + orders.getOrders().toString());

        Log.log("Ships: " + allShips.values().toString());

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
        Log.log("assignTasksToDockPlanets");
        int shipsPerPlanet = 1;
        List<Integer> updated = new ArrayList<>();
        for (Planet freePlanet : new ArrayList<Planet>(planetsManager.getFreePlanets().values())) {
            int shipsCounter = 0;

            for (Integer freeShipId : freeShipsList) {
                Log.log("* FREE SHIP");
                Log.log("free ship id: " + freeShipId.toString());
                if (shipsCounter > shipsPerPlanet) {
                    break;
                }

                if (!updated.contains(freeShipId)) {
                    orders.serOrderToDockPlanet(freePlanet.getId(), freeShipId);
                    dockingShipsList.add(freeShipId);
                    updated.add(freeShipId);
                    shipsCounter++;
                }
            }
        }

        for (Integer updatedShipId : updated) {
            if (freeShipsList.contains(updatedShipId)) {
                freeShipsList.remove(updatedShipId);
            }
        }
    }

    public ArrayList<Move> generateMoveList() {
        moveList.clear();
        Log.log(moveList.toString());

        for (SingleOrder singleOrder : this.orders.getOrders()) {
            switch (singleOrder.getOrderType()) {
                case SingleOrder.DOCK_PLANET:
                    Ship ship = allShips.get(singleOrder.getShipId());
                    Planet planet = planetsManager.getPlanetById(singleOrder.getPlanetId());

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
