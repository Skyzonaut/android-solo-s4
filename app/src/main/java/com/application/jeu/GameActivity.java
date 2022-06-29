package com.application.jeu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.application.model.Event;
import com.application.model.EventsList;
import com.application.model.Inventory;
import com.application.model.Story;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private Inventory inventory;
    private EventsList eventsList;
    private ImageSwitcher imageSwitcher;
    private Event currentEvent;
    private TextView eventTextView;
    private Story story;
    private JSONObject choosenOption;
    private final String firstEventName = "porte";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        this.eventTextView = (TextView) findViewById(R.id.eventTextView);

        this.inventory = this.initializeInventory();

        setupInventoryBar();

        setupImageSwitch();

        this.eventsList = new EventsList(this);

        this.story = new Story(this);

        initializeEvent();
    }

    private Inventory initializeInventory() {
        inventory = new Inventory(this);
//        Log.d("CHARGEMENT-MODELE", inventory.getInventory().toString());
//        Log.d("CHARGEMENT-EPEE", inventory.getSwordObject().toString());
//        Log.d("CHARGEMENT-BOUCLIER", inventory.getShieldObject().toString());
//        Log.d("CHARGEMENT-POMME", inventory.getItemObject("pomme").toString());
        return inventory;
    }

    public void setupInventoryBar() {
        try
        {
            JSONObject sword = inventory.getSwordObject();
            ImageView swordView = findViewById(R.id.swordView);
            swordView.setImageResource(getResources().getIdentifier(sword.getString("img"), "drawable", getPackageName()));

            JSONObject shield = inventory.getShieldObject();
            ImageView shieldView = findViewById(R.id.shieldView);
            shieldView.setImageResource(getResources().getIdentifier(shield.getString("img"), "drawable", getPackageName()));

            JSONObject pomme = inventory.getItemObject("pomme");
            TextView pommeCount = findViewById(R.id.appleCount);
            pommeCount.setText(String.valueOf(pomme.getInt("quantity")));

            JSONObject key = inventory.getItemObject("clef");
            TextView keyCount = findViewById(R.id.keyCount);
            keyCount.setText(String.valueOf(key.getInt("quantity")));

            JSONObject gold = inventory.getItemObject("gold");
            TextView goldCount = findViewById(R.id.goldCount);
            goldCount.setText(String.valueOf(gold.getInt("quantity")));

            JSONObject potion = inventory.getItemObject("potion");
            int potionCount = potion.getInt("quantity");
            if (potionCount >= 1) {
                ImageView potion1View = findViewById(R.id.potion1View);
                potion1View.setImageResource(getResources().getIdentifier(potion.getString("img"), "drawable", getPackageName()));

            }
            if (potionCount == 2) {
                ImageView potion2View = findViewById(R.id.potion2View);
                potion2View.setImageResource(getResources().getIdentifier(potion.getString("img"), "drawable", getPackageName()));
            }

            int life = inventory.getLife();
            TextView lifeCount = findViewById(R.id.lifeCount);
            lifeCount.setText(String.valueOf(life));

        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
        }



    }

    private void setupImageSwitch() {
        Log.d("KEY", "SETUPIMAGE");
        imageSwitcher =  findViewById(R.id.imageSwitcher);
        imageSwitcher.setFactory(() -> {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return imageView;
        });

        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);
        Log.d("KEY", "FIN-SETUPIMAGE");

    }

    private void initializeEvent() {

        // Model
        getFirstEventJsonObject();

        // UI
        updateEventText();
        updateImageSwitcher();
        displayChoice();
    }


    /**
     *
     */
    private void nextEvent() {

        // Model
        getNextEventJsonObject();
        // UI
        updateEventText();
        updateImageSwitcher();
        displayChoice();
    }


    private void getFirstEventJsonObject() {
        Random random = new Random();
        Event tmpEvent;
        int nextEventId;

        try
        {
            do
            {
                nextEventId = random.nextInt((this.eventsList.length()));
                tmpEvent = this.eventsList.get(nextEventId);
            }
            while (!firstEventName.equals(tmpEvent.getJson().getString("name")));
            this.currentEvent = tmpEvent;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void getNextEventJsonObject() {
        Random random = new Random();
        Event tmpEvent = null;
        int nextEventId;

        // Si Event.option.nextCard est vide
        try
        {
            JSONArray nextCards = choosenOption.getJSONArray("nextCard");
            ArrayList<String> nextCardsArray = new ArrayList<>();
            for (int i = 0; i < nextCards.length(); i++)
            {
                nextCardsArray.add(nextCards.getString(i));
                Log.d("NEXT-CARDS", nextCards.getString(i));
            }
            if (nextCardsArray.size() == 0)
            {
                do
                {
                    nextEventId = random.nextInt((this.eventsList.length()));
                    tmpEvent = this.eventsList.get(nextEventId);
//                    Log.d("CARD-NEXT-TEST", '\t' + tmpEvent.toString());
//                    Log.d("CARD-NEXT-TEST", '\t' + tmpEvent.getJson().getString("name"));
//                    Log.d("CARD-NEXT-TEXT", '\t' +  story.toString());
//                    Log.d("CARD-NEXT-TEST", '\t' + nextCardsArray.toString());

                }
                while (!checkEventStoryCondition(tmpEvent));
//                Log.d("CARD-NEXT-TEST", '\t' + tmpEvent.toString());

                this.currentEvent = tmpEvent;
                Log.d("NEXT-CARD", "@@@ " + tmpEvent);

//                Log.d("CARD-NEXT", "\n");
//                Log.d("CARD-NEXT", "\n");
//
//                // Log
//                Log.d("NEXTEVENT", String.valueOf(nextEventId));
//                Log.d("NEXTEVENT", this.currentEvent.toString());
            }
            else
            {
                int canExit;
                do
                {
                    int isNextCard = 0;
                    int hasCorrectStory = 0;

                    nextEventId = random.nextInt((this.eventsList.length()));
                    tmpEvent = this.eventsList.get(nextEventId);
//                    Log.d("CARD-NEXT-TEST", "--------------------");
//                    Log.d("CARD-NEXT-TEST", '\t' + tmpEvent.toString());
//                    Log.d("CARD-NEXT-TEST", '\t' + nextCardsArray.toString());
//                    Log.d("CARD-NEXT-TEST", '\t' + tmpEvent.getJson().getString("name"));
//                    Log.d("CARD-NEXT-TEXT", '\t' + story.toString());

                    isNextCard = nextCardsArray.contains(tmpEvent.getJson().getString("name"))  ? 1 : 0;
                    hasCorrectStory = checkEventStoryCondition(tmpEvent) ? 1 : 0;
                    canExit = isNextCard + hasCorrectStory;
//                    Log.d("CARD-NEXT-TEST", '\t' + String.valueOf(isNextCard));
//                    Log.d("CARD-NEXT-TEST", '\t' + String.valueOf(hasCorrectStory));
//                    Log.d("CARD-NEXT-TEST", String.valueOf(canExit));
//                    Log.d("CARD-NEXT-TEST", "--------------------");

                }
                while (canExit != 2);
//                Log.d("CARD-NEXT-TEST", "CONTINUE&");
//                Log.d("CARD-NEXT-TEST", '\t' + tmpEvent.toString());
                this.currentEvent = tmpEvent;
//                Log.d("CARD-NEXT", "\n");
//                Log.d("CARD-NEXT", "\n");
            }
        }
        catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    /**
     *
     */
    private boolean checkEventStoryCondition(Event event) {
        Log.d("CHK-EVENT", event.toString());
        boolean canLaunch = true;
        JSONObject eventConditions = event.getConditions();
        if (eventConditions != null) {
            Iterator<String> conditionIterator = eventConditions.keys();

            while (conditionIterator.hasNext() && canLaunch) {
                String conditionKey = conditionIterator.next();
                try {
                    if (this.story.getEventCondition(conditionKey) != eventConditions.getInt(conditionKey)) {
                        canLaunch = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("CHK-EVENT", String.valueOf(canLaunch));
        return canLaunch;
    }

    /**
     *
     */
    private void updateEventText() {
        eventTextView.setText(currentEvent.getEventText());
    }

    /**
     *
     */
    private void updateImageSwitcher() {
        int id = getResources().getIdentifier(currentEvent.getImageName(), "drawable", getPackageName());
        this.imageSwitcher.setImageResource(id);
    }


    /**
     *
     */
    private void displayChoice() {

        JSONArray options = currentEvent.getOptions();
        if (currentEvent.getType() != null && currentEvent.getType().equals("no-playable")) {
            this.nextEvent();
            // TODO le sleep se fait après le click et ne lance pas le prochain event
        }
        else
        {
            for (int i = 0; i < options.length(); i++) {
                try {
                    JSONObject option = (JSONObject) options.get(i);
                    if (checkOptionConditions(option)) {
                        createOneChoiceButton(option.getString("text"), i, option);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private boolean checkOptionConditions(JSONObject option) {
        boolean isOk = true;
        try
        {
            if (option.has("optionConditions")) {
                JSONObject optionConditionJsonObject = option.getJSONObject("optionConditions");
                for (Iterator<String> keys = optionConditionJsonObject.keys(); keys.hasNext();){
                    String key = keys.next();
                    if (optionConditionJsonObject.getInt(key) != story.getEventCondition(key)) {
                        isOk = false;
                    }
                }
            }

            if (option.has("inventoryRequirements")) {
                JSONObject inventoryRequirementsJsonObject = option.getJSONObject("inventoryRequirements");
                for (Iterator<String> keys = inventoryRequirementsJsonObject.keys(); keys.hasNext();){
                    String key = keys.next();
//                    Log.d("CHECK-CONDITION-KEY", key);
                    String requirements = inventoryRequirementsJsonObject.getString(key);
                    switch (requirements.charAt(0)){
                        case '>':
                            isOk = inventory.getItemObject(key).getInt("quantity") > Integer.parseInt(requirements.substring(1));
                            break;
                        case '<':
                            isOk = inventory.getItemObject(key).getInt("quantity") < Integer.parseInt(requirements.substring(1));
                            break;
                        case '=':
                            isOk = inventory.getItemObject(key).getInt("quantity") == Integer.parseInt(requirements.substring(1));
                            break;
                        case '!':
                            isOk = inventory.getItemObject(key).getInt("quantity") != Integer.parseInt(requirements.substring(1));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
        }
        return isOk;
    }

    /**
     *
     */
    private void createOneChoiceButton(String text, int id, JSONObject option) {
        Button nButton = new Button(this);
        nButton.setId(id);
        nButton.setText(text);
        nButton.setBackgroundColor(getResources().getColor(R.color.choiceButtonBackground));
        nButton.setTextColor(getResources().getColor(R.color.choiceButtonForeground));
        nButton.setTextSize(15);

        LinearLayout choiceLayout = (LinearLayout) findViewById(R.id.eventOptionLayout);
        choiceLayout.addView(nButton);
        Space space = new Space(GameActivity.this);
        space.setMinimumHeight(10);
        choiceLayout.addView(space);

        nButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceLayout.removeAllViews();
                try
                {
                    story.updateStory(option.getJSONArray("valueToChange"));

                    updateInventory(option);

                    setupInventoryBar();
                }
                catch (JSONException jse)
                {
                    jse.printStackTrace();
                }
                choosenOption = option;
                nextEvent();
            }
        });
    }

    private void updateInventory(JSONObject option) {
        try {
            if (option.has("optionType")) {

                if (option.getString("optionType").equals("loot")) {

                    ArrayList<String> chosingList = new ArrayList<>();
                    HashMap<String, Pair<String, Integer>> stuffMap = new HashMap<>();

                    // Récupération du loot
                    JSONObject loot = option.getJSONObject("loot");

                    // Pour chaque item lootable on récupère sa chance d'arriver
                    // On met ensuite dans l'array "chosingList" autant de fois cette item qu'il n'a
                    // de chance.
                    for (Iterator<String> it = loot.keys(); it.hasNext(); ) {
                        String key = it.next();
                        String stuffItem = loot.getJSONObject(key).getString("stuffItem");
                        int addValue = loot.getJSONObject(key).has("addValue")
                                ? loot.getJSONObject(key).getInt("addValue")
                                : 0;
                        Pair<String, Integer> itemToAddTuple = new Pair<>(stuffItem, addValue);
                        stuffMap.put(key, itemToAddTuple);
                        int chance = loot.getJSONObject(key).getInt("chance");
                        for (int i = 0; i < chance ; i++) {
                            chosingList.add(key);
                        }
                    }

                    // On va ensuite choisir aléatoirement un nombre entre 0 et la taille de la liste
                    // avec tous les items répétés x fois par leur nombre de chance
                    Random random = new Random();
                    int choice = random.nextInt(chosingList.size());

                    // Et on récupère l'item à l'index aléatoirement choisi. Cela permet de gérer
                    // les chances de récupérer un objet de manière assez simple
                    String itemName = chosingList.get(choice);

                    // On récupère dans l'id de l'item, qui sert à l'identifiant dans la liste des objets
                    // disponibles
                    Pair<String, Integer> itemValue = stuffMap.get(itemName);


                    // On vérifie qu'il n'a pas plus de 2 potion
                    if (itemName.equals("potion")){
                        if (inventory.getItemObject("potion").getInt("quantity") >= 2) {
                            Toast toast = Toast.makeText(this, "Vous ne pouvez pas porter plus de 2 potions!", Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                    }

                    int duration = 2;

                    Toast toast = Toast.makeText(this, "Vous trouvez ceci : " + itemName.toUpperCase(Locale.ROOT), Toast.LENGTH_LONG);
                    toast.show();

                    if (itemValue.second != 0) {
                        inventory.setItemObject(itemValue.first, itemValue.second);
                    } else {
                        inventory.setItemObject(itemValue.first, inventory.getItemFromAvailableStuff(itemName));
                    }
                }
            }

            // On retire les éléments qui ont été consommé pour cette option
            if (option.has("inventoryChanges")) {
                JSONObject inventoryChanges = option.getJSONObject("inventoryChanges");
                for (Iterator<String> keys = inventoryChanges.keys(); keys.hasNext();) {
                    String key = keys.next();
                    Log.d("MARCHAND", key);
                    if (inventoryChanges.get(key) instanceof String){
                        inventory.setItemObject(key, inventory.getItemFromAvailableStuff(inventoryChanges.getString(key)));
                    }
                    else {
                        inventory.setItemObject(key, inventoryChanges.getInt(key));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}