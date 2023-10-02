package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class User implementuje uživatele, jak hádajícího, tak hodnotícího hráče
 */
public class User {
    /**
     * konstruktor pro class User
     * @param highestNumber nejvyšší možné číslo v kódu
     * @param combinationLength délka hádaného kódu
     */
    protected User(int highestNumber, int combinationLength) {
        this.highestNumber = highestNumber;
        this.combinationLength = combinationLength;
    }

    /**
     * nejvyšší možné číslo v kódu
     */
    private final int highestNumber;
    /**
     * délka hádaného kódu
     */
    private final int combinationLength;

    private final Scanner scanner = new Scanner(System.in);

    /**
     * požádáme hráče, aby si vymyslel vlastní kód a dáme mu chvíli čas na rozmyšlenou
     */
    protected void generateCode(){
        System.out.println("\nNyní si vyber svůj kód.");
        System.out.println("  Může obsahovat jen čísla od 0 do "+highestNumber+" včetně.");
        System.out.println("  Délka kódu je "+combinationLength+"\n");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * * načítáme odhad uživatele a kontrolujeme zda je validní
     * @param mode mód hry
     * @return odhad uživatele
     */
    protected List<Integer> guess(int mode){
       String input = scanner.nextLine();
       if(input.equals("1") && mode != 3){
           return null;
       }
       while(!(input.matches("[0-"+highestNumber+"]+") && input.length() == combinationLength)){
           System.out.println("  Tvůj odhad nebyl zadán správně, může obsahovat jen čísla od 0 do "+highestNumber+" a mít délku "+combinationLength);
           System.out.println("  Zkus to znovu:");
           input = scanner.nextLine();
       }
       List<Integer> guess = new ArrayList<>(combinationLength);
       for(int i = 0; i < combinationLength; i++){
           guess.add(Integer.parseInt(String.valueOf(input.charAt(i))));
       }
       return guess;
    }

    /**
     * uživatel hodnotí odhad programu, zároveň kontrolujeme zda je reakce uživatele validní
     * @return reakce uživatele - [počet černých bodů, počet bílých bodů]
     */
    protected int[] evaluate(){
        int[] reaction = new int[2];
        boolean validReaction = false;
        String input;

        while (!validReaction) {
            System.out.print("   Černé: ");
            input = scanner.nextLine();
            boolean successParsing = false;

            try {
                reaction[0] = Integer.parseInt(input);
                successParsing = true;
            } catch (NumberFormatException ex) {
                System.out.println("Nezadali jste číslo, zkuste to znovu:");
            }

            while (!successParsing) {
                System.out.println("Nezadali jste číslo, zkuste to znovu:");
                System.out.print("   Černé: ");
                input = scanner.nextLine();

                try {
                    reaction[0] = Integer.parseInt(input);
                    successParsing = true;
                } catch (NumberFormatException ex) {
                    System.out.println("Nezadali jste číslo, zkuste to znovu:");
                }
            }

            System.out.print("   Bílé: ");
            input = scanner.nextLine();
            successParsing = false;

            try {
                reaction[1] = Integer.parseInt(input);
                successParsing = true;
            } catch (NumberFormatException ex) {
                System.out.println("Nezadali jste číslo, zkuste to znovu:");
            }

            while (!successParsing) {
                System.out.println("Nezadali jste číslo, zkuste to znovu:");
                System.out.print("   Černé: "+reaction[0]);
                System.out.print("   Bílé: ");
                input = scanner.nextLine();

                try {
                    reaction[1] = Integer.parseInt(input);
                    successParsing = true;
                } catch (NumberFormatException ex) {
                    System.out.println("Nezadali jste číslo, zkuste to znovu:");
                }
            }

            System.out.println();
            validReaction = true;

            if (reaction[0] + reaction[1] > combinationLength || reaction[0] + reaction[1] < 0) {
                System.out.println("Součet černých a bílých bodů nemůže být vyšší než "+combinationLength+" nebo menší než 0.");
                validReaction = false;
            }
            if (reaction[0] > combinationLength || reaction[0] < 0 || reaction[1] > combinationLength || reaction[1] < 0) {
                System.out.println("Počet černých nebo bílých bodů nemůže být vyšší než "+combinationLength+" nebo menší než 0.");
                validReaction = false;
            }
            if (reaction[0] == combinationLength-1 && reaction[1] == 1) {
                System.out.println("Tato kombinace černých a bílých bodů není možná!");
                validReaction = false;
            }
            if (!validReaction) {
                System.out.println("V tvojí reakci je chyba, zkus to znovu:");
            }
        }
        return reaction;
    }

    /**
     * načte kód, který uživatel uložil do .txt dokumentu
     * uživatel zadá cestu k souboru, pokud soubor neexistuje, je požádán, aby akci zopakoval
     * pokud zadaný kód není validní - opět se celá akce opakuje
     * @return načtený kód
     */
    protected String readCode() {
        while(true) {
            System.out.println("Hráč A zadá cestu k souboru, ve kterém je uložený hádaný kód:");
            String filePath = scanner.nextLine();
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("  Zadaný soubor " + filePath + " neexistuje, zkus to znovu");
            } else {
                try {
                    Scanner sc = new Scanner(file);
                    if(sc.hasNext()) {
                        String input = sc.nextLine();
                        if (!(input.matches("[0-" + highestNumber + "]+") && input.length() == combinationLength)) {
                            System.out.println("  Kód nebyl zadán správně, může obsahovat jen čísla od 0 do " + highestNumber + " a mít délku " + combinationLength);
                            System.out.println("  Změň kód v souboru a zadej ho znovu");
                        }
                        System.out.println("Tvůj kód byl úspěšně zadán.\n");
                        return input;
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("  Soubor nebyl nalezen");
                }
            }
        }
    }
}
