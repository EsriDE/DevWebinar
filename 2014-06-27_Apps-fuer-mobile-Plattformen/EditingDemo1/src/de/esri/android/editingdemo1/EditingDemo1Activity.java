package de.esri.android.editingdemo1;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;


public class EditingDemo1Activity extends Activity {
	private final String TAG = "EditingDemo";
	private String baseMapUrl = "https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer";
	private String meetupLayerUrl = "http://services2.arcgis.com/tISIjAqoejGPFbAF/arcgis/rest/services/Events/FeatureServer/0";
	private MapView mapView;
	private ArcGISFeatureLayer meetupLayer;
	private GraphicsLayer graphicsLayer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mapView = (MapView) findViewById(R.id.map);

		ArcGISTiledMapServiceLayer baseMapLayer = new ArcGISTiledMapServiceLayer(baseMapUrl);
		mapView.addLayer(baseMapLayer);
		
		meetupLayer = new ArcGISFeatureLayer(meetupLayerUrl, MODE.ONDEMAND);
		mapView.addLayer(meetupLayer);
		
		graphicsLayer = new GraphicsLayer();
		mapView.addLayer(graphicsLayer);
		
		mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
			private static final long serialVersionUID = 1L;
	         
			@Override
			public void onStatusChanged(Object source, STATUS status) {
		         if (source == mapView && status == OnStatusChangedListener.STATUS.INITIALIZED) {
		        	 Envelope extent = new Envelope(550000.0, 5800000.0, 1750000.0, 7500000.0);
		        	 mapView.setExtent(extent);
		         }
			}
		});
		
		Button searchMeetupsButton = (Button)findViewById(R.id.search_meetups_button);
		searchMeetupsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchMeetups();
			}
		});

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
	
	private void searchMeetups(){
		graphicsLayer.removeAll();
		QueryAsyncTask queryTask = new QueryAsyncTask();
		queryTask.execute();
	}
	
	private class QueryAsyncTask extends AsyncTask<String, Void, FeatureResult>{

		@Override
		protected FeatureResult doInBackground(String... params) {
			FeatureResult results = null;
			try {
				QueryParameters parameters = new QueryParameters();
				parameters.setOutSpatialReference(SpatialReference.create(102100));
				parameters.setReturnGeometry(true);
				String where = "state = 'Deutschland'";
				parameters.setWhere(where);
				parameters.setGeometry(new Envelope(1080000.0, 6000000.0, 1500000.0, 6500000.0));
				
				QueryTask queryTask = new QueryTask(meetupLayerUrl);
				results = queryTask.execute(parameters);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return results;
		}

		@Override
		protected void onPostExecute(FeatureResult result) {
			if(result != null){
				for (Object element : result) {
					if (element instanceof Feature) {
						Feature feature = (Feature) element;
						SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.RED, 18, STYLE.CROSS);
						Graphic graphic = new Graphic(feature.getGeometry(), symbol, feature.getAttributes());
						graphicsLayer.addGraphic(graphic);
					}
				}
			}
		}
	}
}