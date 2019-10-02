package com.initiativeroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RolledAdventurerRecyclerAdapter extends RecyclerView.Adapter<RolledAdventurerRecyclerAdapter.RolledAdventurerViewHolder>
{
    private Context mCtx;

    List<Adventurer> adventurers;

    //CONSTRUCTOR
    public RolledAdventurerRecyclerAdapter(Context c, List<Adventurer> advents)
    {
        this.adventurers = advents;
        this.mCtx = c;
    }

    //Creates inflater (the class that can change what modular views like RecyclerView look like)
    @NonNull
    @Override
    public RolledAdventurerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.rolled_combatants_recycler_view, null);
        return new RolledAdventurerViewHolder(view); // Need to create - in this case - a "rolled adventurer" view holder class
    }

    //WHAT TO DISPLAY IN ALL KNOWN FIELDS AT THE MOMENT OF CREATION
    //ALL THE FUNCTIONS RELATED TO OBJECTS IN THE VIEW ALSO GO HERE
    @Override
    public void onBindViewHolder(@NonNull final RolledAdventurerViewHolder holder, final int position)
    {

    Adventurer adventurer = adventurers.get(position);
    holder.characterNameTextView.setText(adventurer._name);

    String rollScoreText = adventurer._rollScore +
            " ("
            + (adventurer._rollScore - adventurer._rollMod)
            + (adventurer._rollMod >= 0 ? " + " : " - ")
            + Math.abs(adventurer._rollMod)
            + ")";

    holder.characterRollInfoTextView.setText(rollScoreText);

    //Kill Character
    holder.cancelButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            //holder.card.setVisibility(View.GONE);
            adventurers.remove(position);
            InitiativeRollerManager.mainAct.SetRolledCombatantsList(adventurers);
        }
    });
    }

    //FEED SIZE OF WHATEVER LIST OR ARRAY THIS RECYCLERVIEW IS WORKING WITH
    @Override
    public int getItemCount()
    {
        return adventurers.size();
    }

    //====THIS MAKES THIS CLASS AWARE OF WHAT'S IN EVERY INSTANCE OF THE VIEW
    //WE NEED TO ADD A VIEW HOLDER CLASS EVERY TIME WE USE AN ADAPTER
    class RolledAdventurerViewHolder extends RecyclerView.ViewHolder
    {
        ConstraintLayout card;
        TextView characterNameTextView;
        TextView characterRollInfoTextView;
        Button cancelButton;

        public RolledAdventurerViewHolder(@NonNull View itemView)
        {
            super(itemView);
            card = itemView.findViewById(R.id.rolledConstraintLayout);

            characterNameTextView = itemView.findViewById(R.id.characterNameTextView);
            characterRollInfoTextView = itemView.findViewById(R.id.characterRollInfoTextView);
            cancelButton = itemView.findViewById(R.id.cancelFromRolledList);
        }
    }
}
