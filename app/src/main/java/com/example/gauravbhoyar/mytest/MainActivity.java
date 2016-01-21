package com.example.gauravbhoyar.mytest;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private DBHandler dbHandler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {

            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Brand";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        ListView brandListView;

        DBHandler  handler;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_main, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_asc) {
                setlistdata("ASC");

            }
            if (id == R.id.action_desc) {
                setlistdata("DESC");

            }

            return super.onOptionsItemSelected(item);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            brandListView = (ListView) rootView.findViewById(R.id.brandlist);

            handler = new DBHandler(getActivity());
            SQLiteDatabase db = handler.getWritableDatabase();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                brandListView.setNestedScrollingEnabled(true);
            }



            long count = handler.getRowCount(db);
            if (count == 0)
            {
                if (isNetworkAvailable())
                new DemoAsync(getActivity()).execute("http://188.166.204.35/mobile-api/v3/schemes");
                else
                    Toast.makeText(getActivity(), "NO Network", Toast.LENGTH_SHORT).show();


            }

            else
            {
                setlistdata("");
            }


            return rootView;
        }


        class DemoAsync extends AsyncTask<String, Integer, JSONObject> {
            ProgressDialog dialog;
            Context context;


            public DemoAsync(Context context) {
                this.context = context;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(getActivity());
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Loading....");
                dialog.setMessage("Please Wait....");
                dialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... strings) {
                String json;

                JSONObject jsonObject = null;
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);

                    connection.connect();
                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "n");
                    }
                    is.close();
                    json = sb.toString();

                    try {
                        jsonObject = new JSONObject(json);
                    } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);

                try {
                    JSONObject newObject = jsonObject.getJSONObject("data");

                    for (Iterator<String> iter = newObject.keys(); iter.hasNext(); ) {
                        String key = iter.next();
                        JSONObject object = newObject.getJSONObject(key);
                        ContentValues values = new ContentValues();
                        for (Iterator<String> iterval = object.keys(); iterval.hasNext(); ) {
                            String newkey = iterval.next();

                            Object value = object.get(newkey);
                            if (!object.isNull(newkey)) {
                                if (newkey.contains("-"))
                                    newkey = newkey.replace("-", "_");

                                if (value instanceof JSONArray) {
                                    JSONArray keyStr = (JSONArray) value;
                                    values.put(newkey, keyStr.toString());
                                } else {
                                    String keyStr = (String) value;
                                    values.put(newkey, keyStr);
                                }
                            }
                        }


                        SQLiteDatabase db = handler.getWritableDatabase();
                        db.insert(DBHandler.TABLE_NAME, null, values);


                    }

                    setlistdata("ASC");




                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

        }

        public void setlistdata(String query)
        {

            Cursor cursor = handler.getWritableDatabase().query(handler.TABLE_NAME, null, null, null, null, null, handler.SCHEME_DATE + " "+query);

            String[] from = new String[]{handler.BRAND,
                    handler.MANUFACTURER, handler.MRP, handler.SCHEME, handler.SCHEME_DATE, handler.ORDER_QUANTITY};
            int[] to = new int[]{R.id.brand, R.id.manufacturer, R.id.mrp,
                    R.id.scheme, R.id.scheme_date, R.id.order_qty};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, cursor, from, to, 0);
            brandListView.setAdapter(adapter);
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }


}
