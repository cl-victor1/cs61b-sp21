package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int ROOM_NUMBERS = 9999;
    public LinkedList<Room> rooms = new LinkedList<>();

    private static final long SEED = 28741;
    private static final Random RANDOM = new Random(SEED);
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] finalWorldFrame = null;
        return finalWorldFrame;
    }

    // deal with positions
    public static class Position{
        int x;
        int y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // fill skeleton Tiles with Nothing
    public void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    //draw Single Room
    public void drawSingleRoom(TETile[][] tiles, Position leftUp, Position leftBottom, Position rightUp, Position rightBottom) {
        for (int x = leftBottom.x; x <= rightBottom.x; x++) {
            for (int y = leftBottom.y; y <= leftUp.y; y++) {
                tiles[x][y] = Tileset.WALL;
            }
        }
        for (int x = leftBottom.x + 1; x <= rightBottom.x - 1; x++) {
            for (int y = leftBottom.y + 1; y <= leftUp.y - 1; y++) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    // test overlap
    public boolean testOverlap(TETile[][] tiles, Position leftUp, Position leftBottom, Position rightUp, Position rightBottom) {
        boolean overlap = false;
        for (int x = leftBottom.x; x <= rightBottom.x; x++) {
            for (int y = leftBottom.y; y <= leftUp.y; y++) {
                if (tiles[x][y].character() != ' ') {
                    overlap = true;
                    break;
                }
            }
        }
        return overlap;
    }

    // create random Position
    private Position randomPositionGenerator() {
        int x = RANDOM.nextInt(80);
        int y = RANDOM.nextInt(30);
        return new Position(x, y);
    }

    // draw rooms
    public void createRoom(TETile[][] tiles, Position leftUp, Position leftBottom, Position rightUp, Position rightBottom) {
        if (!testOverlap(tiles, leftUp, leftBottom, rightUp, rightBottom)) {
            drawSingleRoom(tiles, leftUp, leftBottom, rightUp, rightBottom);
        }
    }

    // creat world
    public void createWorld(TETile[][] tiles) {
        for (int i = 0; i < ROOM_NUMBERS; i++) {
            Position leftBottom = randomPositionGenerator();
            int dx = RANDOM.nextInt(8) + 3;
            int dy = RANDOM.nextInt(8) + 3;
            Position leftUp = new Position(leftBottom.x, leftBottom.y + dy);
            Position rightBottom = new Position(leftBottom.x + dx, leftBottom.y);
            Position rightUp = new Position(leftBottom.x + dx, leftBottom.y + dy);
            if (rightUp.x < WIDTH && rightUp.y < HEIGHT) {
                createRoom(tiles, leftUp, leftBottom, rightUp, rightBottom);
            }
        }
    }

}
