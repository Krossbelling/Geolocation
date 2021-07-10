package Krossbelling.geolocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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


public class MainActivity extends Activity implements UserLocationObjectListener  {

    private final String mapkitKey = "75db84e4-3f8a-471d-8542-95162df4a7d6";
    private MapView mapView;
    private UserLocationLayer userLocationLayer;

    double latitude;
    double longitude;

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
            userLocationLayer.setHeadingEnabled(true);

            userLocationLayer.setObjectListener(this);


        }


    }
    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
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
                Toast.makeText(this, "Максимальная долгота: 180°, Максимальная широта: 90°.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "Введите широту и долготу.", Toast.LENGTH_LONG).show();
        }
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }



    }

    private void Mark() {
        mapView.getMap().move(new CameraPosition(new Point(latitude, longitude), 14, 0, 0));
        Point point = new Point(latitude, longitude);
        // Чтобы очистить все прошлые метки, кроме метки местоположения телефона
        // mapview.getMap().getMapObjects().clear();
        mapView.getMap().getMapObjects().addPlacemark(point, ImageProvider.fromResource(this, R.drawable.mapmarker));
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.5)),
                new PointF((float)(mapView.getWidth() * 0.5), (float)(mapView.getHeight() * 0.83)));



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

    }


}