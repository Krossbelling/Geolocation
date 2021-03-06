package Krossbelling.geolocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

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


public class MainActivity extends Activity implements UserLocationObjectListener {

    private final String mapkitKey = "75db84e4-3f8a-471d-8542-95162df4a7d6";
    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    double latitude;
    double longitude;
    double[] latitudeArray;
    double[] longitudeArray;
    static final private int CHOOSE_THIEF = 0;

    static boolean firstStart = true;

    String urlString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(mapkitKey);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        mapView = (MapView)findViewById(R.id.mapview);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }
        else {
            mapView.getMap().setRotateGesturesEnabled(false);
            mapView.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));
            MapKit mapKit = MapKitFactory.getInstance();
            userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
            userLocationLayer.setVisible(true);
            userLocationLayer.setHeadingEnabled(false);
            userLocationLayer.setObjectListener(this);


            new fetchData().start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                userLocationLayer.resetAnchor();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent,CHOOSE_THIEF);
            case R.id.action_about_app:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_THIEF){
            if(resultCode == RESULT_OK){
                urlString = data.getStringExtra(SettingsActivity.THIEF);
                new fetchData().start();
            }
            else {
                urlString ="";
            }
        }
    }

    class  fetchData extends  Thread{

        String data = "";

        @Override
        public void run() {
            try {
                if(urlString != null){
                    // URL url = new URL("https://api.npoint.io/8b4e757fc55c1c0fb110");
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine())!=null){
                        data = data + line;
                    }

                    if (!data.isEmpty()){
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray mark = jsonObject.getJSONArray("Mark");
                        latitudeArray = new double[mark.length()];
                        longitudeArray = new double[mark.length()];
                        for (int i = 0; i < mark.length(); i++){
                            JSONObject ids = mark.getJSONObject(i);
                            String latitudeStr = ids.getString("latitude");
                            latitudeArray[i] = Double.parseDouble(latitudeStr);
                            String longitudeStr = ids.getString("longitude");
                            longitudeArray[i] = Double.parseDouble(longitudeStr);

                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    public void onClick(View view) {
        EditText latitudeEdit = findViewById(R.id.editTextNumberDecimal);
        EditText longitudeEdit = findViewById(R.id.editTextNumberDecimal2);
        if(latitudeEdit.getText().length() != 0 && longitudeEdit.getText().length() != 0){
            latitude = Double.parseDouble(latitudeEdit.getText().toString());
            longitude = Double.parseDouble(longitudeEdit.getText().toString());

            if(latitude<=90 && longitude<=180 && latitude>=-90 && longitude>=-180) {
                Mark();

            }
            else{
                Toast.makeText(this, "???????????????????????? ??????????????: 180??, ???????????????????????? ????????????: 90??.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "?????????????? ???????????? ?? ??????????????.", Toast.LENGTH_LONG).show();
        }
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void Mark(double latitude, double longitude) {
        userLocationLayer.resetAnchor();
        Point point = new Point(latitude, longitude);
        mapView.getMap().getMapObjects().addPlacemark(point, ImageProvider.fromResource(this, R.drawable.mapmarker));

    }

    private void Mark() {
        userLocationLayer.resetAnchor();
        mapView.getMap().move(new CameraPosition(new Point(latitude, longitude), 14, 0, 0));
        Point point = new Point(latitude, longitude);
        mapView.getMap().getMapObjects().addPlacemark(point, ImageProvider.fromResource(this, R.drawable.mapmarker));
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        if(firstStart){
            userLocationLayer.setAnchor(
                    new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                    new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));
            firstStart = false;

        }
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, R.drawable.mapmarker),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {
        try {
            for(int i = 0; i< latitudeArray.length; i++){
                Mark(latitudeArray[i], longitudeArray[i]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


}