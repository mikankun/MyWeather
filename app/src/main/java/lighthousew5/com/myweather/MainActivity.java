package lighthousew5.com.myweather;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void show(View v){

        AsyncTask<Void, Integer, JSONObject> task = new AsyncTask<Void, Integer, JSONObject>(){
            private ProgressDialog pd;

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(MainActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("処理中・・・");
                pd.setCancelable(false);
                pd.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                Log.v("JSONT", "START");
                // サンプルのRSSサイト
                //String uri = "http://maps.googleapis.com/maps/api/geocode/json?latlng=35.689509,%20139.700518&sensor=true&language=ja";
                String uri ="http://weather.livedoor.com/forecast/webservice/json/v1?city=140010";

                // RSSデータを保存するArrayList
                // HTTPクライアント作成
                HttpClient client = new DefaultHttpClient();
                String jsonTxt = "";
                HttpGet get = new HttpGet();
                String result ="";
                JSONObject json = null;
                try{
                    get.setURI(new URI(uri));

                    // GETリクエストを実行してレスポンスを取得
                    HttpResponse res = client.execute(get);
                    // レスポンスからInputStreamを取得
                    InputStream in = res.getEntity().getContent();

                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String x = "";
                    while( (x = br.readLine()) != null){
                        jsonTxt +=new String(x.getBytes(),"utf-8");
                        Log.v("JSONT",x);
                    }

                    json = new JSONObject(jsonTxt);
                    //result = json.getJSONArray("forecasts").getJSONObject(0).getString("forecasts");

                    //result = json.getJSONArray("forecasts").getJSONObject(0).toString();

                    //result = json.toString();

                }catch(Exception e){
                    e.printStackTrace();
                }
                return json;

            }
            @Override
            protected void onPostExecute(JSONObject result) {
                try {
                    //予報時刻
                    String publicTime = result.getString("publicTime");
                    TextView tv2 = (TextView) findViewById(R.id.textView2);
                    tv2.setText(publicTime);
                    //天気概況
                    String text = result.getJSONObject("description").getString("text");
                    TextView tv3 = (TextView) findViewById(R.id.textView3);
                    tv3.setText(text);

                    //天気予報
                    ArrayList<Map<String,String>> list = new ArrayList<>();
                    JSONArray arry = result.getJSONArray("forecasts");
                    for( int i = 0; i < arry.length(); i++){
                        JSONObject obj = arry.getJSONObject(i);
                        HashMap<String,String> map = new HashMap<>();
                        map.put("dateLabel", obj.getString("dateLabel"));
                        map.put("telop",obj.getString("telop"));


                        //map.put("max",obj.getJSONObject("temperature").getJSONObject("max").getString("celsius"));
                        JSONObject temp = obj.getJSONObject("temperature");
                        JSONObject max = temp.optJSONObject("max");
                        if( max != null){
                            map.put("max",max.optString("celsius"));

                        }else {
                            map.put("max", "不明");
                        }
                        list.add(map);
                    }

                    ListView listView = (ListView) findViewById(R.id.listView);
                    String[] keys ={"dateLabel","telop","max"};
                    int[] ids ={R.id.textView4,R.id.textView5,R.id.textView6};
                    SimpleAdapter adapter =
                            new SimpleAdapter(MainActivity.this, list,R.layout.list,keys,ids);

                    listView.setAdapter(adapter);
                    pd.dismiss();
                }catch(Exception e){
                        e.printStackTrace();
                }
            }
        };
        Toast.makeText(this, "AA",Toast.LENGTH_LONG).show();
        task.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
