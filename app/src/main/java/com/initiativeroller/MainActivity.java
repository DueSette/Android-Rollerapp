package com.initiativeroller;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    InitiativeRollerManager rollMngr = new InitiativeRollerManager(this);

    AdView mAdView;
    RecyclerView unrolledRecyclerView, rolledRecyclerView;
    Button rollBtn, cancelFromListBtn, clearBtn, openCharMenuBtn, savePartyListBtn, loadPartyListBtn;
    ConstraintLayout toolbarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CreateSingleton();
        ManageReferences();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //ROLL INITIATIVE
        rollBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(rollMngr.RollInitiative().size() == 0)
                    Toast.makeText(getApplicationContext(), "There are no characters in the Combatants List!", Toast.LENGTH_SHORT).show();
                SetRolledCombatantsList(rollMngr.RollInitiative());
            }
        });

        //CLEAR LIST
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitiativeRollerManager.unrolledCharList.clear();
                SetUnrolledCombatantsList(InitiativeRollerManager.unrolledCharList);
            }
        });

        //BRING UP ADDITIONAL CHARACTER MENU
        openCharMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCharacterDialogue().show();
            }
        });

        //SAVE DIALOG
        savePartyListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenSaveDialog().show();
            }
        });

        //LOAD DIALOG
        loadPartyListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenLoadDialog().show();
            }
        });

        List<Adventurer> advs = new ArrayList<>();
        SetRolledCombatantsList(advs);
        SetUnrolledCombatantsList(advs);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        SetRolledCombatantsList(InitiativeRollerManager.rolledCharList);
        SetUnrolledCombatantsList(InitiativeRollerManager.unrolledCharList);
    }

    @Override
    protected void onResume() {

        super.onResume();
        SetRolledCombatantsList(InitiativeRollerManager.rolledCharList);
        SetUnrolledCombatantsList(InitiativeRollerManager.unrolledCharList);
    }

    public static MainActivity instance; //todo: instead of singleton, make every other class have a Context in their constructor and pass "this" when instatiating it
    private void CreateSingleton()
    {
        if(instance == null)
            instance = this;
    }

    private void ManageReferences() {
        //fetch references to on-screen things

        rollBtn = findViewById(R.id.initiativeRoll);
        cancelFromListBtn = findViewById(R.id.cancelFromUnrolledList);
        clearBtn = findViewById(R.id.clearButton);
        openCharMenuBtn = findViewById(R.id.addCharMenuOpener);
        savePartyListBtn = findViewById(R.id.saveToFile);
        loadPartyListBtn = findViewById(R.id.loadFromFiles);

        unrolledRecyclerView = findViewById(R.id.unrolledCombatantRecyclerView);
        unrolledRecyclerView.setHasFixedSize(true);
        unrolledRecyclerView.setLayoutManager((new LinearLayoutManager(this)));

        rolledRecyclerView = findViewById(R.id.rolledCombatantRecyclerView);
        rolledRecyclerView.setHasFixedSize(true);
        rolledRecyclerView.setLayoutManager((new LinearLayoutManager(this)));

        toolbarHolder = findViewById(R.id.toolbarHolder);
        toolbarHolder.bringToFront();
    }

    public void SetUnrolledCombatantsList(List<Adventurer> advents)
    {
       UnrolledAdventurerRecyclerAdapter adapter = new UnrolledAdventurerRecyclerAdapter(this, advents);
       unrolledRecyclerView.setAdapter(adapter);
    }

    public void SetRolledCombatantsList(List<Adventurer> adventurers)
    {
        RolledAdventurerRecyclerAdapter adapter = new RolledAdventurerRecyclerAdapter(this, adventurers);
        rolledRecyclerView.setAdapter(adapter);
    }

    public void AddCharToUnrolledList(Adventurer adv)
    {
        InitiativeRollerManager.unrolledCharList.add(adv);
        SetUnrolledCombatantsList(InitiativeRollerManager.unrolledCharList);
    }

    //region =====================   DIALOG RELATED REGION============================================================================

    // ============================   NEW CHARACTER   =================================
    //This creates the dialogue page for when users add new characters
    public Dialog AddCharacterDialogue()
    {
       /* //PREPARE BUILDER: THE BUILDER IS A GLORIFIED CONSTRUCTOR THAT CREATES A HIGHLY CUSTOMIZABLE DIALOGUE
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Combatant");
        builder.setIcon(R.drawable.ic_launcher_background);
        builder.setMessage("Input Name and Info");

        builder.setView(R.layout.new_char_layout);
        */

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.new_char_layout);
        dialog.setTitle("Add new Character");

        //Internal data
        final EditText nameInput = dialog.findViewById(R.id.newCharName);
        final EditText initInput = dialog.findViewById(R.id.newCharInitiative);
        final CheckBox advantage = dialog.findViewById(R.id.advantageCheckBox);
        final Button cancel = dialog.findViewById(R.id.cancelButton);
        final Button submit = dialog.findViewById(R.id.submitButton);
        final Button instantRoll = dialog.findViewById(R.id.instantRollButton);

        nameInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(27)});
        initInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        dialog.setCanceledOnTouchOutside(true);

        //ADD CHAR TO UNROLLED LIST
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try
                {
                    int init = Integer.parseInt(initInput.getText().toString());

                    if(nameInput.getText().toString() == "") {
                        Toast.makeText(getApplicationContext(), "Error: character name missing", Toast.LENGTH_SHORT).show(); return; }

                    Adventurer adv = new Adventurer(nameInput.getText().toString(), init, advantage.isChecked(), 0);
                    AddCharToUnrolledList(adv);

                    Toast.makeText(getApplicationContext(), nameInput.getText().toString() + " was added to the list", Toast.LENGTH_SHORT).show();
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(getApplicationContext(), "Initiative Mod is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Error: couldn't add character to list", Toast.LENGTH_SHORT).show();
                    return;
                }

                nameInput.setText("");
                initInput.setText("");
                advantage.setChecked(false);
            }
        });

        //BACK
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //ADD CHAR TO ROLLED LIST
        instantRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try
                {
                    int initMod = Integer.parseInt(initInput.getText().toString());
                    Adventurer adv = new Adventurer(nameInput.getText().toString(), initMod, advantage.isChecked(), 0);

                    rollMngr.AddCharToRolledList(adv);
                    SetRolledCombatantsList(rollMngr.rolledCharList);
                    AddCharToUnrolledList(adv);

                    Toast.makeText(getApplicationContext(), nameInput.getText().toString() + " joined the combat", Toast.LENGTH_SHORT).show();
                }

                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Error: couldn't add character to list", Toast.LENGTH_SHORT).show();
                    return;
                }

                nameInput.setText("");
                initInput.setText("");
                advantage.setChecked(false);
            }
        });

        return dialog;
    }

    // ============================   SAVE   =================================
    public Dialog OpenSaveDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.save_dialog_layout);

        //Internal data
        final EditText nameInput = dialog.findViewById(R.id.partyListName);
        final Button submit = dialog.findViewById(R.id.confirmSavePartyList);
        final Button cancel = dialog.findViewById(R.id.cancel);

        nameInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(26)});

        dialog.setCanceledOnTouchOutside(true);

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(nameInput.getText().toString().trim().length() > 0)
                {
                    SavePartyListToFiles(nameInput.getText().toString());
                    Toast.makeText(getApplicationContext(), nameInput.getText().toString() + " saved!", Toast.LENGTH_SHORT).show();
                    nameInput.setText("");
                    dialog.dismiss();
                }
                else
                    Toast.makeText(getApplicationContext(), "Invalid Name", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }

        });

        return dialog;
    }

    private void SavePartyListToFiles(String fileName)
    {
        FileOutputStream fos = null;

        try
        {
            fos = openFileOutput(fileName + "_party", MODE_PRIVATE);

            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < InitiativeRollerManager.unrolledCharList.size(); i++)
            {
                sb.append("$").append(InitiativeRollerManager.unrolledCharList.get(i)._name).append("\n\n");
                sb.append("%").append(InitiativeRollerManager.unrolledCharList.get(i)._rollMod);

                if(InitiativeRollerManager.unrolledCharList.get(i)._advantage)
                    sb.append("ADV");

                sb.append("\n\n");
            }

            fos.write(sb.toString().getBytes());
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fos != null)
                try
                {
                    fos.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
        }
    }
    // ============================   LOAD   =================================
    private RecyclerView partyListsRecyclerView; //Needs to be a member variable, because a local one would go out of scope when still needed

    public Dialog OpenLoadDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.load_dialog);

        partyListsRecyclerView = dialog.findViewById(R.id.partyListsRecyclerView);
        Button cancelButton = dialog.findViewById(R.id.backFromLoadFiles);
        dialog.setCanceledOnTouchOutside(true);

        partyListsRecyclerView.setLayoutManager((new LinearLayoutManager(this)));
        PartyListsRecyclerAdapter adapter = new PartyListsRecyclerAdapter(getApplicationContext(), SearchFilesForParties());
        partyListsRecyclerView.setAdapter(adapter);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        return dialog;
    }

    public void SetPartyListsAdapter()
    {
        PartyListsRecyclerAdapter adapter = new PartyListsRecyclerAdapter(getApplicationContext(), SearchFilesForParties());
        partyListsRecyclerView.setAdapter(adapter);
    }

    public void LoadPartyFromFiles(String fileName)
    {
        FileInputStream fis = null;

        try
        {
            fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String text;
            List<Adventurer> loadedAdventurers = new ArrayList<>();

            try
            {
                while ((text = br.readLine()) != null)
                {
                    //TENTATIVE
                    String newName = "";
                    String newInit = "";
                    boolean adv = false;

                    if(text.contains("$"))
                    {
                        newName = text.replace("$", "");

                        br.readLine();

                        while((text = br.readLine()) == "") {}
                    }

                    if(text.contains("%"))
                    {
                        newInit = text.replace("%", "");
                        if(text.contains("ADV"))
                        {
                            newInit = newInit.replace("ADV", "");
                            adv = true;
                        }
                    }

                    Adventurer newAdventurer;

                    if(!newName.isEmpty() || !newInit.isEmpty())
                    {
                        newAdventurer = Adventurer.ParseAdventurer(newName, newInit, adv);
                        loadedAdventurers.add(newAdventurer);
                        Toast.makeText(this, newAdventurer._name, Toast.LENGTH_SHORT);
                    }
                }

                InitiativeRollerManager.unrolledCharList = loadedAdventurers;
                SetUnrolledCombatantsList(InitiativeRollerManager.unrolledCharList);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    //============================   EDIT CHAR   =================================

    public Dialog OpenEditCharDialog(final Adventurer adventurer, final int positionInList)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_character_dialog);
        dialog.setCanceledOnTouchOutside(true);

        final TextView editName = dialog.findViewById(R.id.editCharName);
        final TextView editInit = dialog.findViewById(R.id.editCharInit);
        final CheckBox editAdv = dialog.findViewById(R.id.editAdvantage);
        Button confirmBtn = dialog.findViewById(R.id.editConfirm);
        Button backBtn = dialog.findViewById(R.id.editBack);

        editName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(27)});
        editInit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});

        editName.setText(adventurer._name);
        editInit.setText(Integer.toString(adventurer._rollMod));
        editAdv.setChecked(adventurer._advantage);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Adventurer editedAdventurer = new Adventurer(editName.getText().toString(), Integer.parseInt(editInit.getText().toString()), editAdv.isChecked(), 0);

                InitiativeRollerManager.unrolledCharList.remove(adventurer);
                InitiativeRollerManager.unrolledCharList.add(positionInList, editedAdventurer);
                SetUnrolledCombatantsList(InitiativeRollerManager.unrolledCharList);
                dialog.cancel();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        return dialog;
    }
    //endregion =========================================================================================================================

    private List<File> SearchFilesForParties()
    {
        File dir = getFilesDir();

        List<File> files = new ArrayList<>();

        for(File f: dir.listFiles())
        {
            files.add(f);
        }

        return files;
    }
}
