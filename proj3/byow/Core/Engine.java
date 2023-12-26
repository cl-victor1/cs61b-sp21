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
    public static final int ROOM_NUMBERS = 99999;
    public long SEED;
    public Random RANDOM;
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

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        if ((input.startsWith("N") || input.startsWith("n")) && (input.endsWith("S") || input.endsWith("s"))) {
            SEED = Long.parseLong(input.substring(1, input.length() - 1));
            RANDOM = new Random(SEED);
            fillWithNothing(world);
            createWorld(world);
        }
        return world;
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

    // let hallways start from the center of the previous room
    private Position prevCenter;

    // create hallways
    public void createHallways(TETile[][] tiles, Position prevCenter, Position currCenter) {
        int x0 = prevCenter.x;
        int y0 = prevCenter.y;
        int x1 = currCenter.x;
        int y1 = currCenter.y;

        while (x0 != x1 || y0 != y1) {
            if (x0 < x1) {
                tiles[x0][y0] = Tileset.FLOOR;
                x0++;
            } else if (x0 > x1) {
                tiles[x0][y0] = Tileset.FLOOR;
                x0--;
            } else if (y0 < y1) {
                tiles[x0][y0] = Tileset.FLOOR;
                y0++;
            } else if (y0 > y1) {
                tiles[x0][y0] = Tileset.FLOOR;
                y0--;
            }
            // deal with walls
            if (tiles[x0][y0 + 1] == Tileset.NOTHING) {
                tiles[x0][y0 + 1] = Tileset.WALL;
            }
            if (tiles[x0][y0 - 1] == Tileset.NOTHING) {
                tiles[x0][y0 - 1] = Tileset.WALL;
            }
            if (tiles[x0 + 1][y0] == Tileset.NOTHING) {
                tiles[x0 + 1][y0] = Tileset.WALL;
            }
            if (tiles[x0 - 1][y0] == Tileset.NOTHING) {
                tiles[x0 - 1][y0] = Tileset.WALL;
            }
            if (tiles[x0 + 1][y0 + 1] == Tileset.NOTHING) {
                tiles[x0 + 1][y0 + 1] = Tileset.WALL;
            }
            if (tiles[x0 - 1][y0 - 1] == Tileset.NOTHING) {
                tiles[x0 - 1][y0 - 1] = Tileset.WALL;
            }
            if (tiles[x0 + 1][y0 - 1] == Tileset.NOTHING) {
                tiles[x0 + 1][y0 - 1] = Tileset.WALL;
            }
            if (tiles[x0 - 1][y0 + 1] == Tileset.NOTHING) {
                tiles[x0 - 1][y0 + 1] = Tileset.WALL;
            }
        }
    }

    // draw rooms
    public void createRoom(TETile[][] tiles, Position leftUp, Position leftBottom, Position rightUp, Position rightBottom) {
        if (!testOverlap(tiles, leftUp, leftBottom, rightUp, rightBottom)) {
            drawSingleRoom(tiles, leftUp, leftBottom, rightUp, rightBottom);
            int currCenterX = (rightBottom.x + leftBottom.x) / 2;
            int currCenterY = (leftUp.y + leftBottom.y) / 2;
            Position currCenter = new Position(currCenterX, currCenterY);
            if (prevCenter != null) {
                createHallways(tiles, prevCenter, currCenter);
            }
            prevCenter = currCenter;
        }
    }

    // create world
    public void createWorld(TETile[][] tiles) {
        for (int i = 0; i < ROOM_NUMBERS; i++) {
            Position leftBottom = randomPositionGenerator();
            int dx = RANDOM.nextInt(8) + 6;
            int dy = RANDOM.nextInt(8) + 6;
            Position leftUp = new Position(leftBottom.x, leftBottom.y + dy);
            Position rightBottom = new Position(leftBottom.x + dx, leftBottom.y);
            Position rightUp = new Position(leftBottom.x + dx, leftBottom.y + dy);
            if (rightUp.x < WIDTH && rightUp.y < HEIGHT) {
                createRoom(tiles, leftUp, leftBottom, rightUp, rightBottom);
            }
        }
    }

}
