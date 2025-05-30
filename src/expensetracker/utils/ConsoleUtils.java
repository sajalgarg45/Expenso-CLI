package expensetracker.utils;

public class ConsoleUtils {
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    public static void printBox(String[] lines) {
        int w = 0;
        for (String l : lines) w = Math.max(w, l.length());
        printLine(w);
        for (String l : lines) {
            System.out.printf("| %-" + w + "s |\n", l);
        }
        printLine(w);
    }
    private static void printLine(int w) {
        System.out.print('+');
        for (int i = 0; i < w + 2; i++) System.out.print('-');
        System.out.println('+');
    }
    public static void printGraph(double spent, double total) {
        int maxBar = 30;
        int spentBar = (total == 0) ? 0 : (int)(spent / total * maxBar);
        System.out.print("[Spent] ");
        for (int i = 0; i < spentBar; i++) System.out.print('#');
        System.out.println();
        System.out.print("[Left ] ");
        for (int i = 0; i < maxBar - spentBar; i++) System.out.print('#');
        System.out.println();
    }
}