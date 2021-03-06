package com.example.ashutosh.music_player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService;
import com.example.ashutosh.music_player.SoundCloud.SCTrackAdapter;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main2Activity extends AppCompatActivity {

    private List<Track> mListItems ;
    private SCTrackAdapter mAdapter ;
    private TextView mSelectedTrackTitle ;
    private ImageView mSelectedTrackImage ;
    private MediaPlayer mMediaPlayer ;
    private ImageView mPlayerControl ;
    private ImageView mforward ;
    private ImageView catView ;
    private Toolbar tb ;
    public Track track ;
    String artist = "" ;
    public String t ;
    public static String str ;
    String email = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);


        View testView = findViewById(R.id.viewT) ;

        tb = (Toolbar) testView.findViewById(R.id.bar_player) ;
        tb.setVisibility(View.GONE);
        catView = (ImageView) findViewById(R.id.catv) ;
        catView.getLayoutParams().height = 550 ;

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null)
        {
            email = accessToken.getUserId() ;
        }
        else {
            Bundle extras = getIntent().getExtras() ;
            if(extras != null)
            {
                email = extras.getString("em") ;
            }
        }

        mMediaPlayer = new MediaPlayer() ;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause() ;
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.ic_play);
            }
        });

        mListItems = new ArrayList<Track>() ;
        ListView listView = (ListView) findViewById(R.id.track_list_view) ;
        mAdapter = new SCTrackAdapter(this,mListItems) ;
        listView.setAdapter(mAdapter);


        mSelectedTrackTitle = (TextView) findViewById(R.id.selected_track_title) ;
        mSelectedTrackImage = (ImageView) findViewById(R.id.selected_track_image) ;
        mPlayerControl = (ImageView) findViewById(R.id.player_control) ;
        mforward = (ImageView) findViewById(R.id.forward) ;

        mPlayerControl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                togglePlayPause();
            }


        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                track = mListItems.get(position) ;
                String artist = track.getTitle() ;
                int a = artist.indexOf("-")  ;
                int b = artist.indexOf("|")  ;
                int c = artist.indexOf("(")  ;
                int d = (Math.min(Math.min(a,b),c)) ;
                if(d != -1)
                {
                    artist = artist.substring(0,d) ;
                }
                getData(artist);

                    mSelectedTrackTitle.setText(track.getTitle());
                    Picasso.with(Main2Activity.this).load(track.getArtworkURL()).into(mSelectedTrackImage);

                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                    }

                    try {
                        tb.setVisibility(View.VISIBLE);
                        mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                        mMediaPlayer.prepareAsync();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        });

        mforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 30000);
            }
        });


 /*       tb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                str = track.getStreamURL() ;
                Intent intent = new Intent(Main2Activity.this, Player.class) ;
                startActivity(intent);
            }
        }); */


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() ;

        SCService scService = SoundCloud.getService();

        String s1 = getIntent().getStringExtra("category") ;
        String s2 ;
        String s3 = "catv1" ;

        switch(s1)
        {
            case "one" : s2 = "paarth-saxena" ;
                         s3 = "catv3" ;
                          break ;
            case "two" : s2 = "aman-mishra-157201769" ;
                         s3 = "catv6" ;
                          break ;
            case "three" : s2 = "paarth-saxena-18555615" ;
                           s3 = "catv1" ;
                          break ;
            case "four" : s2 = "aman-mishra-529892685" ;
                          s3 = "catv2" ;
                          break ;
            case "five" : s2 = "ashutosh-agarwal-16" ;
                          s3 = "catv4" ;
                          break ;
            case "six" : s2 = "ashutosh-agarwal-89845524" ;
                          s3 = "catv5" ;
                          break ;
            default: s2 = "ashutosh-agarwal-16" ;
        }
        final int resID = getResources().getIdentifier(s3, "drawable", getPackageName()) ;
        scService.getRecentTracks(s2).enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if(response.isSuccessful())
                {
                    List<Track> tracks = response.body() ;
                    loadTracks(tracks) ;
                    catView.setImageResource(resID);
                }
                else
                {
                    showMessage("Error code " + response.code()) ;
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                    showMessage(" Network Error: " + t.getMessage()) ;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void togglePlayPause()
    {
        if(mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
            mPlayerControl.setImageResource(R.drawable.ic_play);
        }
        else
        {
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.ic_pause);
            mforward.setImageResource(R.drawable.forward3);
        }
    }

    private void loadTracks(List<Track> tracks)
    {
        mListItems.clear();
        mListItems.addAll(tracks) ;
        mAdapter.notifyDataSetChanged();
    }

    private void showMessage(String message)
    {
        Toast.makeText(Main2Activity.this, message,Toast.LENGTH_LONG).show();
    }

    void getData(String s)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://itunes.apple.com/search?term=" + s.replace(" ", "+");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    JSONArray jsonArray = response.getJSONArray("results");
                    if(jsonArray.length() != 0)
                    {
                        JSONObject object = jsonArray.getJSONObject(0);
                        String t = (object.optString("artistName", "Unknown"));
                        if(t.indexOf(',') >=0)
                            artist= t.substring(0, t.indexOf(",")) ;
                        else if(t.indexOf('&') >=0)
                            artist = t.substring(0,t.indexOf("&"));
                        else
                            artist = t;
                        com.android.volley.Response.Listener<String> responseListener = new com.android.volley.Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response) ;
                                    boolean success = jsonObject.getBoolean("success") ;
                                    if(success)
                                    {
                                        System.out.println("Successful");
                                    }
                                    else
                                    {
                                        System.out.println("Not Successful");
                                    }
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        } ;

                        SongPushRequest songPushRequest = new SongPushRequest(artist, email, responseListener) ;
                        RequestQueue queue = Volley.newRequestQueue(Main2Activity.this) ;
                        queue.add(songPushRequest) ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new com.android.volley.Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);

    }

}
