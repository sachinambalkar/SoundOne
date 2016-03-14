package play.media.com.media.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import play.media.com.media.Adapter.GroupDetailsAdapter;
import play.media.com.media.model.GroupModel;
import play.media.com.media.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    ProgressDialog pDialog;
    Firebase myFirebaseRef;
    InetAddress IP= null;
    int port=4444;
    String ip_address;
    List<GroupModel> list=new ArrayList<>();
    List<GroupModel> list2=new ArrayList<>();
    GroupDetailsAdapter vivzadapter;
    RecyclerView recyclerView;
    private android.os.Handler handler = new android.os.Handler();
    StringBuilder selectedSong=new StringBuilder();
  //  boolean loadedData=false,loadedIP=false;
//////////////////////////////////////////////
    ///////////////////


    /*private RecyclerView mRecyclerView;
    private DataAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Student> studentList;
    private android.os.Handler handler = new android.os.Handler();*/

    ///////////////////
    ////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_select_group);
        myFirebaseRef = new Firebase("https://soundone.firebaseio.com/");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Spinner spinner=(Spinner)findViewById(R.id.spinner_song);
        List<String> listsong=new ArrayList<>();
        listsong.add(getResources().getString(R.string.song1));
        listsong.add(getResources().getString(R.string.song2));
        listsong.add(getResources().getString(R.string.song3));
        listsong.add(getResources().getString(R.string.testsong_20_sec));


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listsong);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(dataAdapter);


        //list.add(new GroupModel("a", "b", "c", "d"));
        recyclerView=(RecyclerView)findViewById(R.id.my_recycler_view1);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        vivzadapter=new GroupDetailsAdapter(MainActivity.this,list);
        recyclerView.setAdapter(vivzadapter);


        pDialog = ProgressDialog.show(MainActivity.this, null, null, true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.getWindow().setGravity(Gravity.CENTER);
        pDialog.setContentView(R.layout.progressdialog);
        pDialog.show();

        LoadDataFireBase loadDataFireBase=new LoadDataFireBase();
        loadDataFireBase.execute();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        selectedSong=new StringBuilder(item);
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




    public class LoadDataFireBase extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
           try {
               FireBaseRetriveData();
           } catch (Exception e) {
               e.printStackTrace();
           }
           return null;
       }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            vivzadapter.notifyItemInserted(list.size() - 1);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vivzadapter.notifyItemRemoved(list.size());
                    for (GroupModel groupModel : list2) {
                        list.add(groupModel);
                        vivzadapter.notifyItemInserted(list.size());
                    }
                    pDialog.dismiss();


                }
            }, 2000);
        }
    }

    public void CreateGroup(View v){
        final String groupName=((TextView)findViewById(R.id.etv_group_name)).getText().toString();
        final int totalClient=((HoloCircleSeekBar)findViewById(R.id.pickerClient)).getValue();
        pDialog.show();
            new Thread(new Runnable(){

                @Override
                public void run() {

                    URL url;
                    BufferedReader bufferedReader = null;
                    InputStreamReader in = null;
                    HttpURLConnection urlConnection = null;
                    try {
                        url = new URL("http://ip2country.sourceforge.net/ip2c.php?format=JSON");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        in = new InputStreamReader(urlConnection.getInputStream());
                        bufferedReader = new BufferedReader(in);
                        String line;
                        StringBuffer buffer = new StringBuffer();
                        while ((line = bufferedReader.readLine()) != null) {
                            buffer.append(line);
                            buffer.append('\r');
                        }
                        bufferedReader.close();
                        in.close();
                        JSONObject json_data = new JSONObject(buffer.toString());
                        ip_address = json_data.getString("ip");
                    } catch (Exception e) {
                        e.printStackTrace();

                        try {
                            ip_address=getPublicIP();

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    } finally {
                        try {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                            if (in != null) {
                                in.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                    if(ip_address!=null){
                        Map<String,Object> map=new HashMap<>();
                        map.put("IP",ip_address);
                        map.put("PORT",port+"");
                        map.put("MP3",selectedSong.toString());
                        myFirebaseRef.child(groupName).updateChildren(map);
                        GroupModel groupModel=new GroupModel(groupName,selectedSong.toString(),ip_address,port+"");
                        Intent intent = new Intent(getBaseContext(), ServerActivity.class);
                        intent.putExtra("DATA",groupModel);
                        intent.putExtra("CLIENT",totalClient);
                        startActivity(intent);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                pDialog.dismiss();

                            }
                        });

                    }
                }
            }).start();

        try{
//            FireBaseRetriveData();
        }catch (Exception e){

        }
    }

    public static String getPublicIP() throws IOException
    {
        Document doc = Jsoup.connect("http://www.checkip.org").get();
        return doc.getElementById("yourip").select("h1").first().select("span").text();
    }

    public void FireBaseRetriveData() throws  Exception
    {
        myFirebaseRef.child("/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    Map<String, Object> root = (Map<String, Object>) snapshot.getValue();

                try {
                    Iterator iterator = root.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry mapentry = (Map.Entry) iterator.next();
                        Object object = mapentry.getValue();
                        if (object instanceof Map) {
                            Map map = (Map) object;
                            GroupModel groupModel = new GroupModel((String) mapentry.getKey(), (String) map.get("MP3"), (String) map.get("IP"), (String) map.get("PORT"));
                            list2.add(groupModel);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

}
