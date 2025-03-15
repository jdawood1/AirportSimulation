import java.util.ArrayList;
import java.util.List;

public class AirportManager {
    private final List<Airport> airports;

    public AirportManager(List<Airport> airports) {
        // Defensive copy to prevent external modification
        this.airports = new ArrayList<>(airports);
    }

    /**
     * Retrieves an airport by its name.
     *
     * @param name The name of the airport to search for.
     * @return The {@code Airport} object if found, otherwise {@code null}.
     */
    public Airport getAirportByName(String name) {
        return airports.stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}

