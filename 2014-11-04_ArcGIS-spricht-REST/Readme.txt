Material zum Webinar "ArcGIS spricht REST"
Niklas Köhn, Esri Deutschland GmbH, 4.11.2014

=== Postman_ArcGIS-spricht-REST.json ===
Installiert das kostenlose Add-In "Postman" im Chrome Browser und importiert die REST-Befehle aus dem Webinar. Nicht vergessen, bei generateToken Benutzername und Passwort einer gültigen Subskription einzugeben und in den weiteren Befehlen das Token und wom nötig die Item ID auszutauschen.
Aus Zeitgründen wurde im Webinar nicht auf teilweise nötige Metadaten eingegangen. In der Postman-Collection findet ihre den "Add Item"-Befehl in einer Ausführung mit WMS-Metadaten, die benötigt werden, um den Dienst auf einer Karte tatsächlich anzuzeigen. Die nötigen Metadaten eines WMS-Dienstes bekommt ihr über die GetCapabilities-Funktion, und den Zusammenbau des JSON-Codes für ArcGIS Online besorgt ihr dann im Code.

=== ServiceProperties_RestExample.py ===
Ein Python-Script, das ein Token generiert, die Server-Inhalte durchgeht und alle Services innerhalb eines Verzeichnisses unter neuem Namen neu veröffentlicht. In Zeilen 137-138 (def serviceInfo) findet ihr voreingestellte Werte, die ihr auf euren Server anpassen könnt. Ganz unten befinden sich einige auskommentierte Zeilen, die den Befehl arcpy.GetParameterAsText() verwenden - diese sind für Script-Tools gedacht, die die Parameter über die Oberfläche von ArcMap einlesen.

=== OAuth2 (.NET) ===