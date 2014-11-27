/*
 Copyright 2013 Esri
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    NSURL *baseMapURL = [NSURL URLWithString:@"http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer"];
    
    AGSTiledMapServiceLayer *tiledLayer = [AGSTiledMapServiceLayer tiledMapServiceLayerWithURL:baseMapURL];
    [self.mapView addMapLayer:tiledLayer withName:@"Basemap Tiled Layer"];
    
    AGSDynamicMapServiceLayer * dynamicLayer = [AGSDynamicMapServiceLayer dynamicMapServiceLayerWithURL:baseMapURL];
    [dynamicLayer setVisible:false];
    [self.mapView addMapLayer:dynamicLayer withName:@"Dynamic Layer"];
    
    NSURL *featureLayerURL = [NSURL URLWithString:@"http://services2.arcgis.com/tISIjAqoejGPFbAF/arcgis/rest/services/Events/FeatureServer/0"];
    AGSFeatureLayer *featureLayer = [AGSFeatureLayer featureServiceLayerWithURL:featureLayerURL mode:AGSFeatureLayerModeOnDemand];
    [self.mapView addMapLayer:featureLayer withName:@"Events"];
    
    AGSGraphicsLayer *graphicsLayer = [AGSGraphicsLayer graphicsLayer];
    AGSPoint* markerPoint = [AGSPoint pointWithX:1280892.0 y:6094477.0 spatialReference:self.mapView.spatialReference];
    AGSSimpleMarkerSymbol *markerSymbol = [AGSSimpleMarkerSymbol simpleMarkerSymbol];
    markerSymbol.color = [UIColor blueColor];
    AGSGraphic* graphic = [AGSGraphic graphicWithGeometry:markerPoint symbol:markerSymbol attributes:nil];
    [graphicsLayer addGraphic:graphic];
    [self.mapView addMapLayer:graphicsLayer withName:@"Graphics Layer"];
    
    AGSGroupLayer *groupLayer = [[AGSGroupLayer alloc] init];
    [self.mapView addMapLayer:groupLayer withName:@"Group Layer"];
    
    AGSOpenStreetMapLayer *osmLayer = [AGSOpenStreetMapLayer openStreetMapLayer];
    [osmLayer setVisible:false];
    [self.mapView addMapLayer:osmLayer withName:@"OSM Layer"];
                                
    NSURL *wmsUrl = [NSURL URLWithString: @"http://www.wms.nrw.de/gd/guek500?"];
    AGSWMSLayer *wmsLayer = [[AGSWMSLayer alloc] initWithURL:wmsUrl];
    [self.mapView addMapLayer:wmsLayer withName:@"WMS Layer"];
    
    
    self.mapView.layerDelegate = self;
}

- (BOOL)prefersStatusBarHidden{
    return YES; //quick win for iOS 7
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - AGSMapViewLayerDelegate methods
- (void)mapViewDidLoad:(AGSMapView *) mapView {
    
}


@end
