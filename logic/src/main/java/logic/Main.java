package logic;
import java.util.Scanner;

/**
 * Class Main spouští celý program
 */
public class Main {
    /**
     * Funkce main vypíše pravidla hry, a vytvoří instanci class Game
     * Pokud chce hráč spustit hru znova, zadá 0 a může hrát novou hru
     * @param args argumenty programu zadané z příkazové řádky
     */
    public static void main(String[] args) {
        System.out.println("Vítejte u hry LOGIC");
        System.out.println("""
                Pravidla hry:
                  hráč A vybere kód  a hráč B se ho snaží uhodnout,
                  hráč A odhady hráče B hodnotí černými a bílými body,
                    černý bod = správné číslo na spravném místě
                    bilý bod = odhad obsahuje spravné číslo, ale je chybně umístěno
                Hra má tři módy:
                    MOD1 = program vybere kód a uživatel ho hádá
                    MOD2 = uživatel si vybere kód a program ho hádá
                    MOD3 = hrají dva uživatelé proti sobě""");
        String newGame = "0";
        Scanner scanner = new Scanner(System.in);
        while (newGame.equals("0")) {
            Game game = new Game(args);
            game.play();
            System.out.println("Pokud si chcete zahrát znovu stiskněte 0, jinou klávesou hru ukončíte.");
            newGame = scanner.nextLine();
        }
        scanner.close();
    }
}