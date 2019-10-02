package com.initiativeroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class PartyListsRecyclerAdapter extends RecyclerView.Adapter<PartyListsRecyclerAdapter.PartyListsViewHolder> {

    private Context mCtx;
    private List<File> partyLists;

    //CONSTRUCTOR
    public PartyListsRecyclerAdapter(Context c, List<File> parties)
    {
        this.mCtx = c;
        this.partyLists = parties;

        for(int i = 0; i < parties.size(); i++)
            if (!partyLists.get(i).getName().contains("_party"))
            {
                partyLists.remove(i);
                i--;
            }
    }

    //Creates Inflater and Returns Holder (this allows us to modify whatever layout we are working with, in this case the recycler view)
    @NonNull
    @Override
    public PartyListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.load_dialog_party_lists_recycler_view, null);
        return new PartyListsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PartyListsViewHolder holder, int position) {

        final File partyList = partyLists.get(position);

        String partyName = partyList.getName().replace("_party", "");
        holder.partyEntryBtn.setText(partyName);

        holder.cancelPartyEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.card.setVisibility(View.GONE);
                partyList.delete();
                MainActivity.instance.SetPartyListsAdapter();

            }
        });

        holder.partyEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitiativeRollerManager.mainAct.LoadPartyFromFiles(partyList.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return partyLists.size();
    }

    class PartyListsViewHolder extends RecyclerView.ViewHolder
    {
        ConstraintLayout card;
        Button partyEntryBtn;
        Button cancelPartyEntryBtn;

        public PartyListsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            card = itemView.findViewById(R.id.partyListsConstraintLayout);

            partyEntryBtn = itemView.findViewById(R.id.partyNameButton);
            cancelPartyEntryBtn = itemView.findViewById(R.id.cancelPartyListFromFiles);
        }
    }
}
