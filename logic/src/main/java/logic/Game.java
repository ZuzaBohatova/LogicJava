package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class Game je hlavní třída celé hry, inicalizuje hru, řídí její průběh
 */
public class Game {
    /**
     * Konstruktor Class Game
     * inicializuje celou hru a nastaví parametry hry
     * @param args argumenty zadané z příkazové řádky
     */
    protected Game(String[] args){
        initialise(args);
    }
    /**
     * nejvyšší možné číslo v hádaném kódu
     */
    private int highestNumber;
    /**
     * délka hádaného kódu
     */
    private int combinationLength;
    /**
     * maximální počet kroků k uhádnutí kódu
     */
    private int maxSteps;
    /**
     * mód hry, který si hráč vybral
     */
    private int gameMode;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * funkce, která inicializuje každou hru a nastaví její parametry - mód hry, nejvyšší číslo v kódu, délku kódu a maximální počet kroků
     * kontroluje validitu zadaných parametrů hry
     * @param args argumenty hry zadané z příkazové řádky
     */
    private void initialise(String[] args) {
        System.out.println("Zvolte si mód hry: 1 - MOD1, 2 - MOD2, 3 - MOD3:");
        String mod = scanner.nextLine();
        gameMode = setNumber(mod, 1, 3, "mód hry");
        String[] setting = {"-","-","-"};
        System.arraycopy(args, 0, setting, 0, args.length);
        combinationLength = setNumber(setting[0], 2, 7, "délka hádané kombinace");
        highestNumber = setNumber(setting[1], 1, 9, "nejvyšší číslo v kombinaci");
        maxSteps = setNumber(setting[2], 5, 15, "maximální počet kroků");
        System.out.println("\nHádaný kód obsahuje ČÍSLA OD 0 DO "+ highestNumber +", jeho DÉLKA je "
                +combinationLength+" a je "+ maxSteps +" KROKŮ na jeho uhádnutí\n");
    }

    /**
     * Spouští hru, podle toho, který mód si hráč vybral
     */
    protected void play(){
        switch (gameMode) {
            case 1 -> playMode1();
            case 2 -> playMode2();
            case 3 -> playMode3();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("KONEC HRY\n");
    }

    /**
     * zahajuje hru pro mód 1 - program generuje kód a uživatel hádá
     * vytváří jednotlivé hráče a moderuje a ukončuje hru
     */
    private void playMode1() {
        System.out.println("Pokud budete potřebovat nápovědu, tak místo odhadu zadejte 1");
        int steps = 1;
        int[] reaction = new int[2];
        List<Integer> guess;
        int[] code;
        User guessingPlayer = new User(highestNumber, combinationLength);
        Machine evaluatingPlayer = new Machine(highestNumber,combinationLength, false);
        code = evaluatingPlayer.generateCode();
        System.out.println("\nMám vybraný kód, můžete začít hádat.");
        while(reaction[0] != combinationLength){
            if(steps == maxSteps){
                System.out.println("Tentokrát to nevyšlo... Překročil jsi maximální počet kroků!");
                System.out.println("Správný kód byl: "+ guessToString(IntStream.of(code).boxed().collect(Collectors.toList())));
                return;
            }

            System.out.print(steps+". odhad: ");
            guess = guessingPlayer.guess(gameMode);
            if(guess == null){
                if(evaluatingPlayer.giveAHint(code)){
                    System.out.println("Nyní už znáte celý kód.\n");
                    return;
                }
                continue;
            }
            reaction = Machine.evaluate(guess, code);
            System.out.println(steps+". reakce: ");
            System.out.println("   Černé:" +reaction[0]);
            System.out.println("   Bílé: "+reaction[1]+"\n");
            steps++;
        }
        System.out.println("VYHRÁLI JSTE!");
    }

    /**
     * spouští hru pro mód 2 - uživatel vybírá kód a program hádá
     * vytváří jednotlivé hráče, kontroluje a ukončuje hru
     */
    private void playMode2() {
        int steps = 0;
        int[] reaction = new int[2];
        List<Integer> guess = new ArrayList<>(combinationLength);
        Machine guessingPlayer = new Machine(highestNumber, combinationLength, true);
        User evaluatingPlayer = new User(highestNumber,combinationLength);
        evaluatingPlayer.generateCode();
        while(reaction[0] != combinationLength){
            if(steps == maxSteps){
                System.out.println("\nGRATULUJI, VYHRÁL JSI! Překročil jsem maximální počet kroků...");
                return;
            }
            steps++;
            guess = guessingPlayer.guess(steps, reaction, guess);
            if(guess == null){
                break;
            }
            System.out.println(steps+". odhad: "+ guessToString(guess));
            System.out.println(steps+". reakce: ");
            reaction = evaluatingPlayer.evaluate();
        }
        System.out.println("JSEM VÍTĚZ!");
    }

    /**
     * spouští hru pro mód 3 - hrajou dva uživatelé proti sobě
     * vytváří hráče, načítá kód a moderuje a ukončuje hru
     */
    private void playMode3() {
        int steps = 0;
        int[] reaction = new int[2];
        String code;
        User guessingPlayer = new User(highestNumber, combinationLength);
        User evaluatingPlayer = new User(highestNumber,combinationLength);

        code = evaluatingPlayer.readCode();
        while(reaction[0] != combinationLength){
            if(steps == maxSteps){
                System.out.println("\nGRATULUJI, HRÁČ A VYHRÁL! Hráč B překročil maximální počet kroků...");
                System.out.println("Hledaný kód byl "+code);
                return;
            }
            steps++;
            System.out.print(steps+". odhad hráče B: ");
            guessingPlayer.guess(gameMode);
            System.out.println(steps+". reakce hráče A: ");
            reaction = evaluatingPlayer.evaluate();
        }
        System.out.println("GRATULUJI, HRÁČ B VYHRÁL!");
    }

    /**
     * Načítá zadané parametry pro hru a kontroluje zda jsou validní
     * pokud validní nejsou, požádá hráče o jejich opětovné zadání
     * @param arg argumenty programu z příkazové řádky
     * @param min minimální hodnota, které musí zadaný parametr dosahovat
     * @param max maximální hodnota, které může načítaný parametr dosahovat
     * @param name jméno parametru
     * @return vrací zkontolovaný validní parametr
     */
    private int setNumber(String arg, int min, int max, String name){
        do {
            try {
                int number = Integer.parseInt(arg);
                if (number > max || number < min) {
                    throw new NumberFormatException();
                }
                return number;      //pokud je číslo ve správné formátu, vrátím ho
            } catch (NumberFormatException e) {
                if (!"-".equals(arg)) {
                    System.out.println("Parametr " + name + " byl zadán chybně.");
                }
            }
            System.out.println("Zadejte parametr " + name + ", číslo od " + min + " do " + max + ":");
            arg = scanner.nextLine();
        } while (true);
    }

    /**
     * převádí odhad na String
     * @param guess zadaný odhad od programu
     * @return vrací odhad převedený na souvislý String z [0, 1, 2] -> 012
     */
    private String guessToString(List<Integer> guess){
        StringBuilder output = new StringBuilder();
        for (Integer integer : guess) {
            output.append(integer.toString());
        }
        return output.toString();
    }
}
