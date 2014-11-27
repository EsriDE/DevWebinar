package de.esri.android.datasourcedemo;

import java.io.File;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.GroupLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.ogc.KMLLayer;
import com.esri.android.map.ogc.WMSLayer;
import com.esri.android.map.osm.OpenStreetMapLayer;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;


public class DataSourceActivity extends Activity {
	private static final String TAG = "DataSource";
	private String baseMapUrl = "https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer";
	private String featureLayerUrl = "http://services2.arcgis.com/tISIjAqoejGPFbAF/arcgis/rest/services/Events/FeatureServer/0";
	private String wmsUrl = "http://www.wms.nrw.de/gd/guek500?";
	private MapView mapView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mapView = (MapView) findViewById(R.id.map);
                    
		try {
			// Tiled layer
	        ArcGISTiledMapServiceLayer tiledLayer = new ArcGISTiledMapServiceLayer(baseMapUrl);
	        mapView.addLayer(tiledLayer);
	        
	        // Dynamic layer
	        ArcGISDynamicMapServiceLayer dynamicLayer = new ArcGISDynamicMapServiceLayer(baseMapUrl);
	        dynamicLayer.setVisible(false);
	        mapView.addLayer(dynamicLayer);
	        
	        // Feature layer
	        ArcGISFeatureLayer featureLayer = new ArcGISFeatureLayer(featureLayerUrl, MODE.ONDEMAND);
	        mapView.addLayer(featureLayer);
	        
	        // Graphics Layer
	        GraphicsLayer graphicsLayer = new GraphicsLayer();
	        Point point = new Point(1280892.3, 6094477.8);
	        SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(Color.BLUE, 18, STYLE.CIRCLE);
	        Graphic graphic = new Graphic(point, markerSymbol);
	        graphicsLayer.addGraphic(graphic);
	        mapView.addLayer(graphicsLayer);
	        
	        // Offline tiled layer
	        String tpkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ArcGis/München.tpk";
		    ArcGISLocalTiledLayer localTiledLayer = new ArcGISLocalTiledLayer(tpkPath);
		    mapView.addLayer(localTiledLayer);
	        
			// Offline feature layer   
			String gdbFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ArcGis/Offline.geodatabase";
			Geodatabase geodatabase = new Geodatabase(gdbFilePath);
			GeodatabaseFeatureTable featureTable = geodatabase.getGeodatabaseFeatureTableByLayerId(0);
			FeatureLayer localFeatureLayer = new FeatureLayer(featureTable);
			mapView.addLayer(localFeatureLayer);
			
	        // Group layer
	        GroupLayer groupLayer = new GroupLayer();	        
	        groupLayer.setVisible(false);
	        mapView.addLayer(groupLayer);
	        
	        // WMS Layer
	        SpatialReference webMercator = SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE); 
	        WMSLayer wmsLayer = new WMSLayer(wmsUrl, webMercator);
	        groupLayer.addLayer(wmsLayer);
	        
	        // OSM layer
	        OpenStreetMapLayer osmLayer = new OpenStreetMapLayer();
	        groupLayer.addLayer(osmLayer);
	        
	        // KML layer
	        String kml = "http://www.gpsies.com/download.do?fileId=pwrgotohpgqfnkqi&filetype=kml";
	        KMLLayer kmlLayer = new KMLLayer(kml, webMercator);
	        groupLayer.addLayer(kmlLayer);
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
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