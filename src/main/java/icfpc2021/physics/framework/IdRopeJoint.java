package icfpc2021.physics.framework;

import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.geometry.Vector2;

public class IdRopeJoint extends RopeJoint<SimulationBody> {
    public int id;

    public IdRopeJoint(int id,
                       SimulationBody body1,
                       SimulationBody body2,
                       Vector2 anchor1,
                       Vector2 anchor2) {
        super(body1, body2, anchor1, anchor2);
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof IdRopeJoint) {
            IdRopeJoint that = (IdRopeJoint) obj;
            return that.id == id;
        } else {
            return false;
        }
    }
}
