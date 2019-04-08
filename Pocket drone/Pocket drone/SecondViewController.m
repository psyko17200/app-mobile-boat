//
//  SecondViewController.m
//  Pocket drone
//
//  Created by Christophe GUILLEMET on 06/04/2019.
//  Copyright © 2019 calvin . All rights reserved.
//


#import "SecondViewController.h"
#import <MapKit/MKAnnotation.h>

@interface SecondViewController ()
@end

@implementation SecondViewController
@synthesize carte;

- (void)viewDidLoad {
    [super viewDidLoad];
    //[_homeLat setDelegate:self];
    //[_homeLong setDelegate:self];
    i = 0;
    angle = 0;
    angleBoat = 0;
    barre = 1.5;
    distance = 0;
    difX = 0;
    difY = 0;
    rafraichissement = 0.0025;
    lastlat = 0;
    lastlong = 0;
    lastLoc = kCLLocationCoordinate2DInvalid;
    //creation de la map
    lastAnnotation = nil;
    speed = 0;
    self.carte.delegate=self;
    longi = 0;
    lat = 0;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)plotRouteOnMap: (CLLocationCoordinate2D )lastLocation atCurrent2DLocation: (CLLocationCoordinate2D )currentLocation {
    //Plot Location route on Map
    CLLocationCoordinate2D *plotLocation = malloc(sizeof(CLLocationCoordinate2D) * 2);
    plotLocation[0] = lastLocation;
    plotLocation[1] = currentLocation;
    MKPolyline *line = [MKPolyline polylineWithCoordinates:plotLocation count:2];
    [carte addOverlay:line];
    [carte setCenterCoordinate:plotLocation[1] animated:YES];
}

- (void)accelerometer:(UIAccelerometer *)accelerometer didAccelerate:
(UIAcceleration *)acceleration {
    [carte removeAnnotation:lastAnnotation];
    
    if(acceleration.z < 0 && acceleration.x > 0) { // Calcul vitesse
        speed = acceleration.z*-60;
        [vitesse setText:[NSString stringWithFormat:@"%.2f",speed]]; // Affichage vitesse
    } else if(acceleration.x < 0) { // Vitesse max
        speed = 60;
        [vitesse setText:[NSString stringWithFormat:@"%.2f",speed]]; // Affichage vitesse
    } else if(acceleration.x >= 1){
        [vitesse setText:[NSString stringWithFormat:@"%.2f",0.00]]; // Affichage vitesse
    }
    
    if(acceleration.y < -0.25) {
        [self tournerDroite];
    } else if(acceleration.y > 0.25) {
        [self tournerGauche];
    }
    [self rafraichir];
}

- (BOOL)shouldAutorotate
{
    switch (i) {
        case 0:
            return NO;
            break;
        case 1:
            return YES;
            break;
        default:
            return NO;
    }
}

- (void)tournerGauche {
    barre = barre - 0.1;
    angle = angle - 0.1;
}

- (void)tournerDroite {
    barre = barre + 0.1;
    angle = angle + 0.1;
}

- (void)rafraichir {
    distance = speed / 0.05;
    difX = sin(barre) * distance * rafraichissement;
    difY = cos(barre) * distance * rafraichissement;
    
    lat = lat + (difX / 110574.61);
    longi = longi + (difY / 110574.61);
    
    angleBoat = atan2(longi - lastlong, lat - lastlat);
    
    location = CLLocationCoordinate2DMake(lat, longi);
    
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = location;
    [carte addAnnotation:annotationPoint];
    lastAnnotation = annotationPoint;
    
    if (CLLocationCoordinate2DIsValid(lastLoc) ) {
        [self plotRouteOnMap:lastLoc atCurrent2DLocation:location];
    }
    
    lastLoc = location;
    lastlat = lat;
    lastlong = longi;
}

