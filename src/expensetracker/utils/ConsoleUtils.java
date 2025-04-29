package expensetracker.utils;

public class ConsoleUtils {
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printBox(String[] lines) {
        int width = 0;
        for (String l : lines) width = Math.max(width, l.length());
        printLine(width);
        for (String l : lines) {
            System.out.printf("| %-" + width + "s |\n", l);
        }
        printLine(width);
    }

    private static void printLine(int w) {
        System.out.print('+');
        for (int i = 0; i < w + 2; i++) System.out.print('-');
        System.out.println('+');
    }

    public static void printGraph(double spent, double total) {
        int maxBar = 30;
        int spendBar = (total == 0) ? 0 : (int)(spent / total * maxBar);
        System.out.print("[Spent]");
        for (int i = 0; i < spendBar; i++) System.out.print("#");
        System.out.println();
        System.out.print("[Left ]");
        for (int i = 0; i < maxBar - spendBar; i++) System.out.print("#");
        System.out.println();
    }
}
