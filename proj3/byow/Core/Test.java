package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import static byow.Core.Engine.*;

public class Test {
    // just to test
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.interactWithInputString(args[0]);
        System.out.println(engine.toString());
    }
}
