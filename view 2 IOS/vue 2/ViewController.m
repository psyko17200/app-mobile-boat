//
//  ViewController.m
//  vue 2
//
//  Created by calvin  on 31/03/2019.
//  Copyright © 2019 calvin . All rights reserved.
//

#import "ViewController.h"
#import <MapKit/MKAnnotation.h>

@interface ViewController ()
@end

@implementation ViewController
@synthesize carte;

- (void)viewDidLoad {
    [super viewDidLoad];
    i = 0;
    angle = 0;
    lastlat = 0;
    lastlong = 0;
    lastLoc = kCLLocationCoordinate2DInvalid;
    [[UIAccelerometer sharedAccelerometer]setDelegate:self];
    //creation de la map
    lastAnnotation = nil;
    speed = 0;
    self.carte.delegate=self;
    longi = 48.856614;
    lat = 2.3522219;
    location = CLLocationCoordinate2DMake(longi, lat);
    MKCoordinateSpan span = MKCoordinateSpanMake(0.005, 0.005); // zoom
    MKCoordinateRegion region = MKCoordinateRegionMake(location,span);  // charge la location et le zoom
    [carte setRegion: region animated:YES]; // affiche
    annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = location;
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
    [xlabel setText:[NSString stringWithFormat:@"%f",acceleration.x]];
    [ylabel setText:[NSString stringWithFormat:@"%f",acceleration.y]];
    [zlabel setText:[NSString stringWithFormat:@"%f",acceleration.z]];
    
    if(acceleration.z >= 0){
        [vitesse setText:[NSString stringWithFormat:@"%d",0]];
        speed = 0;
    }else{
        [vitesse setText:[NSString stringWithFormat:@"%f",acceleration.z*-60]]; // Vitesse max 60K/n
        speed = acceleration.z*-60;
    }
    
    /*if ([carte respondsToSelector:@selector(camera)]) {
        MKMapCamera *newCamera = [[carte camera] copy];
        if(acceleration.x <= 0){
            [newCamera setHeading:angle+(acceleration.y*180)-360]; // rotation du bateau
        }else{
            [newCamera setHeading:angle+acceleration.y*180]; // rotation du bateau
        }
        [carte setCamera:newCamera animated:YES];
    }*/
    
    if(acceleration.z < 0 && acceleration.x > 0){
        longi = longi - (acceleration.z/50000);
    }else if(acceleration.z < 0 && acceleration.x < 0){
        longi = longi + (acceleration.z/50000);
    }else if(acceleration.z >= 0 && acceleration.x < 0){

    }
    
    lat = lat + (acceleration.y/40000);
    
    location = CLLocationCoordinate2DMake(longi, lat);
    
    angle = atan2(lat - lastlat, longi - lastlong);
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

// Button
- (IBAction)buttonSwitchToLandscape:(id)sender
{
    i = 1;
    [self shouldAutorotate];
    [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationLandscapeLeft) forKey:@"orientation"];
    [UINavigationController attemptRotationToDeviceOrientation];
    i = 0;
    [self shouldAutorotate];
}

// Button back
- (IBAction)backbutton:(id)sender
{
    i = 1;
    [self shouldAutorotate];
    [[UIDevice currentDevice] setValue:@(UIInterfaceOrientationPortrait) forKey:@"orientation"];
    [UINavigationController attemptRotationToDeviceOrientation];
    i = 0;
    [self shouldAutorotate];
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
    annView.transform = CGAffineTransformRotate(carte.transform, angle);
    return annView;
}

@end
