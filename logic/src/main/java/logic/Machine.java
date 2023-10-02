package logic;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class Machine implementuje hráče, za kterého hraje program
 */
public class Machine {
    /**
     * kontruktor class Machine
     * pokud je parametr evaluating true, vytváří hrací plán
     * @param highestNumber nejvyšší možné číslo v kódu
     * @param combinationLength délka kódu
     * @param guessing true, pokud je machine hádající hráč, false, pokud není
     */
    protected Machine(int highestNumber, int combinationLength, boolean guessing) {
        this.highestNumber = highestNumber;
        Machine.combinationLength = combinationLength;
        if(guessing) {
            gamePlan = new GamePlan(highestNumber, combinationLength);
        }
    }

    /**
     * hrací plán pomocí kterého může machine vytvářet své odhady
     */
    private GamePlan gamePlan;
    /**
     * nejvyšší číslo v kódu
     */
    private final int highestNumber;
    /**
     * délka kódu
     */
    private static int combinationLength;
    /**
     * nápověda pro uživatele, do které se postupně ukládají napovězená čísla
     */
    private final List<Integer> hint = new ArrayList<>();

    /**
     * Generuje náhodný kód podle highestNumber a combinationLength
     * @return vygenerovaný kód s délkou = combinationLength
     */
    protected int[] generateCode() {
        int[] combination = new int[combinationLength];
        Random r = new Random();
        for (byte i = 0; i < combinationLength; ++i) {
            combination[i] = r.nextInt(0,highestNumber + 1);
        }
        return combination;
    }

    /**
     * Hodnotí odhad zadaný uživatelem, porovnává ho s vybraným kódem a vrací černé a bílé body
     * @param guess odhad uživatele
     * @param code vybraný hádaný kód programu
     * @return pole int[] reaction = [počet černých bodů, počet bílých bodů]
     */
    protected static int[] evaluate(List<Integer> guess, int[] code){
        int[] copyCombination = new int[combinationLength];
        System.arraycopy(code, 0, copyCombination, 0, combinationLength);
        int[] reaction = new int[2];

        for (byte i = 0; i < combinationLength; ++i) { //pripocitava cerne body - spravne cislo na spravne pozici
            if (copyCombination[i] == guess.get(i)) {
                ++reaction[0];
                copyCombination[i] = -1;
                guess.set(i, -1);
            }
        }

        guess.removeAll(Collections.singleton(-1));

        for (byte i = 0; i < combinationLength; ++i) { //pripocitava bile body - spatne cislo na spravne pozici
            int index = guess.indexOf(copyCombination[i]);
            if (index != -1) {
                guess.remove(index);
                copyCombination[i] = -1;
                ++reaction[1];
            }
        }
        return reaction; //vraci pole obsahujici pocet cernych a pocet bilych bodu
    }

    /**
     * Vytváří odhad programu pro hledaný kód, který si vybral uživatel
     * @param steps počet kroků hry, které už byly odehrány
     * @param reaction poslední reakce uživatele na poslední odhad
     * @param guess poslední odhad programu
     * @return nový odhad
     */
    protected List<Integer> guess(int steps, int[] reaction, List<Integer> guess){
        if (steps == 1)      //pokud hrajeme teprve kolo 1, program musi nejprve vygenerovat hadany kod vuci kteremu pak bude hodnotit
        {
            guess = Arrays.stream(generateCode()).boxed().collect(Collectors.toList());
        }
        else {
            guess = gamePlan.ifExistAnotherOptionsReturnGuess(reaction, guess);
        }
        return guess;
    }

    /**
     * Pokud hráč místo odhadu zadá 1, zavolá se funkce nápovědy a ta mu napoví jedno další číslo
     * Pokud již nemáme co napovídat -> nápověda je stejně dlouhá jako hledaný kód, vracíme true a hra už déle nepokračuje
     * @param code hádaný kód
     * @return true - již nemáme co napovídat , false - uživatel ještě nezná celý kód, hra může pokračovat
     */
    protected boolean giveAHint(int[] code){
        System.out.print("Nápověda: ");
        hint.add(code[hint.size()]);
        //pridava dalsi cisla do napovedy
        for (Integer integer : hint) {
            System.out.print(integer);
        }
        for (int i = combinationLength; i > hint.size(); --i)
        {
            System.out.print("-");         //cisla, ktera uzivateli jeste nebyla prozrazena nahradi pomlckou
        }

        System.out.println();
        return hint.size() == combinationLength;
    }
}
