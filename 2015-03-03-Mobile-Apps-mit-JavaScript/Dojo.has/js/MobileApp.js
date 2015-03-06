define(["dojo/_base/declare", "dojo/has", "dojo/has!touch?dojo/touch:dojo/mouse", "dojo/dom", 
    "esri/map", 
    "esri/dijit/LocateButton",
    "esri/arcgis/utils",
    "esri/dijit/Popup", 
	"esri/dijit/PopupTemplate",
    "esri/dijit/PopupMobile",
	"esri/symbols/SimpleFillSymbol", 
	"esri/Color",
    "dojo/dom-construct",
	"dojo/domReady!"], 
function (declare, has, hid, dom,
	Map,
	LocateButton,
	arcgisUtils,
	Popup,
	PopupTemplate,
	PopupMobile,
	SimpleFillSymbol,
	Color,
	domConstruct) {
	return declare(null, {
		map : null,
		
		constructor : function () {
			if(has("touch")){
				dom.byId("output").innerHTML = "You have a touch capable device and so I loaded <code>dojo/touch</code>.";
			this.initMobileMap();
			}else{
				dom.byId("output").innerHTML = "You do not have a touch capable device and so I loaded <code>dojo/mouse</code>.";
			this.initMap();
			}
		}, 

		initMobileMap : function() {
			var popup = new PopupMobile(null, domConstruct.create("div"));
			this.startMap(popup);
		}, 

		initMap : function() {
			var fill = new SimpleFillSymbol("solid", null, new Color("#A4CE67"));
			var popup = new Popup({
				fillSymbol: fill,
				titleInBody: false
			}, domConstruct.create("div"));
			this.startMap(popup);
		},
		
		startMap : function(popup) {
			map = arcgisUtils.createMap("1df512c380994cc5a3dad2e2d428eea3", "mapDiv",{
				mapOptions: {
				  center: [-87.62, 41.89],
				  zoom: 10,
				  infoWindow: popup
				}
			});

			}
	});
});

