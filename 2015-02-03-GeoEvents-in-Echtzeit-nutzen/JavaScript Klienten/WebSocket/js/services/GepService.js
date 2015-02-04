define(
	// Import required classes
	[
		"dojo/_base/declare", "dojo/json", "dojo/_base/array",
		"esri/symbols/SimpleMarkerSymbol", "esri/renderers/SimpleRenderer",
		"esri/layers/FeatureLayer", "esri/graphic", "esri/geometry/Point",
		"dijit/form/CheckBox", "esri/symbols/SimpleLineSymbol",
		"esri/geometry/Polyline", "esri/Color", "dojo/dom", "dojo/domReady!"
	],
	// Hand over returned objects of required libraries
	function (declare, JSON, array, SimpleMarkerSymbol, SimpleRenderer,
			FeatureLayer, Graphic, Point, CheckBox, SimpleLineSymbol, Polyline, Color) {
		var GepService = declare(
			//Class package and name
			"de.esri.gep.GepService",
			// Super classes
			[
			],
			// Class definition
			/**
			 *@class This class is the entry point into the application and creates all needed Objects
			 */
			{
				_layerName : null,
				_geometryField : null,
				_trackIdField : null,
				_attributeList : null,
				_objectIdField : null,
				_objectId : null,
				_featureLayer : null,
				_webSocketUrl : null,
				_tracks : null,
				_webSocket : null,
				_infoTemplate : null,
				_trackHistoryLimit : null,

				constructor : function (options) {
					console.log("GepService.constructor(" + options + ")");
					this._objectId = {
						_id : 0,
						getNext : function () {
							this._id++;
							return this._id;
						}
					};

					this._webSocketUrl = options.webSocketUrl;
					this._trackHistoryLimit = options.trackHistoryLimit;
					this._tracks = {};
					this._attributeList = [];
					this._objectIdField = "ObjectId";

					this.loadGeoEventDefinition(options.geoEventDefinitionUrl, options.geoEventDefinition);
					this.createLayer();

					var service = this;
					var checkBox = new CheckBox({
						name: options.checkBoxName,
						value: "Visibility",
						checked: true,
						onChange: function (newValue) {service.setVisibility(newValue); }
					}, options.checkBoxName);
				},

				setVisibility : function (newValue) {
					this._featureLayer.setVisibility(newValue);
				},

				loadGeoEventDefinition : function (url, name) {
					var geoEventDefTypeLookup = {
						//Types used by GeoEvent Processor
						"String" : "esriFieldTypeString",
						"Double" : "esriFieldTypeDouble",
						"Date" : "esriFieldTypeDate",
						"Geometry" : "esriGeometryPoint"
					};

					try {
						var xmlhttp = new XMLHttpRequest();
						xmlhttp.open("GET", url, false);
						xmlhttp.send();
						var fullJson = JSON.parse(xmlhttp.responseText);
						array.forEach(fullJson.geoEventDefinitions, function (def) {
							if (def.name === name) {
								this._layerName = def.name;
								array.forEach(def.fieldDefinitions, function (field) {
									if (field.type === "Geometry") {
										this._geometryField = field.name;
									} else {
										this._attributeList.push({
											"name" : field.name,
											"type" : geoEventDefTypeLookup[field.type]
										});
									}
									if (field.fieldDefinitionTag && field.fieldDefinitionTag[0] === "TRACK_ID") {
										this._trackIdField = field.name;
									}
								}, this);
							}
						}, this);
					} catch (e) {
						console.log("Fehler in GeoEventServiceControl.loadGeoEventDefinition()");
					}
				},

				createLayer : function () {
					this._attributeList.push({
						"name" : this._objectIdField,
						"type" : "esriFieldTypeInteger"
					});
					var layerDefinition = {
						"objectIdField" : this._objectIdField,
						"fields" : this._attributeList
					};
					this._featureLayer = new FeatureLayer({
						"layerDefinition" : layerDefinition,
						"featureSet" : null
					});
					//fake layerInfos, so a TOC can be generated
					var layerInfos = [{
						"id" : 0,
						"name" : this._layerName,
						"parentLayerId" : -2
					}];
					this._featureLayer.layerInfos = layerInfos;
					this._featureLayer.visibleLayers = [0];
					var defaultSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.STYLE_SQUARE, 10, null, new Color([255, 255, 0]), 1);
					this._featureLayer.setRenderer(new SimpleRenderer(defaultSymbol));
				},

				startService : function () {
					var service = this;
					this._webSocket = new WebSocket(this._webSocketUrl);
					this._webSocket.onopen = function (evt) { service.onOpenWs(evt); };
					this._webSocket.onclose = function (evt) { service.onCloseWs(evt); };
					this._webSocket.onmessage = function (evt) { service.onMessageWs(evt); };
					this._webSocket.onerror = function (evt) { service.onErrorWs(evt); };
				},

				stopService : function () {
					this._webSocket.disconnect();
				},

				onOpenWs : function (evt) {
					var now = new Date();
					console.log("CONNECTED " + this._webSocketUrl + " " + now.getHours() + ":" + now.getMinutes());
				},

				onCloseWs : function (evt) {
					var now = new Date();
					console.log("DISCONNECTED " + this._webSocketUrl + " " + now.getHours() + ":" + now.getMinutes());
				},

				getGeometry : function (jsonData) {
					return new Point(jsonData[this._geometryField]);
				},

				onMessageWs : function (evt) {
					var newFeatures = [];
					var updateFeatures = [];

					try {
					
					var rawData = "" + evt.data;
					var jsonData = JSON.parse(rawData);
					var geometry = this.getGeometry(jsonData);
					var attributes = {};
					array.forEach(this._attributeList, function (field) {
						attributes[field.name] = jsonData[field.name];
					}, this);
					var bahn = this._tracks[jsonData[this._trackIdField]];

					if (!bahn) {
						attributes[this._objectIdField] = this._objectId.getNext();

						var graphic = new Graphic(geometry, null);
						graphic.setAttributes(attributes);
						this._tracks[jsonData[this._trackIdField]] = {
							"latest" : graphic,
							"tail" : null
						};
						newFeatures.push(this._tracks[jsonData[this._trackIdField]].latest);
					} else {
						attributes[this._objectIdField] = bahn.latest.attributes[this._objectIdField];
						bahn.latest.setGeometry(geometry);
						bahn.latest.setAttributes(attributes);
						updateFeatures.push(bahn.latest);
					}
					if (this._trackHistoryLimit && this._trackHistoryLimit > 1) {
						if (bahn && !bahn.tail) {
							var polyline = Polyline();
							polyline.addPath([bahn.latest.geometry, geometry]);
							var _lineSymbol = new SimpleLineSymbol(
								/*Style:*/SimpleLineSymbol.STYLE_SOLID,
								/*Color:*/new Color([0, 0, 0]),
								/*Width:*/1
							);
							var tailGraphics = new Graphic(polyline, _lineSymbol);
							bahn.tail = tailGraphics;
							newFeatures.push(bahn.tail)
						} else if (bahn && bahn.tail) {
							var path = bahn.tail.geometry.paths[0];
							var lastPoint = path[path.length - 1];
							if (lastPoint[0] !== geometry.x || lastPoint[1] !== geometry.y) {
								bahn.tail.geometry.insertPoint(0, bahn.tail.geometry.paths[0].length, geometry);
								if (this._trackHistoryLimit && bahn.tail.geometry.paths[0].length > this._trackHistoryLimit) {
									//remove oldest Point in tracks tail and delete object
									var removedPoint = bahn.tail.geometry.removePoint(0, 0);
									delete removedPoint;
								}
								updateFeatures.push(bahn.tail);
							}
						}
					}
					this._featureLayer.applyEdits(newFeatures, updateFeatures, null);
					this._webSocket.send("data received");
					} catch (e) {
						console.log("Fehler in onMessageWs " + e);
					}
				},

				onErrorWs : function (evt) {
					console.log("Error " + this._webSocketUrl + ":" + evt.data);
				}
			}
		);
		return GepService;
	}
);