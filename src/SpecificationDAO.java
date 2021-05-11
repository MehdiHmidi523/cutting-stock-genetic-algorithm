import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SpecificationDAO {

    private String FILE_PATH;
    private int m, n;  // m:  number of beam in stock, n: number of items ordered
    private HashMap<Integer, Float> beams_in_stock = null;
    private ArrayList<Order> orders = null;

    public SpecificationDAO(String file_path) {
        if (file_path != null) {
            FILE_PATH = file_path;
            read_problem_spec();
        } else {
            try {
                throw new FileNotFoundException("This file path could not be resolved");
            } catch (FileNotFoundException e) {
                e.getMessage();
            }
        }
    }

    public void read_problem_spec() {
        try (LineNumberReader rdr = new LineNumberReader(new FileReader(FILE_PATH))) {
            ArrayList<Integer> beams_length = new ArrayList<>();
            ArrayList<Float> beams_cost = new ArrayList<>();
            ArrayList<Integer> item_length = new ArrayList<>();
            ArrayList<Integer> item_quant = new ArrayList<>();

            // capture problem specification line by line
            for (String line; (line = rdr.readLine()) != null; ) {
                switch (rdr.getLineNumber()) {
                    case 1:
                        m = Integer.parseInt(line.split(":")[1].trim());
                        break;
                    case 2:
                        n = Integer.parseInt(line.split(":")[1].trim());
                        break;
                    case 3:
                        String[] tmp = line.split(":")[1].split(",");
                        for (int i = 0; i < tmp.length; i++)
                            beams_length.add(Integer.parseInt(tmp[i].trim()));
                        break;
                    case 4:
                        String[] aux = line.split(":")[1].split(",");
                        for (int i = 0; i < aux.length; i++)
                            beams_cost.add(Float.parseFloat(aux[i].trim()));
                        break;
                    case 5:
                        String[] str = line.split(":")[1].split(",");
                        for (int i = 0; i < str.length; i++)
                            item_length.add(Integer.valueOf(str[i].trim()));
                        break;
                    case 6:
                        String[] quant = line.split(":")[1].split(",");
                        for (int i = 0; i < quant.length; i++)
                            item_quant.add(Integer.valueOf(quant[i].trim()));
                        break;
                    default:
                        break;
                }
            }

            //generate runtime lists
            beams_in_stock = new HashMap<Integer, Float>(m);
            for (int i = 0; i < m; i++) {
                int b_length = beams_length.get(i);
                float b_cost = beams_cost.get(i);
                beams_in_stock.put(b_length, b_cost);
            }

            orders = new ArrayList<Order>(n);
            for (int i = 0; i < n; i++) {
                int p_length = item_length.get(i);
                int p_quantity = item_quant.get(i);
                orders.add(new Order(p_length, p_quantity));
            }
        } catch (IOException e) {
            System.err.println("Problem File Not valid ..");
            e.printStackTrace();
        }
    }

    public HashMap<Integer, Float> getBeams_in_stock() {
        return beams_in_stock;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }
}
