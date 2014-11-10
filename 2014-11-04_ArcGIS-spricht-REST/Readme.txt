Material zum Webinar "ArcGIS spricht REST"
Niklas K�hn, Esri Deutschland GmbH, 4.11.2014

=== Postman_ArcGIS-spricht-REST.json ===
Installiert das kostenlose Add-In "Postman" im Chrome Browser und importiert die REST-Befehle aus dem Webinar. Nicht vergessen, bei generateToken Benutzername und Passwort einer g�ltigen Subskription einzugeben und in den weiteren Befehlen das Token und wom n�tig die Item ID auszutauschen.
Aus Zeitgr�nden wurde im Webinar nicht auf teilweise n�tige Metadaten eingegangen. In der Postman-Collection findet ihre den "Add Item"-Befehl in einer Ausf�hrung mit WMS-Metadaten, die ben�tigt werden, um den Dienst auf einer Karte tats�chlich anzuzeigen. Die n�tigen Metadaten eines WMS-Dienstes bekommt ihr �ber die GetCapabilities-Funktion, und den Zusammenbau des JSON-Codes f�r ArcGIS Online besorgt ihr dann im Code.

=== ServiceProperties_RestExample.py ===
Ein Python-Script, das ein Token generiert, die Server-Inhalte durchgeht und alle Services innerhalb eines Verzeichnisses unter neuem Namen neu ver�ffentlicht. In Zeilen 137-138 (def serviceInfo) findet ihr voreingestellte Werte, die ihr auf euren Server anpassen k�nnt. Ganz unten befinden sich einige auskommentierte Zeilen, die den Befehl arcpy.GetParameterAsText() verwenden - diese sind f�r Script-Tools gedacht, die die Parameter �ber die Oberfl�che von ArcMap einlesen.

=== OAuth2 (.NET) ===