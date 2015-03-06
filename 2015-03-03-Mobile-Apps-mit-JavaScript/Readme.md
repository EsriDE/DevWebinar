# **Mobile Apps mit JavaScript**
*Beispiele aus dem Webinar "[Mobile Apps mit JavaScript] (https://geonet.esri.com/events/1368)"*

## **Instructions**

Dieses Repository beinhaltet einige der gezeigten Beispiele aus dem Webinar. Die anderen Beispiele sind in den [Folien](http://www.eggits.net/files/gisiq/arcgis-fuer-entwickler-webinar-2-7.pdf) verlinkt. Weitere Details und Erläuterungen findet ihr in der [Aufzeichnung des Webinar]().

##### **Dojo.has**
Das Beispiel zeigt eine programmatische Browserweiche mit Dojo.has  

Ordner am besten in den lokalen Webserver packen und im Browser ansteuern. Das Dojo Toolkit wird implizit über die ArcGIS API for JavaScript angezogen. In der Datei js/MobileApp.js wird zunächst das Modul "dojo/has" referenziert und als Variable "has" in die Klasse geleitet. Der Konstruktor verwendet nun eine Fallunterscheidung von has("touch"), um herauszufinden, ob es sich um ein Gerät mit Touchscreen handelt, und erstellt dementsprechend ein reguläres oder ein mobiles Karten-Popup.
Nutzt doch die Gelegenheit und ladet im mobilen Modus den ClusterGitHub-Layer oder die HeatMap! Achtung: Das Beispiel Dojo.has erstellt die Karte aus einer ArcGIS-Online-Webmap mittels arcgisUtils.createMap((), die anderen beiden über das Map()-Objekt und das manuelle Hinzufügen eines Layers. Da müsst ihr euch was einfallen lassen. ;)

##### **MediaQueries**
Das Beispiel zeigt Responsives Layout per CSS Media Queries. 

Die Datei "CttMobile.css" enthält ergänzende CSS-Angaben für die nicht mobiloptimierte Applikation "Concert Telling Tweets", die Stand 4.3.2015 unter http://service02.eggits.net/ConcertTellingTweets/ erreichbar ist. In den Entwicklertools des Browsers eurer Wahl könnt ihr die Stylesheets live bearbeiten und die Ergebnisse temporär sehen. 
Die eigentliche Media Query ist die obenstehende Zeile "@media screen and (max-width: 640px)", die bewirkt, dass die folgenden Angaben auf Auflösungen unter 640px Breite angewendet werden. Solche Media Queries werden oft mehrstufig angewendet, im Inhalte responsiv an Auflösungen anzupassen.

##### **HeatMap**
Das Beispiel zeigt eine zoomstufenabhängige Darstellung von Karteninhalten

Mit Hilfe des ScaleDependentRenderer der JavaScript API werden die Daten, je nach Zoomstufe einzeln oder als HeatMap dargestellt. Für die Darstellung als HeatMap gibt es ebenfalls einen entsprechenden Renderer im JavaScript API.
    
## **Requirements**

Das ArcGIS API for JavaScript unterstützt folgende [Browser](https://developers.arcgis.com/javascript/jshelp/supported_browsers.html).

## **Resources**

Die Code-Beispiele verwenden das [ArcGIS API for JavaScript](https://developers.arcgis.com/javascript/). Da die gehostete Version referenziert wird, wird kein Download und keine Installation benötigt.

## **Issues**

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## **Licensing**

Copyright 2015 Esri Deutschland GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

A copy of the license is available in the repository's LICENSE-2.0.txt file.
