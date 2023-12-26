package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import static byow.Core.Engine.*;

public class Test {
    // just to test
    public static void main(String[] args) {
        Engine engine = new Engine();

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        engine.fillWithNothing(world);
        engine.createWorld(world);
        ter.renderFrame(world);
    }
}
