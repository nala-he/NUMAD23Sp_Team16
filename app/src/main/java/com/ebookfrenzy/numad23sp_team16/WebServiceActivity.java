package com.ebookfrenzy.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    private EditText countryEditText ;
    private TextView capitalTextView ;
//    private TextView currencyTextView;
    private Handler textHandler = new Handler();
    private JSONObject jObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service);

        countryEditText = (EditText)findViewById(R.id.country_edittext);
        capitalTextView = (TextView)findViewById(R.id.capital_textview);
//        currencyTextView = (TextView)findViewById(R.id.currency_textview);

    }

    public void callWebserviceButtonHandler(View view) {
        runnableThread runnableThread = new runnableThread();
        new Thread(runnableThread).start();

    }

    class runnableThread implements Runnable {

        @Override
        public void run() {
            textHandler.post(new Runnable() {

                // REST Country web service: https://restcountries.com/v3.1/name/
                String input;
                {
                    try {
                        input = NetworkUtil.validInput(countryEditText.getText().toString());
                    } catch (NetworkUtil.MyException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                // Get String response from the url address
                String resp;
                {
                    try {
                        resp = NetworkUtil.httpResponse(url);
                        JSONArray jArray = new JSONArray(resp);
                        jObject = jArray.getJSONObject(0);
                    } catch (IOException e) {
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void run() {

                    try {
                        // call the region of the given country from restcountry api
//                        result_view.setText(jObject.getString("region"));
                        // call the capital info from restcountry api
                        JSONArray capitalArray = jObject.getJSONArray("capital");
                        capitalTextView.setText(capitalArray.getString(0));

//                        JSONObject currencyObject = jObject.getJSONObject("currencies");
//                        currencyTextView.setText(currencyObject.getString("name"));



                        // Retrieve the translations from web service call only if translations button checked
                        // TODO: Add if statement to check if translations button checked
                        JSONObject translationsObject = jObject.getJSONObject("translations");

                        // Create array to hold translated names
                        List<String> translatedNames = new ArrayList<>();

                        // Iterate through each language to get translation
                        Iterator<String> iter = translationsObject.keys();
                        while(iter.hasNext()) {
                            String key = iter.next();
                            try {
                                JSONObject value = translationsObject.getJSONObject(key);
                                String name = value.getString("official");
                                // Store translated official name in our array
                                translatedNames.add(name);
                            } catch (JSONException e) {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Remove duplicate translations from array
                        Set<String> removedDuplicates = new HashSet<>(translatedNames);
                        translatedNames.clear();
                        translatedNames.addAll(removedDuplicates);



                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
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