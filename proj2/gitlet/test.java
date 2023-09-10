package gitlet;

import java.util.HashMap;
import java.util.HashSet;

public class test {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        HashSet<Integer> a = new HashSet<>();
        a.add(1);
        a.add(2);
        a.add(3);
        HashSet<Integer> b = new HashSet<>(a);
        a.add(4);
        System.out.println(b.toString());
    }

}