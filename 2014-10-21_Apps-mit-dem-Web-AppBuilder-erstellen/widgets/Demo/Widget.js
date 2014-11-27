define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/_base/array',
    'jimu/BaseWidget',
    'esri/tasks/IdentifyTask',
    'esri/tasks/IdentifyParameters'
  ],
function(declare, lang, array, BaseWidget, IdentifyTask, IdentifyParameters) {
  //Ableiten von BaseWidget.
  return declare([BaseWidget], {

    //please note that this property is be set by the framework when widget is loaded.
    baseClass: 'jimu-widget-demo',

    name: 'Demo',

    identifyParams: null,

    onClickListener: null,

    serviceUrls: [],

    resultString: "",

    postCreate: function() {
      this.inherited(arguments);
      console.log('postCreate Identify');
    },

	startup: function() {
      console.log("startup Identify");
      this.inherited(arguments);

      // Parameter für Identify erstellen
	  this.identifyParams = new IdentifyParameters();
      this.identifyParams.tolerance = 3;
      this.identifyParams.returnGeometry = true;
      this.identifyParams.layerOption = IdentifyParameters.LAYER_OPTION_ALL;
      this.identifyParams.width = this.map.width;
      this.identifyParams.height = this.map.height;

      array.forEach(this.map.layerIds, function(id) {
        var layer = this.map.getLayer(id);
        if (id !== "defaultBasemap" && layer.url !== null)
        {
          console.log("Layer " + id + ": " + layer.url);
          this.serviceUrls.push(layer.url);
        }
      }, this);
    },

    onOpen: function(){
      console.log('onOpen Identify');
	  
	  // Listener für Mausklick initialisieren
      this.onClickListener = this.map.on("click", lang.hitch(this, this.executeIdentifyTask));
    },

    executeIdentifyTask: function(event) {
        console.log("MapClick " + event);
		
		// Identify bei Mausklick ausführen
        this.resultString = "";
        this.identifyParams.geometry = event.mapPoint;
        this.identifyParams.mapExtent = this.map.extent;
        array.forEach(this.serviceUrls, function(url) {
            var identifyTask = new IdentifyTask(url);
            identifyTask.execute(this.identifyParams, lang.hitch(this, this.parseIdentifyResult));
        }, this);
    },

    // Ergebnis Identify ausgeben
	parseIdentifyResult: function(idResults) {
      console.log(idResults);
      array.forEach(idResults, function(result) {
        console.log(result);
        this.resultString = this.resultString + result.layerName + ":<br>" + result.value + "<br><br>";
      }, this);
      this.mapIdNode.innerHTML = this.resultString;
    },

    onClose: function(){
      console.log('onClose');
	  
	  // Listener für Mausklick entfernen und Ergebnis löschen
      this.onClickListener.remove();
      this.mapIdNode.innerHTML = "";
    },

    onMinimize: function(){
      console.log('onMinimize');
    },

    onMaximize: function(){
      console.log('onMaximize');
    },

    onSignIn: function(credential){
      /* jshint unused:false*/
      console.log('onSignIn');
    },

    onSignOut: function(){
      console.log('onSignOut');
    }
  });
});