package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.internal.LoginAuthorizationType;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // google sign in
    private GoogleApiClient googleApiClient;

    private NavigationView navigationView;

    private ImageView navHeaderProfilePic;
    private TextView navHeaderUserName;
    private TextView navHeaderUserEmail;

    private static final String TAG = "MainActivity ******* ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        navHeaderProfilePic = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.navHeaderProfilePic);
        navHeaderUserName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.navHeaderUserName);
        navHeaderUserEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.navHeaderUserEmail);

        if (firebaseUser.getPhotoUrl() != null){
            String photoUrl = firebaseUser.getPhotoUrl().toString();
            //new MainActivity.ImageLoadTask(imageUrl, navHeaderProfilePic).execute();
            Picasso.with(getApplicationContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.circle_white)
                    .resize(100, 100)
                    //.transform(new CircleTransform())
                    .centerCrop()
                    .into(navHeaderProfilePic);
        }else {
            Log.d(TAG, "onStart: no profile picture");
        }

        navHeaderUserName.setText(firebaseUser.getDisplayName());
        navHeaderUserEmail.setText(firebaseUser.getEmail());

        FragmentManager manager = getSupportFragmentManager();
        HomeFragment homeFragment = new HomeFragment();

        manager.beginTransaction().replace(R.id.content_main, homeFragment).commit();
    }

    // getting profile picture if user logs in with facebook or google
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find) {
            // Handle the camera action
        } else if (id == R.id.nav_favs) {

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_events) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            // firebase user sign out
            firebaseAuth.signOut();
            // google sign out - seems to work without this code?
            if (googleApiClient != null){
                Log.d(TAG, "onClick: signing out google user");
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Log.d(TAG, "onClick: sign out successful");
                            }
                        });
            }
            // firebase user facebook sign out
            LoginManager.getInstance().logOut();
            // start loginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            // end main activity
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
