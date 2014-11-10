#-------------------------------------------------------------------------------
# Name:        Rest example
# Purpose:     Get all service properties
# Author:      lcl-dama
# Created:     31.10.2014
#-------------------------------------------------------------------------------
import os
import sys
import urllib
import json
import httplib
import arcpy

# function to print messages in python and arcGIS. Returns given message
def printInfo(message, warning=False):
    print message
    if not warning: arcpy.AddMessage(message)
    else: arcpy.AddWarning(message)
    return message

# get access token
def getToken(username, password, server, port, exp=60):
    query_dict = {'username':   username,
                  'password':   password,
                  'expiration': str(exp),
                  'client':     'requestip',
                  'f': 'json'}
    tokenURL = "http://{}:{}/arcgis/admin/generateToken".format(server, port)
    tokenResponse = urllib.urlopen(tokenURL, urllib.urlencode(query_dict))
    tokenOuput = json.loads(tokenResponse.read())
    if "token" not in tokenOuput: printInfo(tokenOuput['messages'])
    else: return tokenOuput['token']    # Return the token, expiry and URL

# get manifest Json with additional service details
def getJson(url, token, serverName, port):
    try:
        # Create parameters and connect to server http
        params = urllib.urlencode({'token': token, 'f': 'json'})
        headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
        httpConn = httplib.HTTPConnection(serverName, port)
        urlEncode = urllib.quote(url.encode('utf-8'))
        httpConn.request("POST", urlEncode, params, headers)

        # Read response
        response = httpConn.getresponse()
        if (response.status != 200):
            httpConn.close()
            printInfo("Could not read folder information.", True)
            return False
        else:
            data = response.read()
            # Check that data returned is not an error object
            if not assertJsonSuccess(data):
                printInfo("Error when reading server information. " + str(data), True)
                return False
            # Deserialize response into Python object
            dataObj = json.loads(data)
            httpConn.close()
            if not dataObj:
                return False
            return dataObj

    except arcpy.ExecuteError:
        printInfo(arcpy.GetMessages(2), True)
    except IOError as e:
        printInfo("I/O error({0}): {1}".format(e.errno, e.strerror), True)
        return False

# ensure Json was sucessfully loaded
def assertJsonSuccess(data):
    # A function that checks that the input JSON object is not an error object.
    obj = json.loads(data)
    if 'status' in obj and obj['status'] == "error":
        printInfo("Error: JSON object returns an error. " + str(obj))
        return False
    else: return True

# create connection file (.ags) and return path
def CreateContectionFile(wrkspc, userName, password, serverName, port):
    con = "http://{}:{}/arcgis/admin".format(serverName, port)
    connection_file_path = str(wrkspc) + "\\tmpConnection.ags"
    agsname=os.path.basename(connection_file_path)
    try:
        # remove and replace .ags file if already exists
        if os.path.exists(connection_file_path): os.remove(connection_file_path)
        arcpy.mapping.CreateGISServerConnectionFile("ADMINISTER_GIS_SERVICES", wrkspc,
                    agsname, con, "ARCGIS_SERVER", True, None, userName, password, True)
        return connection_file_path

    except arcpy.ExecuteError:
        printInfo(arcpy.GetMessages(2), True)
    except IOError as e:
        printInfo("I/O error({0}): {1}".format(e.errno, e.strerror), True)
        return False

# open mxd, update service name, and publish again
def updateMXD(mxdPath, newServiceName, folder, connection_file_path, wrkspc):
    mapDoc = arcpy.mapping.MapDocument(mxdPath)
    sddraft = wrkspc + "\\" + newServiceName + '.sddraft'
    ##sddraft = os.path.dirname(mxdPath) + "\\" + newServiceName + '.sddraft'
    sd = sddraft.replace('.sddraft', '.sd')
    try:
        # create service definition draft
        analysis = arcpy.mapping.CreateMapSDDraft(mapDoc, sddraft, newServiceName,
                 'ARCGIS_SERVER', connection_file_path, True, folder)
        printInfo("sddraft created successfully")
        # stage and upload the service if the sddraft analysis did not contain errors
        if analysis['errors'] == {}:
            if os.path.exists(sd): os.remove(sd)
            arcpy.StageService_server(sddraft, sd)
            printInfo("sd created successfully")
            arcpy.UploadServiceDefinition_server(sd, connection_file_path)
            printInfo("service uploaded successfully")
            return True

        # if the sddraft or SD analysis contained errors, display them
        printInfo(analysis['errors'], True)
        return False

    except arcpy.ExecuteError:
        printInfo(arcpy.GetMessages(2), True)
    except IOError as e:
        printInfo("I/O error({0}): {1}".format(e.errno, e.strerror), True)
        return False

