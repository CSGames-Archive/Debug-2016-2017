package sim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import sim.reactor.Reactor;
import sim.reactor.ReactorItem;
import sim.reactor.items.Coolant;
import sim.reactor.items.ControlRod;
import sim.reactor.items.UraniumRod;

public class Simulator {

    public static void main(String[] args) {
        Reactor reactor = parseReactor(System.in);

        Integer turns = 0;
        Integer energy = 0;
        Integer heat = 0;
        while (heat < reactor.heatCapacity) {
            Integer e = reactor.energyOutput();
            Integer h = reactor.heatOutput();
            if(heat + h >= reactor.heatCapacity) {
                break;
            }
            energy += e;
            heat += h;
            turns++;
        }
        System.out.println("Reactor grid:");
        reactor.grid.displayGrid();
        System.out.println("\nReactor can run " + turns + " turns before explosion.");
        System.out.println(" * Energy output: " + energy);
        System.out.println(" * Heat output: " + heat);
    }

    /**
     * Parse the input source into a Reactor instance.
     *
     * Aborts the execution if the input is malformed.
     *
     * @param source.
     * @return Reactor instance.
     */
    static Reactor parseReactor(InputStream source) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(source));
        try {
            String line = reader.readLine();
            if (line == null) {
                System.out.println("Malformed input.");
                System.exit(1);
                return null;
            }
            String[] parts = parseLine(line);
            Reactor reactor = new Reactor(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

            line = reader.readLine();
            while (line != null) {
                parts = parseLine(line);
                ReactorItem item = itemFactory(parts);
                if (item == null) {
                    System.out.println("Malformed input.");
                    System.exit(1);
                }
                reactor.grid.getSlot(Integer.parseInt(parts[2]), Integer.parseInt(parts[3])).setItem(item);
                line = reader.readLine();
            }
            return reactor;
        } catch (IOException ex) {
            System.out.println("Malformed input.");
            System.exit(1);
            return null;
        }
    }

    /**
     * Split a line with a space.
     *
     * @param line the line to split.
     * @return the splat line.
     */
    static String[] parseLine(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 4) {
            System.out.println("Malformed input.");
            System.exit(1);
        }
        return parts;
    }

    /**
     * Build a ReactorItem from its String representation.
     *
     * @param parts splat line.
     * @return ReactorItem or null.
     */
    static ReactorItem itemFactory(String[] parts) {
        switch (parts[0]) {
            case "C":
                return new ControlRod(Integer.parseInt(parts[1]));
            case "U":
                return new UraniumRod(Integer.parseInt(parts[1]));
            case "O":
                return new Coolant(Integer.parseInt(parts[1]));
            default:
                return null;
        }
    }
}