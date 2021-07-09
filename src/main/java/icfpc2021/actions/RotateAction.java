package icfpc2021.actions;

public class RotateAction implements Action {
    public RotateAction(double x, double y, double rotate) {
        this.x = x;
        this.y = y;
        this.rotate = rotate;
    }

    public double x;
    public double y;
    public double rotate;
}
