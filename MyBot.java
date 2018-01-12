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
        fleetManager.setGameMap(gameMap);

        final String initialMapIntelligence =
                "width: " + gameMap.getWidth() +
                "; height: " + gameMap.getHeight() +
                "; players: " + gameMap.getAllPlayers().size() +
                "; planets: " + gameMap.getAllPlanets().size();
        Log.log(initialMapIntelligence);

        for (;;) {
            networking.updateMap(gameMap);

            List<Planet> planets = new ArrayList<>(gameMap.getAllPlanets().values());
            planetsManager.updatePlanetsStatus(planets);

            List<Ship> ships = new ArrayList<>(gameMap.getMyPlayer().getShips().values());
            fleetManager.checkShipAndAddToNewShipsIfNotRegistered(ships);

            Log.log("Ships and Planets Added...");

            Log.log("Assigning orders...");
            fleetManager.assignOrdersForShips();
            Log.log("Orders assigned...");

            ArrayList<Move> moveList = fleetManager.generateMoveList();

            Networking.sendMoves(moveList);
        }
    }
}
