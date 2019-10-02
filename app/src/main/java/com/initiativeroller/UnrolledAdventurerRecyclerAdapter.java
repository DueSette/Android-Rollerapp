package com.initiativeroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UnrolledAdventurerRecyclerAdapter extends RecyclerView.Adapter<UnrolledAdventurerRecyclerAdapter.AdventurerViewHolder>
{
    private List<Adventurer> adventurers;
    private Context mCtx;

    public UnrolledAdventurerRecyclerAdapter(Context c, List<Adventurer> advents)
    {
        this.adventurers = advents;
        this.mCtx = c;
    }

    @NonNull
    @Override
    public AdventurerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.unrolled_combatants_recycler_view, null);
        return new AdventurerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdventurerViewHolder holder, final int position) {

    final Adventurer adventurer = adventurers.get(position);
    holder.characterInfoTextView.setText(adventurer._name +": (" + adventurer._rollMod + ")" + (adventurer._advantage ? "  ADV" : ""));

    //EditButton
    holder.editButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            InitiativeRollerManager.mainAct.OpenEditCharDialog(adventurer, position).show();
        }
    });

    //CancelButton
    holder.cancelButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            InitiativeRollerManager.CancelFromUnrolledList(position);
        }
    });
    }

    @Override
    public int getItemCount() {
        return adventurers.size();
    }

    class AdventurerViewHolder extends RecyclerView.ViewHolder
    {
        TextView characterInfoTextView;
        Button cancelButton;
        Button editButton;

        private AdventurerViewHolder(@NonNull View itemView)
        {
            super(itemView);

            characterInfoTextView = itemView.findViewById(R.id.characterNameTextView);
            cancelButton = itemView.findViewById(R.id.cancelFromUnrolledList);
            editButton = itemView.findViewById(R.id.editCharButton);
        }
    }
}
