import java.util.Scanner;
import java.io.*;

public class IO
{
    
    private static Scanner s = new Scanner( System.in );

    public static void println(String str) {
        System.out.println(str);
    }
    
    public static void print(String str) {
        System.out.print(str);
    }
    
    public static int readInt() {
        while(true) {
            System.out.print("");
            String input = s.nextLine();
            try {
                int i = Integer.parseInt(input);
                return i;
            } catch(Exception e) {
                System.out.println("ERROR: " + input + " is not an integer.");
                
            }
        }
    }

    public static int readInt(String str) {
        while(true) {
            System.out.print(str);
            String input = s.nextLine();
            try {
                int i = Integer.parseInt(input);
                return i;
            } catch(Exception e) {
                System.out.println("ERROR: " + input + " is not an integer.");
                
            }
        }
    }
    
    public static double readDouble() {
        while(true) {
            System.out.print("");
            String input = s.nextLine();
            try {
                int i = Integer.parseInt(input);
                return i;
            } catch(Exception e) {
                System.out.println("ERROR: " + input + " is not a double.");
                
            }
        }
    }

    public static double readDouble(String str) {
        while(true) {
            System.out.print(str);
            String input = s.nextLine();
            try {
                double i = Double.parseDouble(input);
                return i;
            } catch(Exception e) {
                System.out.println("ERROR: " + input + " is not a double.");
                
            }
        }
    }
    
    public static String readLine() {
        System.out.print("");
        String input = s.nextLine();
        return(input);
    }
    
    public static String readLine(String str) {
        System.out.print(str);
        String input = s.nextLine();
        return(input);
    }
    
    public static boolean readBoolean() {
        while(true) {
            System.out.print("");
            String input = s.nextLine();
            try {
                boolean i = Boolean.parseBoolean(input);
                return i;
            } catch(Exception e) {
                System.out.println("ERROR: " + input + " is not a boolean.");
                
            }
        }
    }

    public static boolean readBoolean(String str) {
        while(true) {
            System.out.print(str);
            String input = s.nextLine();
            try {
                boolean i = Boolean.parseBoolean(input);
                return i;
            } catch(Exception e) {
                System.out.println("ERROR: " + input + " is not a boolean.");
                
            }
        }
    }
    
}
