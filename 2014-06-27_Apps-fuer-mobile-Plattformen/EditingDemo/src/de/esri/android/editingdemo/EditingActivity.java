package de.esri.android.editingdemo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.LocationDisplayManager.AutoPanMode;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureEditResult;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.geocode.Locator;
import com.esri.core.tasks.geocode.LocatorFindParameters;
import com.esri.core.tasks.geocode.LocatorGeocodeResult;
import com.esri.core.tasks.na.ClosestFacilityParameters;
import com.esri.core.tasks.na.ClosestFacilityResult;
import com.esri.core.tasks.na.ClosestFacilityTask;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.NATravelDirection;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.query.QueryParameters;
import com.esri.core.tasks.query.QueryTask;


public class EditingActivity extends Activity {
	private final String TAG = "EditingDemo";	
	private static final int EDIT_ATTRIBUTE_DIALOG_ID = 1;
	private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	private String baseMapUrl = "https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer";
	private String meetupLayerUrl = "http://services2.arcgis.com/tISIjAqoejGPFbAF/arcgis/rest/services/Events/FeatureServer/0";
	private String meetupQueryUrl = "http://services2.arcgis.com/tISIjAqoejGPFbAF/arcgis/rest/services/Events/FeatureServer/0/query?where=start+%3E+%272014-05-27%27+AND+name+%3D+%27GeoDev+Meet-up%27&objectIds=&time=&geometry=&geometryType=esriGeometryEnvelope&inSR=&spatialRel=esriSpatialRelIntersects&distance=&units=esriSRUnit_Meter&outFields=name%2C+host%2C+place%2C+address%2C+zip%2C+city%2C+state%2C+link%2C+start%2C+end_&returnGeometry=true&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&returnExtentOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&resultOffset=&resultRecordCount=&f=pjson&token=";
	private String geocodeUrl = "http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer";
	private String closestFacilityUrl = "http://route.arcgis.com/arcgis/rest/services/World/ClosestFacility/NAServer/ClosestFacility_World";
	private MapView mapView;
	private ArcGISFeatureLayer meetupLayer;
	private GraphicsLayer graphicsLayer;
	private LocationDisplayManager locationDisplayManager;
	private boolean showingGpsPosition;
	private Locator locator;
	private Point addressLocation;
	private View editAttributesLayout;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        		
        LayoutInflater inflater = this.getLayoutInflater();
        editAttributesLayout = inflater.inflate(R.layout.edit_attributes_dialog, null);

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
		
