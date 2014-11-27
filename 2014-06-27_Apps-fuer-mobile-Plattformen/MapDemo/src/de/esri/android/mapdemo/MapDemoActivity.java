package de.esri.android.mapdemo;

import android.app.Activity;
import android.os.Bundle;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;


public class MapDemoActivity extends Activity {
	private String baseMapUrl = "https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer";
	private MapView mapView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		mapView = (MapView) findViewById(R.id.map);
		
		ArcGISTiledMapServiceLayer baseMapLayer = new ArcGISTiledMapServiceLayer(baseMapUrl);
		mapView.addLayer(baseMapLayer);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mapView.pause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mapView.unpause();
	}
}