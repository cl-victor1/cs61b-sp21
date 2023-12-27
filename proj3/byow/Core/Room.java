package byow.Core;

import java.io.Serializable;

public class Room implements Serializable {
    Engine.Position leftUp;
    Engine.Position leftBottom;
    Engine.Position rightUp;
    Engine.Position rightBottom;
    public Room(Engine.Position leftUp, Engine.Position leftBottom, Engine.Position rightUp, Engine.Position rightBottom) {
        this.leftUp = leftUp;
        this.leftBottom = leftBottom;
        this.rightUp = rightUp;
        this.rightBottom = rightBottom;
    }
}