// Button
- (IBAction)stop:(id)sender
{
    [carte removeAnnotation:annotationPoint];
    NSString* labelString = [_stop titleForState:UIControlStateNormal];
    if([labelString isEqualToString:@"Stop"]){
        [[UIAccelerometer sharedAccelerometer]setDelegate:nil];
        [_stop setTitle:@"Start" forState:UIControlStateNormal];
    }else if([labelString isEqualToString:@"Start"]){
        [[UIAccelerometer sharedAccelerometer]setDelegate:self];
        [_stop setTitle:@"Stop" forState:UIControlStateNormal];
    }
}

- (IBAction)buttonSwitchToLandscape:(id)sender
{
    self.tabBarController.tabBar.hidden = YES;
    carte.hidden = NO;
    vitesse.hidden = NO;
    retour.hidden = NO;
    _stop.hidden = NO;
    _home.hidden = NO;
    _speedCompt.hidden = NO;
    _noeud.hidden = NO;
    buttonSwitchToLandscape.hidden = YES;
    i = 1;
    lat = 46.146741;
    longi = -1.168283;
    location = CLLocationCoordinate2DMake(lat, longi);
    MKCoordinateSpan span = MKCoordinateSpanMake(0.005, 0.005); // zoom
    MKCoordinateRegion region = MKCoordinateRegionMake(location,span);  // charge la location et le zoom
    [carte setRegion: region animated:YES]; // affiche
    carte.mapType=MKMapTypeSatellite;
    annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = location;
    [carte addAnnotation:annotationPoint];
    
    [self shouldAutorotate];
    [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationLandscapeLeft) forKey:@"orientation"];
    [UINavigationController attemptRotationToDeviceOrientation];
    i = 0;
    [self shouldAutorotate];
}

// Button back
- (IBAction)backbutton:(id)sender
{
    self.tabBarController.tabBar.hidden = NO;
    carte.hidden = YES;
    vitesse.hidden = YES;
    retour.hidden = YES;
    _stop.hidden = YES;
    _home.hidden = YES;
    _speedCompt.hidden = YES;
    _noeud.hidden = YES;
    buttonSwitchToLandscape.hidden = NO;
    lat = 0;
    longi = 0;
    lastLoc = kCLLocationCoordinate2DInvalid;
    i = 1;
    [self shouldAutorotate];
    [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationPortrait) forKey:@"orientation"];
    [UINavigationController attemptRotationToDeviceOrientation];
    i = 0;
    [self shouldAutorotate];
    [[UIAccelerometer sharedAccelerometer]setDelegate:nil];
    [self.carte removeOverlays:self.carte.overlays];
    [carte removeAnnotation:lastAnnotation];
    [carte removeAnnotation:annotationPoint];
    [_stop setTitle:@"Start" forState:UIControlStateNormal];
}

- (IBAction)Home:(id)sender
{
    [self.carte removeOverlays:self.carte.overlays];
    [carte removeAnnotation:lastAnnotation];
    [carte removeAnnotation:annotationPoint];
    [[UIAccelerometer sharedAccelerometer]setDelegate:nil];
    [_stop setTitle:@"Start" forState:UIControlStateNormal];
    lat = 46.146741;
    longi = -1.168283;
    location = CLLocationCoordinate2DMake(lat, longi);
    annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = location;
    [carte addAnnotation:annotationPoint];
    [carte setCenterCoordinate:location animated:YES];
    lastLoc = kCLLocationCoordinate2DInvalid;
}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id<MKOverlay>)overlay
{
    if([overlay isKindOfClass:[MKPolyline class]])
    {
        MKPolylineView *lineView = [[MKPolylineView alloc] initWithPolyline:overlay];
        lineView.lineWidth = 4;
        lineView.strokeColor = [UIColor redColor];
        lineView.fillColor = [UIColor redColor];
        return lineView;
    }
    return nil;
}

- (MKAnnotationView *)mapView:(MKMapView *)map viewForAnnotation:(id <MKAnnotation>)annotation
{
    MKAnnotationView *annView = [[MKAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"Boatpin"];
    
    annView.canShowCallout = YES;
    annView.calloutOffset = CGPointMake(-5, 5);
    annView.image=[UIImage imageNamed:@"boat"];
    annView.transform = CGAffineTransformRotate(carte.transform, angleBoat);
    return annView;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

@end
