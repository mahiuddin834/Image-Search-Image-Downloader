package com.itnation.imagesearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.startapp.sdk.adsbase.StartAppAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    DrawerLayout drawerLayout;
    MaterialToolbar materialToolbar;
    NavigationView navigationView;

    EditText search_EditText;
    ImageView search_icon;
    ProgressBar progressBar;
    RecyclerView image_recycler;

    ArrayList<String> imageList;

    ImageAdapter imageAdapter;

    LinearLayout Liniare_lay;
    RelativeLayout image_Relative;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartAppAd.disableSplash();

        drawerLayout = findViewById(R.id.drawer_layout);
        materialToolbar = findViewById(R.id.toolbar_layout);
        navigationView = findViewById(R.id.navigation_view);
        image_recycler = findViewById(R.id.image_recycler);
        progressBar=findViewById(R.id.progress_bar);
        search_EditText= findViewById(R.id.search_EditText);
        search_icon= findViewById(R.id.search_icon);
        Liniare_lay= findViewById(R.id.Liniare_lay);
        image_Relative= findViewById(R.id.image_Relative);

        imageList= new ArrayList<>();









        ActionBarDrawerToggle actionBarDrawerToggle= new ActionBarDrawerToggle
                (MainActivity.this, drawerLayout, materialToolbar, R.string.opendrawer, R.string.closedrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.home){

                    image_Relative.setVisibility(View.GONE);
                    Liniare_lay.setVisibility(View.VISIBLE);
                    drawerLayout.closeDrawer(GravityCompat.START);



                }else if (item.getItemId()==R.id.download_image){

                    Toast.makeText(MainActivity.this,"This is Download Image", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);

                } else if (item.getItemId()==R.id.search_history) {

                    Toast.makeText(MainActivity.this,"This is Search History", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);

                }else if (item.getItemId()== R.id.feedback) {


                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    }


                    drawerLayout.closeDrawer(GravityCompat.START);

                }


                return true;
            }
        });


        //===============================================================================


        GridLayoutManager gridLayoutManager= new GridLayoutManager(MainActivity.this, 2);
        image_recycler.setLayoutManager(gridLayoutManager);
        imageAdapter = new ImageAdapter(imageList,MainActivity.this);
        image_recycler.setAdapter(imageAdapter);




        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String searchString = search_EditText.getText().toString();



                if (searchString.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Entre Your Search Query",Toast.LENGTH_SHORT).show();
                }else {


                    Liniare_lay.setVisibility(View.GONE);
                    image_Relative.setVisibility(View.VISIBLE);
                    searchImage();

                }


            }
        });






    }

    ///========================




    private void searchImage() {

        imageList.clear();
        progressBar.setVisibility(View.VISIBLE);

        String searchText = search_EditText.getText().toString();
        ImageViewActivity.imageName= searchText;

        String searchURL = "https://api.pexels.com/v1/search?query="+ searchText +"&per_page=78&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, searchURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                JSONArray photosArray=null;

                try {

                    photosArray = response.getJSONArray("photos");
                    for (int i=0; i<photosArray.length(); i++){
                        JSONObject photoObject = photosArray.getJSONObject(i);
                        String ImageUrl= photoObject.getJSONObject("src").getString("large");
                        imageList.add(ImageUrl);


                    }

                    imageAdapter.notifyDataSetChanged();


                }catch (JSONException e){
                    e.printStackTrace();
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Failed to load wallpaper", Toast.LENGTH_SHORT).show();


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "1PQ8rtNVnbpnM2CRvjk8RUF8afM1ZwLQ5AnbF4bddtM3bEOqgywZv1jm");

                return headers;


            }
        };

        requestQueue.add(jsonObjectRequest);






    }





}