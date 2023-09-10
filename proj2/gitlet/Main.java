package gitlet;

import java.io.IOException;
import java.util.Arrays;

import static gitlet.Repository.init;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args == null) {
            System.out.println("What is your command?");
            System.exit(0);
        }

        Repository myRepository = new Repository();

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                init();
                break;
            case "add":
                myRepository.add(args[1]);
                break;
            case "commit":
                myRepository.commit(args[1]);
                break;
            case "checkout":
                myRepository.checkout(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "log":
                myRepository.log();
                break;
        }
    }
}