# Parameters:
    # user: administrative username
    # passw: adminstrative password
    # server: server name  ie localhost
    # port: port of access ie 6080
    # wrkspc: workspace to save connection file, sddraft, and sd files. Must exist and
            # have write permissions. If fails, automatically tries "C:\projects".
    # serviceFolder: folder with services to be updated.  ie "Fold1"
    # serviceSuffix: suffix to add to all service names
    # system: False to leave system folder out of the loop
    # utilities: False to leave utilities folder out of the loop
def serviceInfo(user="siteadmin", passw="siteadmin", server="localhost", port=6080, wrkspc=r"C:\data",
         serviceFolder="Pub", serviceSuffix="_RestWeb", system=False, utilities=False):
    try:
        printInfo("\nInitializing service access...")

        # create token and get JSON data object
        token = getToken(user, passw, server, port)
        serverUrl = "/arcgis/admin/services/"
        dataObj = getJson(serverUrl, token, server, port)

        #Store the Folders in a list to loop on, Remove the System and Utilities folders
        folders = dataObj["folders"]
        if not system: folders.remove("System")
        if not utilities: folders.remove("Utilities")

        #Loop through folders, continue only for given folder name(s)
        for folder in folders:
            if folder in serviceFolder:
                # Build the URL for the current folder
                if folder != "": folder += "/"
                folderURL = serverUrl + folder
                # Connect to URL and post parameters and Read response
                dataObj = getJson(folderURL, token, server, port)
                if dataObj:
                    if folder == "": folderName = "root"
                    if folder != "": folderName = folder[:-1]
                    printInfo("Folder information successfully processed for: {} - Now processing services...".format(folderName))

                    # Loop through each service in the folder
                    for item in dataObj['services']:
                        printInfo(item)
                        serviceName = item["serviceName"]
                        sUrl = "%s%s.%s" %(folderURL, serviceName, item["type"])
                        if not sUrl.endswith("Server"):
                            printInfo("Service not of type '__Server'", True)
                            break
                        sUrl += "/iteminfo/manifest/manifest.json"

                        # get Manifest Json with local and server MXD routes
                        mxdJson = getJson(sUrl, token, server, port)
                        if "resources" not in str(mxdJson):
                            printInfo("No mxd information. Json msg: " + str(mxdJson["messages"][0]), True)
                            break
                        localMXD = str(mxdJson["resources"][0]["onPremisePath"])
                        msd = str(mxdJson["resources"][0]["serverPath"])
                        serverMXD = msd.replace(".msd", ".mxd")

                        if serverMXD.endswith(".mxd"):
                            # create connection file
                            try:
                                connection_file_path = CreateContectionFile(wrkspc, user, passw, server, port)
                                printInfo("  AG4S Connection File created: " + str(connection_file_path))
                                # access mxd, update service name, republish
                                if serviceName.endswith(serviceSuffix): serviceName += "1"
                                else: newServiceName = serviceName + serviceSuffix
                                success = updateMXD(serverMXD, newServiceName, folderName, connection_file_path, wrkspc)
                                if not success:
                                    printInfo("Upload Failed", True)
                                    break
                            except:
                                wrkspc = r"C:\projects"
                                connection_file_path = CreateContectionFile(wrkspc, user, passw, server, port)
                                printInfo("  AG4S Connection File created: " + str(connection_file_path))
                                # access mxd, update service name, republish
                                newServiceName = serviceName + serviceSuffix
                                success = updateMXD(serverMXD, newServiceName, folderName, connection_file_path, wrkspc)
                                if not success:
                                    printInfo("Upload Failed", True)
                                    break

    except arcpy.ExecuteError:
        printInfo(arcpy.GetMessages(2), True)
    except IOError as e:
        printInfo("I/O error({0}): {1}".format(e.errno, e.strerror), True)
        return False

if __name__ == '__main__':
    # Get parameters from ArcGIS
##    user = arcpy.GetParameterAsText(0)
##    passw = arcpy.GetParameterAsText(1)
##    server = arcpy.GetParameterAsText(2)
##    port = arcpy.GetParameterAsText(3)
##    workspace = arcpy.GetParameterAsText(4)
##    serviceFolder = arcpy.GetParameterAsText(5)
##    serviceSuffix = arcpy.GetParameterAsText(6)
##    system = arcpy.GetParameterAsText(7)
##    utilities = arcpy.GetParameterAsText(8)
##    serviceInfo(user, passw, server, port, workspace, serviceFolder, serviceSuffix, system, utilities)
    serviceInfo()
