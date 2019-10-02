package com.initiativeroller;

public class Adventurer
{
    String _name;
    int _rollMod;
    boolean _advantage;

    public int _rollScore = 0;

    public Adventurer(String name, int rollMod, boolean adv, int rollScore)
    {
        _name = name;
        _rollMod = rollMod;
        _advantage = adv;
        _rollScore = rollScore;
    }

    public static Adventurer ParseAdventurer (String name, String init, boolean adv)
    {
        if(init.contains("ADV"))
            adv = true;

        Adventurer adventurer = new Adventurer(name, Integer.parseInt(init), adv, 0);
        return adventurer;
    }
}