		Button addMeetupButton = (Button)findViewById(R.id.add_meetup_button);
		addMeetupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(EDIT_ATTRIBUTE_DIALOG_ID);	
			}
		});
		
		Button closestMeetupsButton = (Button)findViewById(R.id.closest_meetups_button);
		closestMeetupsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchClosestMeetups();
			}
		});
		
		locationDisplayManager = mapView.getLocationDisplayManager();
		final ImageButton gpsButton = (ImageButton)findViewById(R.id.gps_button);
		gpsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(showingGpsPosition){
					locationDisplayManager.stop();
					gpsButton.setImageResource(R.drawable.ic_location_on);
					showingGpsPosition = false;
				}else{
					locationDisplayManager.setAutoPanMode(AutoPanMode.LOCATION);
					locationDisplayManager.start();
					gpsButton.setImageResource(R.drawable.ic_location_off);
					showingGpsPosition = true;
				}
			}
		});
		
		locator = Locator.createOnlineLocator(geocodeUrl);
		
		final EditText addressText = (EditText)findViewById(R.id.address_text);
		final ImageButton searchButton = (ImageButton)findViewById(R.id.search_button);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				String address = addressText.getText().toString();
				searchAddress(address);
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
	
	private void searchAddress(String address){
		LocatorFindParameters findParams = new LocatorFindParameters(address);
		findParams.setSourceCountry("DE");
		findParams.setMaxLocations(2);
		findParams.setOutSR(mapView.getSpatialReference());
		new SearchAddressTask().execute(findParams);
	}
	
	private class SearchAddressTask extends AsyncTask<LocatorFindParameters, Void, List<LocatorGeocodeResult>>{

		@Override
		protected List<LocatorGeocodeResult> doInBackground(LocatorFindParameters... params) {
			List<LocatorGeocodeResult> results = null;
			try {
				results = locator.find(params[0]);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return results;
		}
		
		@Override
		protected void onPostExecute(List<LocatorGeocodeResult> result) {
			if(result != null && result.size() > 0){
				addressLocation = result.get(0).getLocation();
				SimpleMarkerSymbol addressSymbol = new SimpleMarkerSymbol(Color.RED, 20, SimpleMarkerSymbol.STYLE.CIRCLE);
				Graphic addressGraphic = new Graphic(addressLocation, addressSymbol);
				graphicsLayer.addGraphic(addressGraphic);
				TextSymbol addressText = new TextSymbol(18, result.get(0).getAddress(), Color.BLACK);
				addressText.setOffsetX(20);
				addressText.setOffsetY(20);
				Graphic textGraphic = new Graphic(addressLocation, addressText);
				graphicsLayer.addGraphic(textGraphic);
				mapView.zoomToResolution(addressLocation, 2);
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

	      case EDIT_ATTRIBUTE_DIALOG_ID:
	        // create the attributes dialog
	        Dialog dialog = new Dialog(this);
	        dialog.setContentView(editAttributesLayout);
	        dialog.setTitle("Attribute editieren");

	        Button editCancelButton = (Button) editAttributesLayout.findViewById(R.id.btn_edit_discard);
	        editCancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissDialog(EDIT_ATTRIBUTE_DIALOG_ID);
				}
			});

	        Button editApplyButton = (Button) editAttributesLayout.findViewById(R.id.btn_edit_apply);
	        editApplyButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(addressLocation != null){
						graphicsLayer.removeAll();
						addNewMeetup(addressLocation);
					}
					dismissDialog(EDIT_ATTRIBUTE_DIALOG_ID);
				}
			});

	        return dialog;
	    }
	    return null;
	}
	
	private void addNewMeetup(Point point){
		try{
			EditText nameEditText = (EditText) editAttributesLayout.findViewById(R.id.name_value);
			EditText hostEditText = (EditText) editAttributesLayout.findViewById(R.id.host_value);
			EditText placeEditText = (EditText) editAttributesLayout.findViewById(R.id.place_value);
			EditText streetEditText = (EditText) editAttributesLayout.findViewById(R.id.street_value);
			EditText plzEditText = (EditText) editAttributesLayout.findViewById(R.id.plz_value);
			EditText cityEditText = (EditText) editAttributesLayout.findViewById(R.id.city_value);
			EditText stateEditText = (EditText) editAttributesLayout.findViewById(R.id.state_value);
			EditText linkEditText = (EditText) editAttributesLayout.findViewById(R.id.link_value);
			EditText sourceEditText = (EditText) editAttributesLayout.findViewById(R.id.source_value);
			EditText startEditText = (EditText) editAttributesLayout.findViewById(R.id.start_value);
			EditText endEditText = (EditText) editAttributesLayout.findViewById(R.id.end_value);
			
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("name", nameEditText.getText().toString());
			attributes.put("host", hostEditText.getText().toString());
			attributes.put("place", placeEditText.getText().toString());
			attributes.put("address", streetEditText.getText().toString());
			attributes.put("zip", plzEditText.getText().toString());
			attributes.put("city", cityEditText.getText().toString());
			attributes.put("state", stateEditText.getText().toString());
			attributes.put("link", linkEditText.getText().toString());
			attributes.put("source", sourceEditText.getText().toString());
	    	Calendar calendar = Calendar.getInstance();
	    	dateFormat.setCalendar(calendar);
	    	calendar.setTime(dateFormat.parse(startEditText.getText().toString()));
			attributes.put("start", Long.valueOf(calendar.getTimeInMillis()));
			calendar.setTime(dateFormat.parse(endEditText.getText().toString()));
			attributes.put("end_", Long.valueOf(calendar.getTimeInMillis()));

			Graphic meetupGraphic = new Graphic(point, null, attributes);
			meetupLayer.applyEdits(new Graphic[]{ meetupGraphic }, null, null, new CallbackListener<FeatureEditResult[][]>() {
				@Override
				public void onCallback(FeatureEditResult[][] result) {
					if (result[0] != null && result[0][0] != null && result[0][0].isSuccess()) {
						Log.i(TAG, "Neues Meetup Event hinzugefügt mit ID: " + result[0][0].getObjectId());
					}
				}
				
				@Override
				public void onError(Throwable e) {
					Log.e(TAG, e.getMessage());
				}
			});			
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
		}
	}
	
	private void searchClosestMeetups(){
		Point location = locationDisplayManager.getPoint();

		SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.BLUE, 14, SimpleMarkerSymbol.STYLE.DIAMOND);
		final Graphic graphic = new Graphic(location, sms);
		
		NAFeaturesAsFeature locationFeature = new NAFeaturesAsFeature();
		locationFeature.addFeature(graphic);
		locationFeature.setSpatialReference(mapView.getSpatialReference());
		
		NAFeaturesAsFeature meetupsFeatures = new NAFeaturesAsFeature();
		meetupsFeatures.setURL(meetupQueryUrl);
		meetupsFeatures.setSpatialReference(mapView.getSpatialReference());
		
		ClosestFacilityParameters cfp = new ClosestFacilityParameters();
		cfp.setReturnFacilities(true);
		cfp.setOutSpatialReference(mapView.getSpatialReference());
		cfp.setTravelDirection(NATravelDirection.TO_FACILITY);
		cfp.setIncidents(locationFeature);
		cfp.setFacilities(meetupsFeatures);
		cfp.setDefaultCutoff(360.0);			// innerhalb von 6 Stunden
		cfp.setDefaultTargetFacilityCount(5);
		
		new ClosestMeetupsTask().execute(cfp);
	}
	
	private class ClosestMeetupsTask extends AsyncTask<ClosestFacilityParameters, Void, ClosestFacilityResult> {
		private ProgressDialog progressDialog;
		
	    @Override
	    protected void onPreExecute() {
	    	progressDialog = ProgressDialog.show(EditingActivity.this, "Nächste Meetups", "Meetups werden gesucht...");
	    }
	    
		@Override
		protected ClosestFacilityResult doInBackground(ClosestFacilityParameters... params) {
			ClosestFacilityResult result = null;
			try{
				UserCredentials uc = new UserCredentials();
				uc.setUserAccount(getString(R.string.arcgis_user), getString(R.string.arcgis_password));
				ClosestFacilityTask closestFacilityTask = new ClosestFacilityTask(closestFacilityUrl, uc);
				result = closestFacilityTask.solve(params[0]);
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(ClosestFacilityResult result) {
			progressDialog.dismiss();
									
			if (result != null) {
				Envelope searchExtent = new Envelope();
				SimpleLineSymbol routeSymbol = new SimpleLineSymbol(Color.BLUE, 3);
				PictureMarkerSymbol destinationSymbol = new PictureMarkerSymbol(getApplicationContext(), 
						getResources().getDrawable(R.drawable.route_destination));
				
				for(Route route : result.getRoutes()){
					Graphic routeGraphic = new Graphic(route.getRouteGraphic().getGeometry(), routeSymbol);
					Graphic endGraphic = new Graphic(((Polyline) routeGraphic.getGeometry()).getPoint(((Polyline) routeGraphic.getGeometry()).getPointCount() - 1), destinationSymbol);
					graphicsLayer.addGraphics(new Graphic[] { routeGraphic, endGraphic });
					searchExtent.merge(route.getEnvelope());
				}
				mapView.setExtent(searchExtent, 150);
			} else {
				Toast.makeText(EditingActivity.this, "Kein Meetup gefunden.", Toast.LENGTH_LONG).show();
			}
		}
	}
}