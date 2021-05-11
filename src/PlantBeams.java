import java.util.ArrayList;
import java.util.List;

public class PlantBeams {
    List<Beam> beams_in_use = new ArrayList<Beam>();
    private float sum_cost = 0;
    private int waste = 0;

    PlantBeams() {
    }

    PlantBeams(List<Beam> plantSelection) {
        beams_in_use = plantSelection;
    }

    public boolean hasLength(int item_length) {
        for (Beam b : beams_in_use)
            if (b.getBeam_length_left() >= item_length)
                return true;
        return false;
    }

    public boolean satisfy(int item_length) {
        for (Beam b : beams_in_use)
            if (b.getBeam_length_left() >= item_length)
                return b.satisfy(item_length);
        return false;
    }

    public void reinforce(int random_beam_length, float cost) {
        beams_in_use.add(new Beam(random_beam_length, cost));
        sum_cost += cost;
    }

    public List<Beam> getBeams_in_use() {
        return beams_in_use;
    }

    public float getSum_cost() {
        sum_cost = 0;
        for (Beam x : beams_in_use)
            sum_cost += x.getOriginal_beam_cost();
        return sum_cost;
    }

    public int getWaste() {
        waste = 0;
        for (Beam x : beams_in_use)
            waste += x.getBeam_length_left();
        return waste;
    }

    public void setBeams_in_use(List<Beam> new_beams_in_use) {
        for (Beam l1 : new_beams_in_use) {
            Beam x = new Beam(l1.getBeam_length_left(), l1.getOriginal_beam_cost());
            x.getItem_lengths_satisfied().addAll(l1.getItem_lengths_satisfied());
            beams_in_use.add(x);
        }
    }

    public void setSum_cost(float sum_cost) {
        this.sum_cost = sum_cost;
    }

    public void setWaste(int waste) {
        this.waste = waste;
    }
}
