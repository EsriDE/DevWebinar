# **GeoEvents in Echtzeit nutzen**
*Beispiele aus dem Webinar "[GeoEvents in Echtzeit nutzen] (https://geonet.esri.com/events/1367)"*

## **Instructions**

Durch die zunehmende Verbreitung von Sensoren und sozialen Netzwerken stehen mehr und mehr Echtzeitdatenströme zur Verfügung: Twitter Feeds, Pegelstände, Wetterdaten, Staumeldungen, aktuelle Positionen von öffentlichen Verkehrsmitteln usw. Diese digitalen Schätze gilt es zu heben. Esri ermöglicht dies mit der ArcGIS GeoEvent Extension for Server.

Dieses Repository beinhaltet zwei Aspekte:
* Das Einlesen und Verarbeiten von Echtzeitdaten in der GeoEvent Extension
* Die Anzeige der Echtzeitdaten in einem JavaScript Klient

##### **Konfiguration**
Hier handelt es sich um das gezeigte Erdbeben-Beispiel. Die XML-Konfigurationsdatei kann mit Hilfe des ArcGIS GeoEvent Manger über Site -> GeoEvent -> Configuration Store  -> Import Configuration eingespielt werden. Damit wird der Input, der GeoEvent Service und der Output erzeugt. Der Output muss allerdings noch angepasst werden, da hier ein FeatureServie benötigt wird. Dieser kann mit Hilfe der Datei EarthquakeBulletin.mpk auf einem ArcGIS for Server oder ArcGIS Online erzeugt werden. Die Verbindung zur jeweiligen Instanz kann mit Hilfe des ArcGIS GeoEvent Managers über Site -> GeoEvent -> Data Stores -> Register ArcGIS Server in der GeoEvent Extension registriert werden.

##### **Java Script Klienten**
In diesem Abschnitt befinden sich die zwei gezeigten Beispiele zur Nutzung von Echtzeitdaten in einem JavaScript Klienten:
* **Die Nutzung eines WebSockets**
Das Beispiel erwartet die Echtzeitdaten über einen, in der GeoEvent Extension erzeugten, WebSocket im Format Generic-JSON. In der Datei Application.js müssen hierfür die Parameter webSocketUrl, geoEventDefinitionUrl und geoEventDefinition angepasst werden. Außerdem wird vorausgesetzt dass in der GeoEventDefinition der eindeutige Schlüssel durch den Tag TRACK_ID und das Geometriefeld durch den Tag GEOMETRY gekennzeichnet sind.
* **Die Nutzung des neusen StreamService**
Mit der Version 10.3 wurde mit dem StreamService ein neuer Dienstetyp eingeführt. Dieser basiert ebenfalls auf dem Prinzip des WebSockets. Allerdings wird die Kommunikationsschicht durch Esri gekapselt, so dass die Nutzung für den Anwender deutlich einfacher ist. Im Code muss nur noch die URL zum StreamService angepasst werden.
    
## **Requirements**

Die ArcGIS GeoEvent Extension setzt einen ArcGIS for Server voraus. [Hier](http://server.arcgis.com/en/geoevent-extension/latest/install/windows/system-requirements.htm) findet man die Systemanforderungen für beide Komponenten.

Das ArcGIS API for JavaScript unterstützt folgende [Browser](https://developers.arcgis.com/javascript/jshelp/supported_browsers.html).

## **Resources**
Für die Beispiele wird ein [ArcGIS for Server](http://server.arcgis.com/en/server/) und die [ArcGIS GeoEvent Extension for Server](http://server.arcgis.com/en/geoevent-extension/) benötigt.

Die Code-Beispiele verwenden das [ArcGIS API for JavaScript](https://developers.arcgis.com/javascript/). Da die gehostete Version referenziert wird, wird kein Download und keine Installation benötigt.

## **Issues**

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## **Licensing**

Copyright 2014 Esri Deutschland GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

A copy of the license is available in the repository's LICENSE-2.0.txt file.
