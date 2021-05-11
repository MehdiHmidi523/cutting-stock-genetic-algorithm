import java.util.ArrayList;

public class Beam {
    private ArrayList<Integer> item_lengths_satisfied = new ArrayList<>();
    private int beam_length_left;
    private float original_beam_cost;

    Beam(int b_length, float b_cost) {
        beam_length_left = b_length;
        original_beam_cost = b_cost;
    }

    public boolean satisfy(int item_length) {
        if (beam_length_left >= item_length) {
            beam_length_left -= item_length;
            item_lengths_satisfied.add(item_length);
            return true;
        } else return false;
    }

    public int getBeam_length_left() {
        return beam_length_left;
    }

    public float getOriginal_beam_cost() {
        return original_beam_cost;
    }

    public ArrayList<Integer> getItem_lengths_satisfied() {
        return item_lengths_satisfied;
    }

    public void setItem_lengths_satisfied(ArrayList<Integer> item_lengths_satisfied) {
        this.item_lengths_satisfied = item_lengths_satisfied;
    }

    public void setBeam_length_left(int beam_length_left) {
        this.beam_length_left = beam_length_left;
    }

    public void setOriginal_beam_cost(float original_beam_cost) {
        this.original_beam_cost = original_beam_cost;
    }
}