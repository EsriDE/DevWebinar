define(
	// Import required classes
	[
		"dojo/_base/declare", "esri/symbols/SimpleMarkerSymbol", "esri/symbols/PictureMarkerSymbol",
		"esri/InfoTemplate", "esri/geometry/Point", "esri/renderers/UniqueValueRenderer",
		"GEP/services/GepService", "dojo/dom", "dojo/domReady!"
	],
	// Hand over returned objects of required libraries
	function (declare, SimpleMarkerSymbol, PictureMarkerSymbol, InfoTemplate, Point, UniqueValueRenderer, GepService) {
		var GepServiceMVV = declare(
			//Class package and name
			"de.esri.gep.GepServiceMVV",
			// Super classes
			[GepService],
			// Class definition
			/**
			 *@class This class is the entry point into the application and creates all needed Objects
			 */
			{
				constructor : function (options) {
					this._featureLayer.setRenderer(this.getRenderer());
					this._featureLayer.setInfoTemplate(new InfoTemplate("${name} nach ${lstopname}"));
				},

				getRenderer : function () {
					var defaultSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.STYLE_SQUARE, 10, null, new dojo.Color([255, 255, 0]), 1);
					var renderer = new UniqueValueRenderer(defaultSymbol, "lineno");
					renderer.addValue("1", new PictureMarkerSymbol("icons/S1.png", 36, 16));
					renderer.addValue("2", new PictureMarkerSymbol("icons/S2.png", 36, 16));
					renderer.addValue("3", new PictureMarkerSymbol("icons/S3.png", 36, 16));
					renderer.addValue("4", new PictureMarkerSymbol("icons/S4.png", 36, 16));
					renderer.addValue("6", new PictureMarkerSymbol("icons/S6.png", 36, 16));
					renderer.addValue("7", new PictureMarkerSymbol("icons/S7.png", 36, 16));
					renderer.addValue("8", new PictureMarkerSymbol("icons/S8.png", 36, 16));
					renderer.addValue("20", new PictureMarkerSymbol("icons/S20.png", 36, 16));
					renderer.addValue("27", new PictureMarkerSymbol("icons/S27.png", 36, 16));
					renderer.addValue("(A)", new PictureMarkerSymbol("icons/A.png", 20, 20));
					return renderer;
				}
			}
		);
		return GepServiceMVV;
	}
);