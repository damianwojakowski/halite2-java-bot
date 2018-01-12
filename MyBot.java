import hlt.*;
import hlt.extended.FleetManager;
import hlt.extended.Orders;
import hlt.extended.PlanetsManager;

import java.util.*;

public class MyBot {

    public static void main(final String[] args) {
        final Networking networking = new Networking();
        final GameMap gameMap = networking.initialize("Damiano v0.1.0");
        final FleetManager fleetManager = new FleetManager();
        final PlanetsManager planetsManager = new PlanetsManager();
        final Orders orders = new Orders();

        planetsManager.setPlayerId(gameMap.getMyPlayerId());

        fleetManager.setPlanetsManager(planetsManager);
        fleetManager.setOrders(orders);

        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        //Log.log(initialMapIntelligence);

        final ArrayList<Move> moveList = new ArrayList<>();

        for (;;) {
            moveList.clear();
            networking.updateMap(gameMap);

            List<Planet> planets = new ArrayList<>(gameMap.getAllPlanets().values());
            planetsManager.updatePlanetsStatus(planets);

            List<Ship> ships = new ArrayList<>(gameMap.getMyPlayer().getShips().values());
            fleetManager.checkShipAndAddToNewShipsIfNotRegistered(ships);
            fleetManager.assignTasksForShips();

            for (final Ship ship : gameMap.getMyPlayer().getShips().values()) {
                if (ship.getDockingStatus() != Ship.DockingStatus.Undocked) {
                    continue;
                }


                for (final Planet planet : gameMap.getAllPlanets().values()) {

                    if (planet.isOwned()) {
                        continue;
                    }

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
            Networking.sendMoves(moveList);
        }
    }
}
