package Krossbelling.geolocation;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
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



public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {
    private MapView mapview;
    private UserLocationLayer userLocationLayer;

    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MapKitFactory.setApiKey("75db84e4-3f8a-471d-8542-95162df4a7d6");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        mapview = findViewById(R.id.mapview);
        mapview.getMap().setRotateGesturesEnabled(false);
        mapview.getMap().move(new CameraPosition(new Point(0, 0), 14, 0, 0));

        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapview.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);

        userLocationLayer.setObjectListener(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        mapview.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapview.onStart();

    }
    public void onClick(View view) {

        EditText latitudeEdit = findViewById(R.id.editTextNumberDecimal);
        latitude = Double.parseDouble(latitudeEdit.getText().toString());
        EditText longitudeEdit = findViewById(R.id.editTextNumberDecimal2);
        longitude = Double.parseDouble(longitudeEdit.getText().toString());

        mapview.getMap().move(new CameraPosition(new Point(latitude, longitude), 14, 0, 0));
        Point point = new Point(latitude, longitude);
        // Чтобы очистить все прошлые метки, кроме метки местоположения телефона
        // mapview.getMap().getMapObjects().clear();
        mapview.getMap().getMapObjects().addPlacemark(point, ImageProvider.fromResource(this, R.drawable.mapmarker));


    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(mapview.getWidth() * 0.5), (float)(mapview.getHeight() * 0.5)),
                new PointF((float)(mapview.getWidth() * 0.5), (float)(mapview.getHeight() * 0.83)));



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
    public void onObjectRemoved(UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(UserLocationView userLocationView, ObjectEvent objectEvent) {

    }
}