package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine implements Serializable {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private static final int ROOM_NUMBERS = 999999;
    private TERenderer ter = new TERenderer();
    private long SEED;
    private Random RANDOM;
    private List<Room> rooms = new ArrayList<>();
    private Position myPosition;
    private TETile[][] world = new TETile[WIDTH][HEIGHT + 3];
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        drawMenu();
        boolean skipMenu = false;
        boolean skipSeed = false;
        boolean worldExists = false;
        boolean quitMode = false;
        StringBuilder seed = new StringBuilder();
        String prevDescription = null;
        //check for keyboard input
        while (true) {
            // Heads Up Display
            if (worldExists) {
                int mouseX = (int) StdDraw.mouseX();
                int mouseY = (int) StdDraw.mouseY();
                String description = world[mouseX][mouseY].description();
                // show the description only when it changes
                if (!description.equals(prevDescription)) {
                    ter.renderFrame(world);
                    mousePointer(description);
                    prevDescription = description;
                }
            }
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            if (quitMode && (key == 'Q' || key == 'q')) {
                    // save game
                    saveWorld(this);
                    System.exit(0);
            }
            else if (!skipMenu && (key == 'n' || key == 'N')) {
                skipMenu = true;
                drawThing("random seed:");
            } else if (!skipMenu && (key == 'l' || key == 'L')){
                // load game
                Engine engine = loadWorld();
                if (engine != null) {
                    skipMenu = true;
                    worldExists = true;
                    skipSeed = true;
                    // restore the loaded world
                    restoreWorld(engine);
                    // restore the initial conditions of ter
                    ter.initialize(WIDTH, HEIGHT + 2, 0, 0);
                    ter.renderFrame(world);
                } else {
                    System.exit(0);
                }
            } else if (!skipMenu && (key == 'q' || key == 'Q')) {
                // quit game
                System.exit(0);
            } else if (!skipSeed && (key == 's' || key == 'S')) {
                // start game
                skipSeed = true;
                this.SEED = Long.parseLong(seed.toString());
                this.RANDOM = new Random(SEED);
                // restore the initial conditions
                ter.initialize(WIDTH, HEIGHT + 2, 0, 0);
                fillWithNothing(world);
                createWorld(world);
                worldExists = true;
                ter.renderFrame(world);
            } else if (!skipSeed && Character.isDigit(key)) {// seed
                seed.append(key);
                drawThing(seed.toString());
            } else if (skipMenu && key == ':') {
                quitMode = true;
            } else {
                // move the figure
                moveFigure(world, key, this.myPosition);
                quitMode = false;
            }
        }
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

        // ter.initialize(WIDTH, HEIGHT); The autograder cannot use StdDraw
        int option = getOption(input);

        switch (option) { // Enhanced switch created by IDE
            // new game
            case 0 -> {
                this.SEED = Long.parseLong(getSeed(input));
                this.RANDOM = new Random(SEED);
                fillWithNothing(world);
                createWorld(world);
                multipleMove(input);
                // deal with save and quit
                String regexSave = "(?i).*(:Q)$";
                if (input.matches(regexSave)) {
                    saveWorld(this);
                }
               // ter.renderFrame(world); The autograder cannot use StdDraw
            }
            // load game
            case 1 -> {
                // load game
                Engine engine = loadWorld();
                if (engine != null) {
                    // restore the loaded world
                    restoreWorld(engine);
                    multipleMove(input);
                    // deal with save and quit
                    String regexSave = "(?i).*(:Q)$";
                    if (input.matches(regexSave)) {
                        saveWorld(this);
                    }
                   // ter.renderFrame(world); The autograder cannot use StdDraw
                }
            }
        }
        return world;
    }

    public void multipleMove(String input) {
        // deal with motions
        String regexMove = "(?i)^(N[0-9]+S|L)([WASD]+).*";
        Pattern patternMove = Pattern.compile(regexMove);
        Matcher matcherMove = patternMove.matcher(input);
        if (matcherMove.find()) {
            // store sequence of motions
            String motion =  matcherMove.group(2);
            for (char key : motion.toCharArray()) {
                moveFigure(this.world, key, this.myPosition);
            }
        }
    }

    public int getOption(String input) {
        String regexNew = "(?i)^(N[0-9]+S).*";
        String regexLoad = "(?i)^L.*";
        if (input.matches(regexNew)) {
            return 0;
        }
        if (input.matches(regexLoad)) {
            return 1;
        }
        return -1; // Invalid input.
    }

    public String getSeed (String input) {
        String regex = "(?i)^N([0-9]+)S.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        // return seed
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // check if it is wall or locked door
    public boolean checkWall(TETile[][] tiles, Position p) {
        return tiles[p.x][p.y].character() == Tileset.WALL.character() ||
                tiles[p.x][p.y].character() == Tileset.LOCKED_DOOR.character();
    }

    // move the avatar one step (exchange tiles)
    public void walk(TETile[][] tiles, Position myPosition, Position nextPosition) {
        TETile myTile = tiles[myPosition.x][myPosition.y];
        TETile nextTile = tiles[nextPosition.x][nextPosition.y];
        //exchange
        tiles[myPosition.x][myPosition.y] = nextTile;
        tiles[nextPosition.x][nextPosition.y] = myTile;
        myPosition.x = nextPosition.x;
        myPosition.y = nextPosition.y;
      // ter.renderFrame(tiles); The autograder cannot use StdDraw, but needs to be uncommented to
        // correctly run interactWithKeyboard.
    }

    //moveFigure
    public void moveFigure(TETile[][] tiles, char key, Position myPosition) {
        if (key == 'w' || key =='W') {
            Position nextPosition = new Position(myPosition.x, myPosition.y + 1);
            if (!checkWall(tiles, nextPosition)) {
                walk(tiles, myPosition, nextPosition);
            }
        } else if (key == 'a' || key =='A') {
            Position nextPosition = new Position(myPosition.x - 1, myPosition.y);
            if (!checkWall(tiles, nextPosition)) {
                walk(tiles, myPosition, nextPosition);
            }
        } else if (key == 's' || key =='S') {
            Position nextPosition = new Position(myPosition.x, myPosition.y - 1);
            if (!checkWall(tiles, nextPosition)) {
                walk(tiles, myPosition, nextPosition);
            }
        } else if (key == 'd' || key =='D') {
            Position nextPosition = new Position(myPosition.x + 1, myPosition.y);
            if (!checkWall(tiles, nextPosition)) {
                walk(tiles, myPosition, nextPosition);
            }
        }
    }

    // draw Menu
    public void drawMenu() {
        StdDraw.clear(Color.black);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.75, "CS61B: THE GAME");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.5, "NEW GAME (N)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.35, "LOAD GAME (L)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.2, "QUIT (Q)");
        StdDraw.show();
    }

    // draw mouse pointer description
    public void mousePointer(String s) {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(3, 31, s);
        StdDraw.show();
    }

    // draw anything
    public void drawThing(String s) {
        StdDraw.clear(Color.black);
        Font font = new Font("Arial", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    // deal with positions
    public static class Position implements Serializable{
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
    public void drawSingleRoom(TETile[][] tiles, Room room) {
        for (int x = room.leftBottom.x; x <= room.rightBottom.x; x++) {
            for (int y = room.leftBottom.y; y <= room.leftUp.y; y++) {
                tiles[x][y] = Tileset.WALL;
            }
        }
        for (int x = room.leftBottom.x + 1; x <= room.rightBottom.x - 1; x++) {
            for (int y = room.leftBottom.y + 1; y <= room.leftUp.y - 1; y++) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    // test overlap
    public boolean testOverlap(TETile[][] tiles, Room room) {
        boolean overlap = false;
        for (int x = room.leftBottom.x; x <= room.rightBottom.x; x++) {
            for (int y = room.leftBottom.y; y <= room.leftUp.y; y++) {
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
            if (tiles[x0][y0 + 1].equals(Tileset.NOTHING)) {
                tiles[x0][y0 + 1] = Tileset.WALL;
            }
            if (tiles[x0][y0 - 1].equals(Tileset.NOTHING)) {
                tiles[x0][y0 - 1] = Tileset.WALL;
            }
            if (tiles[x0 + 1][y0].equals(Tileset.NOTHING)) {
                tiles[x0 + 1][y0] = Tileset.WALL;
            }
            if (tiles[x0 - 1][y0].equals(Tileset.NOTHING)) {
                tiles[x0 - 1][y0] = Tileset.WALL;
            }
            if (tiles[x0 + 1][y0 + 1].equals(Tileset.NOTHING)) {
                tiles[x0 + 1][y0 + 1] = Tileset.WALL;
            }
            if (tiles[x0 - 1][y0 - 1].equals(Tileset.NOTHING)) {
                tiles[x0 - 1][y0 - 1] = Tileset.WALL;
            }
            if (tiles[x0 + 1][y0 - 1].equals(Tileset.NOTHING)) {
                tiles[x0 + 1][y0 - 1] = Tileset.WALL;
            }
            if (tiles[x0 - 1][y0 + 1].equals(Tileset.NOTHING)) {
                tiles[x0 - 1][y0 + 1] = Tileset.WALL;
            }
        }
    }

    // create rooms
    public void createRoom(TETile[][] tiles, Room room) {
        if (!testOverlap(tiles, room)) {
            drawSingleRoom(tiles, room);
            int currCenterX = (room.rightBottom.x + room.leftBottom.x) / 2;
            int currCenterY = (room.leftUp.y + room.leftBottom.y) / 2;
            Position currCenter = new Position(currCenterX, currCenterY);
            if (prevCenter != null) {
                createHallways(tiles, prevCenter, currCenter);
            }
            prevCenter = currCenter;
            // only add really constructed rooms
            this.rooms.add(room);
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
            Room newRoom = new Room(leftUp, leftBottom, rightUp, rightBottom);
            if (rightUp.x < WIDTH && rightUp.y < HEIGHT) {
                createRoom(tiles, newRoom);
            }
        }
        // build a locked door
        Room roomWithDoor = rooms.get(RANDOM.nextInt(this.rooms.size()));
        tiles[(roomWithDoor.leftBottom.x + roomWithDoor.rightBottom.x) / 2][roomWithDoor.leftBottom.y] = Tileset.LOCKED_DOOR;
        // create the moving avatar
        Room roomWithAvatar = rooms.get(RANDOM.nextInt(this.rooms.size()));
        myPosition = new Position((roomWithAvatar.leftBottom.x + roomWithAvatar.rightBottom.x) / 2, (roomWithAvatar.leftBottom.y + roomWithAvatar.leftUp.y) / 2);
        tiles[myPosition.x][myPosition.y] = Tileset.AVATAR;
    }

    //load world
    public Engine loadWorld() {
        File f = new File("./world.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Engine loadWorld = (Engine) os.readObject();
                os.close();
                return loadWorld;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        return null;
    }

    // restore the saved world status
    public void restoreWorld(Engine engine) {
        this.SEED = engine.SEED;
        this.RANDOM = engine.RANDOM;
        this.rooms = engine.rooms;
        this.myPosition = engine.myPosition;
        this.ter = engine.ter;
        this.world = engine.world;
    }

    // save World
    public void saveWorld(Engine w) {
        File f = new File("./world.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(w);
            os.close();
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
