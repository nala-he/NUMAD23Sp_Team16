package com.ebookfrenzy.numad23sp_team16;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WebServiceActivity extends AppCompatActivity {
    private static final String TAG = "WebServiceActivity";

    private EditText countryEditText;
    private TextView countryTextView;
    private TextView capitalTextView;
    private TextView currencyTextView;
    private ImageView flagImageView;

    // temporary translation view, need to be updated to recyclerView
    private TextView translationTextView;

    private Handler textHandler = new Handler();
    private Handler imageHandler = new Handler();

    private SwitchCompat capitalButton;
    private SwitchCompat currencyButton;
    private SwitchCompat flagButton;
    private SwitchCompat translationButton;

    //private TextView officialNameText;

    private List<Name> translatedNames = new ArrayList<>();
    private RecyclerView namesRecyclerView;
    private NameAdapter nameAdapter = new NameAdapter(translatedNames, WebServiceActivity.this);

    private Bitmap flag;
    private String officialName;
    private String capitalName;
    private String currencyString;
    private String pngUrl;
    Boolean capitalChecked = false;
    Boolean currencyChecked = false;
    Boolean flagChecked = false;
    Boolean translationChecked = false;
    static final String CAPITAL_BUTTON = "CAPITAL_BUTTON";
    static final String CURRENCY_BUTTON = "CURRENCY_BUTTON";
    static final String FLAG_BUTTON = "FLAG_BUTTON";
    static final String TRANSLATION_BUTTON = "TRANSLATION_BUTTON";
    static final String OFFICIAL_NAME = "OFFICIAL_NAME";
    static final String CAPITAL = "CAPITAL";
    static final String CURRENCY = "CURRENCY";
    static final String FLAG_URL = "FLAG_URL";
    static final String NUMBER_ITEMS = "NUMBER_ITEMS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service);

        countryEditText = (EditText)findViewById(R.id.country_edittext);
        countryTextView = (TextView)findViewById(R.id.country_textview);
        capitalTextView = (TextView)findViewById(R.id.capital_textview);
        currencyTextView = (TextView)findViewById(R.id.currency_textview);
        flagImageView = (ImageView)findViewById(R.id.flag_imageview);
        namesRecyclerView = findViewById(R.id.nameRecyclerView);
        // officialNameText = findViewById(R.id.officialNameText);

        // temporary translation view, need to be updated to recyclerView
        translationTextView = (TextView)findViewById(R.id.temp_translation_textview);

        capitalButton = findViewById(R.id.capital_switch_button);
        currencyButton = findViewById(R.id.currency_switch_button);
        flagButton = findViewById(R.id.flag_switch_button);
        translationButton = findViewById(R.id.translation_switch_button);

        //add icons
        ImageView countryIcon = findViewById(R.id.imageView_country);
        ImageView currencyIcon = findViewById(R.id.imageView_currency);
        ImageView capitalIcon = findViewById(R.id.imageView_capital);
        ImageView translationIcon = findViewById(R.id.imageView_translation);
        countryIcon.setImageResource(R.drawable.country);
        capitalIcon.setImageResource(R.drawable.capital);
        currencyIcon.setImageResource(R.drawable.currency);
        translationIcon.setImageResource(R.drawable.translation);
    }

    @SuppressLint("SetTextI18n")
    public void callWebserviceButtonHandler(View view) {
        runnableThread runnableThread = new runnableThread();
        countryTextView.setText("Official Name: ");
        capitalTextView.setText("Capital: ");
        currencyTextView.setText("Currency: ");
        flagImageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.flag));

        // to be updated after implementing recycler view
        translationTextView.setText("Translations: ");

        new Thread(runnableThread).start();
    }

    class runnableThread implements Runnable {

        @Override
        public void run() {
            textHandler.post(new Runnable() {

                String input;
                {
                    try {
                        //add trim() so that extra space after country name won't be recognized as invalid name
                        input = NetworkUtil.validInput(countryEditText.getText().toString().trim());
                    } catch (NetworkUtil.MyException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                URL url;
                {
                    try {
                        url = new URL(input);
                    } catch (MalformedURLException e) {
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                // Get String response from the url address
                String resp;
                JSONObject jObject;
                {
                    try {
                        resp = NetworkUtil.httpResponse(url);
                        JSONArray jArray = new JSONArray(resp);
                        jObject = jArray.getJSONObject(0);

                        // Obtain the flag image from the url address
                        JSONObject flagObject = jObject.getJSONObject("flags");
                        pngUrl = flagObject.getString("png");
                        URL url = new URL(pngUrl);
                        flag = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } catch (IOException e) {
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),"Please type in a full country name",Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void run() {

                    try {
                        // call the region of the given country from restcountry api
//                        result_view.setText(jObject.getString("region"));
                        // display the country name
                        officialName = jObject.getJSONObject("name").getString("official");
                        countryTextView.setText("Official Name: " + officialName);

                        // Store switch states for each ping
                        capitalChecked = capitalButton.isChecked();
                        currencyChecked = currencyButton.isChecked();
                        flagChecked = flagButton.isChecked();
                        translationChecked = translationButton.isChecked();

                        if (capitalChecked) {
                            // call the capital info from rest country api
                            JSONArray capitalArray = jObject.getJSONArray("capital");
                            capitalName = capitalArray.getString(0).replace(",\n", ",");
                            capitalTextView.setText("Capital: " + capitalName);
                        }
                        if (currencyChecked) {
                            // call the currency info from rest country api
                            JSONObject currencyObject = jObject.getJSONObject("currencies");
                            currencyString = currencyObject.toString()
                                    .replace("{", "")
                                    .replace("}", "")
                                    .replace("\"", "")
                                    .replace(":", ": ")
                                    .replace(",", ", ");
                            currencyTextView.setText("Currency: " + currencyString);
                        }
                        if (flagChecked) {
                            // set the flag info from rest country api (cannot call it here in main
                            // activity, need to be done in a thread above)
                            flagImageView.setImageBitmap(flag);
                        }

                        // Ensure translated names array is empty whenever we make a web service call
                        translatedNames.clear();
                        nameAdapter.notifyDataSetChanged();

                        if (translationChecked) {
                            // Retrieve the translations from web service call only if translations button checked
                            JSONObject translationsObject = jObject.getJSONObject("translations");

                            // Iterate through each language to get translation
                            Iterator<String> iter = translationsObject.keys();

                            // Create set to store translated strings to avoid duplicates
                            Set<String> noDuplicates = new HashSet<>();

                            while(iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    JSONObject value = translationsObject.getJSONObject(key);
                                    String name = value.getString("official");

                                    // Store translated string in our set
                                    noDuplicates.add(name);

                                } catch (JSONException e) {
                                    Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }

                            // Convert non-duplicated translated names to array as Name objects
                            for (String s : noDuplicates) {
                                // Create new Name object
                                Name translation = new Name(s);
                                translatedNames.add(translation);
                            }

                            // Get recycler view from layout, set layout and adapter
                            namesRecyclerView.setLayoutManager(new LinearLayoutManager(WebServiceActivity.this));
                            namesRecyclerView.setAdapter(nameAdapter);

                            // yutong's temp translation implementation
//                            String translationString = jObject.getJSONObject("translations")
//                                    .toString().replace("{", "")
//                                    .replace("-", "")
//                                    .replace("\"", "")
//                                    .replace(":", ": ")
//                                    .replace("},", "\n")
//                                    .replace("}", "");
//                            translationTextView.setText("Translations:\n"
//                                    + translationString);
                        }


                        // Commented out - implemented by yutong
                        // Retrieve official name of country
                        //JSONObject countryName = jObject.getJSONObject("name");
                        //String officialName = countryName.getString("official");
                        //officialNameText.setText(officialName);



                    } catch (Exception e) {
                        // commented out this part to avoid repetitive exception toast messages showing up
                        // when the user input an invalid country name
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show();
//                            }
//                        });
                    }
                }
            });
            try {
                Thread.sleep(1000); //Makes the thread sleep or be inactive for 10 seconds
            } catch (InterruptedException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // Create new worker thread for decoding image url to bitmap
    class urlToBitmapThread implements Runnable {

        @Override
        public void run() {
            try {
                URL url = new URL(pngUrl);
                flag = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                imageHandler.post(() -> {
                    flagImageView.setImageBitmap(flag);
                });
            } catch (IOException e) {
                Toast.makeText(WebServiceActivity.this, "Unable to load flag image",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    // Device rotated: continue to display country info
    // Handling Orientation Changes on Android
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = translatedNames == null ? 0 : translatedNames.size();

        // Store switch button states (checked vs. not checked) AFTER ping has been made
        outState.putBoolean(CAPITAL_BUTTON, capitalChecked);
        outState.putBoolean(CURRENCY_BUTTON, currencyChecked);
        outState.putBoolean(FLAG_BUTTON, flagChecked);
        outState.putBoolean(TRANSLATION_BUTTON, translationChecked);

        // Store official name, capital, currency, and flag png url
        outState.putString(OFFICIAL_NAME, officialName);
        outState.putString(CAPITAL, capitalName);
        outState.putString(CURRENCY, currencyString);
        outState.putString(FLAG_URL, pngUrl);

        // Store translations
        // Store size of list as key-value pair
        outState.putInt(NUMBER_ITEMS, size);

        // Store current list of translated names as key-value pairs
        // Generate unique key for each item and store as key-value pair
        for (int i = 0; i < size; i++) {
            // put name info into instance
            outState.putString("name" + i, translatedNames.get(i).getName());
        }

        // Call superclass to save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore buttons
        capitalChecked = savedInstanceState.getBoolean(CAPITAL_BUTTON);
        currencyChecked = savedInstanceState.getBoolean(CURRENCY_BUTTON);
        flagChecked = savedInstanceState.getBoolean(FLAG_BUTTON);
        translationChecked = savedInstanceState.getBoolean(TRANSLATION_BUTTON);

        // Restore official name
        officialName = savedInstanceState.getString(OFFICIAL_NAME);
        if (officialName != null) {
            countryTextView.setText("Official Name: " + officialName);
        }

        // Restore capital
        if (capitalChecked) {
            capitalName = savedInstanceState.getString(CAPITAL);

            if (capitalName != null) {
                capitalTextView.setText("Capital: " + capitalName);
            }
        }

        // Restore currency
        if (currencyChecked) {
            currencyString = savedInstanceState.getString(CURRENCY);

            if (currencyString != null) {
                currencyTextView.setText("Currency: " + currencyString);
            }
        }

        // Restore flag
        if (flagChecked) {
            pngUrl = savedInstanceState.getString(FLAG_URL);

            if (pngUrl != null) {
                urlToBitmapThread runnableThread = new urlToBitmapThread();
                new Thread(runnableThread).start();
            }
        }

        // Restore translations
        if (translationChecked) {
            // Get size of list from key-value pair
            int size = savedInstanceState.getInt(NUMBER_ITEMS);

            if (size != 0) {
                // Retrieve keys we stored in the instance
                for (int i = 0; i < size; i++) {
                    String name = savedInstanceState.getString("name" + i);

                    // Create and add new link card based on stored instance
                    Name translatedName = new Name(name);
                    translatedNames.add(translatedName);
                }
                namesRecyclerView.setLayoutManager(new LinearLayoutManager(WebServiceActivity.this));
                namesRecyclerView.setAdapter(nameAdapter);
            }
        }
    }
}