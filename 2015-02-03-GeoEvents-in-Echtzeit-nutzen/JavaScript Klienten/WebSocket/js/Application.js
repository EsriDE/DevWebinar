define(
	// Import required classes
	[
		"dojo/_base/declare", "esri/map", "esri/dijit/BasemapGallery", "esri/dijit/Bookmarks", 
		"esri/dijit/HomeButton", "esri/dijit/LocateButton",
		"GEP/services/GepServiceMVV",
		"dojo/dom", "dojo/domReady!"
	],
	// Hand over returned objects of required libraries
	function (declare, Map, BasemapGallery, Bookmarks, HomeButton, LocateButton, GepServiceMVV) {
		var thisClass = declare(
			//Class package and name
			"de.esri.gep.Application",
			// Super classes
			[
			],
			// Class definition
			/**
			 *@class This class is the entry point into the application and creates all needed Objects
			 */
			{
				map : null,
				bookmarks : null,
				bahnService : null,
				newsService : null,
				weatherService : null,
				earthquakeService : null,
				funkService : null,
				flightService : null,

				constructor : function () {

					// Initialize map
					this.map = new Map("mapDiv", {
						basemap: "streets",
						zoom: 3
					});

					// Add Basemap Gallery
					var basemapGallery = new BasemapGallery({
						showArcGISBasemaps: true,
						map: this.map
					}, "basemapGallery");
					basemapGallery.startup();
					basemapGallery.on("error", function (error) {console.log("Error BasemapGallery: " + error); });

					// Initialize and add Bookmarks
					var bookmarks_list = [{
						"extent": {
							"spatialReference": {
								"wkid": 102100
							},
							"xmin": -521950.5575206075,
							"ymin": 5710373.121152867,
							"xmax": 2765233.878099917,
							"ymax": 7653809.865185271
						},
						"name": "Germany"
					}, {
						"extent": {
							"spatialReference": {
								"wkid": 102100
							},
							"xmin": 1239663.8249445548,
							"ymin": 6106891.067630515,
							"xmax": 1342388.2874883881,
							"ymax": 6167623.435688525
						},
						"name": "Munich"
					}, {
						"extent": {
							"spatialReference": {
								"wkid": 102100
							},
							"xmin": -14097248,
							"ymin": 3674779,
							"xmax": -12190128,
							"ymax": 4991348
						},
						"name": "California"
					}];
					this.bookmarks = new Bookmarks({
						map: this.map,
						bookmarks: bookmarks_list
					}, "bookmarks");

					var home = new HomeButton({
						map: this.map
					}, "HomeButton");
					home.startup();

					var geoLocate = new LocateButton({
						map: this.map
					}, "LocateButton");
					geoLocate.startup();
					
					// Add MVV data
					this.bahnService = new GepServiceMVV({
						"webSocketUrl" : "wss://localhost:6143/sbahn",
						"geoEventDefinitionUrl" : "http://localhost:6180/geoevent/rest/.json",
						"geoEventDefinition" : "sBahn",
						"checkBoxName" : "checkBoxMVV",
						"trackHistoryLimit" : 0
					});
					this.map.addLayer(this.bahnService._featureLayer);
					this.bahnService.startService();
				}
			}
		);
		return thisClass;
	}
);