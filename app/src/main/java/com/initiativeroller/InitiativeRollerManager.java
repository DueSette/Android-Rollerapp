package com.initiativeroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class InitiativeRollerManager
{
    public static List<Adventurer> unrolledCharList = new ArrayList<>();

    public static List<Adventurer> rolledCharList = new ArrayList<>();

    public static MainActivity mainAct;

    InitiativeRollerManager(MainActivity main)
    {
        mainAct = main;
    }

    Random rand = new Random();

    public List<Adventurer> RollInitiative()
    {
        rolledCharList.clear();
        rolledCharList.addAll(unrolledCharList);

        for (int i = 0; i < rolledCharList.size(); i++)
        {
            int roll;

            if(rolledCharList.get(i)._advantage)
                roll = AdvantageRoll();
            else
                roll = NormalRoll();

           roll += rolledCharList.get(i)._rollMod;
           rolledCharList.get(i)._rollScore = roll;
        }

        InsertionSort(rolledCharList);

        return rolledCharList;
    }

    private List<Adventurer> InsertionSort(List<Adventurer> arr)
    {
        int n = arr.size();

        for (int i = 0; i < n; i++)
        {
            int key = arr.get(i)._rollScore;
            String keySt = arr.get(i)._name;
            int keyRollMod = arr.get(i)._rollMod;

            int j = i - 1;

            /* Move elements of arr[0..i-1], that are greater than key, to one position ahead of their current position */

            while (j >= 0 && arr.get(j)._rollScore > key)
            {
                arr.get(j + 1)._rollScore = arr.get(j)._rollScore;
                arr.get(j + 1)._name = arr.get(j)._name;
                arr.get(j + 1)._rollMod = arr.get(j)._rollMod;
                j = j - 1;
            }
            arr.get(j + 1)._rollScore = key;
            arr.get(j + 1)._name = keySt;
            arr.get(j + 1)._rollMod = keyRollMod;
        }

        Collections.reverse(arr);
        return arr;
    }

    private int AdvantageRoll()
    {
        int roll = rand.nextInt(20);
        int roll2 = rand.nextInt(20);

        int result = Math.max(roll, roll2);

        result++;
        return result;
    }

    private int NormalRoll()
    {
        int roll = rand.nextInt(20);

        roll++;
        return roll;
    }

    public void AddCharToRolledList( Adventurer adv)
    {
        int roll = 0;

        if(adv._advantage)
            roll = Math.abs(AdvantageRoll());
        else
            roll = Math.abs(NormalRoll());

        adv._rollScore = roll + adv._rollMod;

        rolledCharList.add(adv);
        InsertionSort(rolledCharList);
    }

    static void CancelFromUnrolledList(int position)
    {
        unrolledCharList.remove(position);
        mainAct.SetUnrolledCombatantsList(unrolledCharList);
    }
}
