package com.ebookfrenzy.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

    private SwitchCompat capitalButton;
    private SwitchCompat currencyButton;
    private SwitchCompat flagButton;
    private SwitchCompat translationButton;

    //private TextView officialNameText;

    // Create array to hold translated names
    private List<Name> translatedNames = new ArrayList<>();

    private RecyclerView namesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service);

        countryEditText = (EditText)findViewById(R.id.country_edittext);
        countryTextView = (TextView)findViewById(R.id.country_textview);
        capitalTextView = (TextView)findViewById(R.id.capital_textview);
        currencyTextView = (TextView)findViewById(R.id.currency_textview);
        flagImageView = (ImageView)findViewById(R.id.flag_imageview);
        // officialNameText = findViewById(R.id.officialNameText);



        // temporary translation view, need to be updated to recyclerView
        translationTextView = (TextView)findViewById(R.id.temp_translation_textview);

        capitalButton = findViewById(R.id.capital_switch_button);
        currencyButton = findViewById(R.id.currency_switch_button);
        flagButton = findViewById(R.id.flag_switch_button);
        translationButton = findViewById(R.id.translation_switch_button);
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
                        input = NetworkUtil.validInput(countryEditText.getText().toString());
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
                Bitmap flag;
                JSONObject jObject;
                {
                    try {
                        resp = NetworkUtil.httpResponse(url);
                        JSONArray jArray = new JSONArray(resp);
                        jObject = jArray.getJSONObject(0);

                        // Obtain the flag image from the url address
                        JSONObject flagObject = jObject.getJSONObject("flags");
                        String pngUrl = flagObject.getString("png");
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
                        countryTextView.setText("Official Name: " + jObject.getJSONObject("name")
                                .getString("official"));

                        if (capitalButton.isChecked()) {
                            // call the capital info from rest country api
                            JSONArray capitalArray = jObject.getJSONArray("capital");
                            capitalTextView.setText("Capital: " + capitalArray.getString(0)
                                    .replace(",\n", ","));
                        }
                        if (currencyButton.isChecked()) {
                            // call the currency info from rest country api
                            JSONObject currencyObject = jObject.getJSONObject("currencies");
                            String currencyString = currencyObject.toString()
                                    .replace("{", "")
                                    .replace("}", "")
                                    .replace("\"", "")
                                    .replace(":", ": ")
                                    .replace(",", ", ");
                            currencyTextView.setText("Currency: " + currencyString);
                        }
                        if (flagButton.isChecked()) {
                            // set the flag info from rest country api (cannot call it here in main
                            // activity, need to be done in a thread above)
                            flagImageView.setImageBitmap(flag);
                        }
                        if (translationButton.isChecked()) {
                            // Ensure translated names array is empty whenever we make a web service call
                            translatedNames.clear();

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
                            namesRecyclerView = findViewById(R.id.nameRecyclerView);
                            namesRecyclerView.setLayoutManager(new LinearLayoutManager(WebServiceActivity.this));
                            namesRecyclerView.setAdapter(new NameAdapter(translatedNames, WebServiceActivity.this));

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
}