package icfpc2021.actions;

public class MoveAction implements Action {
    public MoveAction(double dX, double dY) {
        this.dX = dX;
        this.dY = dY;
    }

    public double dX;
    public double dY;
}
