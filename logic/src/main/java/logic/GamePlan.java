package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class GamePlan, její instance je vytvořena v případě, že je program hráčem, který hádá kód
 * vygeneruje všechny možnosti a řídí vyhledávání validních možností
 */
public class GamePlan {
    /**
     * všechny možnosti hádaného kódu pro zadané parametry programu
     */
    private int[][] possibilities;
    /**
     * délka zadaného kódu
     */
    private final int combinationLength;

    /**
     * kolik z možností může být naším hledaným kódem
     */
    private int remainingPossibilities;
    /**
     * maximální číslo v kódu
     */
    private final int highestNumber;
    /**
     * aktuální index prohledávání
     */
    private int currentIndex = 0;

    /**
     * Kostruktor GamePlan, nastaví počet možností (remainingPossibilities) podle zadaného nejvyššího čísla a délky kombinace
     * sestaví pole všech možností - possibilities
     * @param highestNumber nejvyšší možné číslo v kódu
     * @param combinationLength délka hádaného kódu
     */
    public GamePlan(int highestNumber, int combinationLength) {
        this.remainingPossibilities =  (int) Math.pow(highestNumber+1, combinationLength); // vypocitame pocet variací
        this.combinationLength = combinationLength;
        this.highestNumber = highestNumber;
        setPossibilities();
    }

    /**
     * Vyvoří všechny možnosti, které mohou být hledaným kódem
     */
    private void setPossibilities() {
        possibilities = new int[remainingPossibilities][combinationLength];
        generateVariationsHelper(possibilities, new int[combinationLength], 0);

    }

    /**
     * Pomocná rekurzivní funkce k vygenerovaní všech možných variací kódu
     * @param result pole možností
     * @param current aktuální možnost
     * @param index index v poli current
     */
    private void generateVariationsHelper(int[][] result, int[] current, int index) {
        if (index == current.length) {
            System.arraycopy(current, 0, result[currentIndex], 0, current.length);
            currentIndex++;
            return;
        }
        for (int i = 0; i <= highestNumber; i++) {
            current[index] = i;
            generateVariationsHelper(result, current, index + 1);
        }
    }

    /**
     * Zjišťuje zda podle dosavadních reakcí a odhadů existují ještě nějaké možnosti pro hledaný kód
     * Pokud ano, vrací nový odhad, pokud ne, vrací null
     * @param reaction poslední reakce uživatele na poslední odhad
     * @param guess poslední odhad
     * @return nový odhad nebo null
     */
    public List<Integer> ifExistAnotherOptionsReturnGuess(int[] reaction, List<Integer> guess) {
        int wr = 0;  // kam piseme v poli moznosti

        if (remainingPossibilities == 0) { // zadne moznosti - neni reseni, uzivatel nekde udelal chybu ve vyhodnoceni
            System.out.println("Nejsou žádné další možnosti, někde ve vašich reakcích byla chyba\n");
            return null;
        } else {
            for (int i = 0; i < remainingPossibilities; i++) {
                int[] kod = new int[combinationLength];

                System.arraycopy(possibilities[i], 0, kod, 0, combinationLength);

                int[] vyhodnoceni = Machine.evaluate(new ArrayList<>(guess), kod); // vyhodnotime odhad oproti moznemu kodu

                if (reaction[1] == vyhodnoceni[1] && reaction[0] == vyhodnoceni[0]) { // pokud sedi ohodnoceni - muze to byt hledany kod
                    // prepis - realne moznosti posouvame nahoru
                    System.arraycopy(possibilities[i], 0, possibilities[wr], 0, combinationLength);

                    wr++; // kam piseme se posune o radek dal
                }
            }

            remainingPossibilities = wr; // zmensuje se pocet moznosti

            if (remainingPossibilities == 0) { // neni zadna dalsi moznost
                System.out.println("Nejsou zadne dalsi moznosti, nekde ve vasich reakcich je chyba");
                return null;
            } else {
                Random r = new Random();
                int index = r.nextInt(remainingPossibilities);
                guess.clear();
                for (int i = 0; i < combinationLength; i++) {
                    guess.add(possibilities[index][i]);
                }
            }
        }
        return guess;
    }
}