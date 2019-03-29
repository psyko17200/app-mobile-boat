//
//  ViewController.m
//  vue3
//
//  Created by Calvin Guillemet on 27/03/2019.
//  Copyright © 2019 Calvin Guillemet. All rights reserved.
//

#import "ViewController.h"
#import <MapKit/MKAnnotation.h>
@interface ViewController ()

@end

@implementation ViewController
@synthesize carte,end,clear;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    self.carte.showsUserLocation=YES;
    self.carte.delegate=self;
    CLLocationCoordinate2D location = CLLocationCoordinate2DMake(48.8534,2.3488); // location
    MKCoordinateSpan span = MKCoordinateSpanMake(0.0005, 0.0005); // zoom
    MKCoordinateRegion region = MKCoordinateRegionMake(location,span);  // charge la location et le zoom
    [carte setRegion: region animated:YES]; // affiche
    
    
    UITapGestureRecognizer *fingerTap = [[UITapGestureRecognizer alloc]
                                         initWithTarget:self action:@selector(handleMapFingerTap:)];
    fingerTap.numberOfTapsRequired = 1;
    fingerTap.numberOfTouchesRequired = 1;
    [self.carte addGestureRecognizer:fingerTap];
    
    lastLocation = kCLLocationCoordinate2DInvalid;
    lastAnnotation = nil;
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)handleMapFingerTap:(UIGestureRecognizer *)gestureRecognizer {
    
    if (gestureRecognizer.state != UIGestureRecognizerStateEnded) {
        return;
    }
    
    CGPoint touchPoint = [gestureRecognizer locationInView:self.carte];
    CLLocationCoordinate2D touchMapCoordinate =
    [self.carte convertPoint:touchPoint toCoordinateFromView:self.carte];
    
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = touchMapCoordinate;
    annotationPoint.subtitle=@"LABEL";
    annotationPoint.title=@"Title";

    [self.carte addAnnotation:annotationPoint];
    lastAnnotation = annotationPoint;
    
    NSLog(@"Latitude %f",annotationPoint.coordinate.latitude);
    NSLog(@"longitude %f",annotationPoint.coordinate.longitude);
    
    
    
    if (CLLocationCoordinate2DIsValid(lastLocation) ) {
        [self plotRouteOnMap:lastLocation atCurrent2DLocation:annotationPoint.coordinate];
    }
    lastLocation = annotationPoint.coordinate;
    
}

- (void)plotRouteOnMap: (CLLocationCoordinate2D )lastLocation atCurrent2DLocation: (CLLocationCoordinate2D )currentLocation {
    //Plot Location route on Map
    CLLocationCoordinate2D *plotLocation = malloc(sizeof(CLLocationCoordinate2D) * 2);
    plotLocation[0] = lastLocation;
    plotLocation[1] = currentLocation;
    MKPolyline *line = [MKPolyline polylineWithCoordinates:plotLocation count:2];
    [carte addOverlay:line];
    //[carte setCenterCoordinate:plotLocation[0]];
}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id<MKOverlay>)overlay
{
    if([overlay isKindOfClass:[MKPolyline class]])
    {
        MKPolylineView *lineView = [[MKPolylineView alloc] initWithPolyline:overlay];
        lineView.lineWidth = 2;
        lineView.strokeColor = [UIColor redColor];
        lineView.fillColor = [UIColor redColor];
        return lineView;
    }
    return nil;
}

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id <MKAnnotation>)annotation
{
    MKAnnotationView *annView = [[MKAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"Boatpin"];
    annView.canShowCallout = YES;
    annView.calloutOffset = CGPointMake(0, 0);
    annView.image=[UIImage imageNamed:@"pin"];
    UILabel *subTitlelbl = [[UILabel alloc]init];
    subTitlelbl.text = @"sri ganganagar this is my home twon.sri ganganagar this is my home twon.sri ganganagar this is my home twon.  ";
    
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:subTitlelbl attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationLessThanOrEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:150];
    
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:subTitlelbl attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:0];
    [subTitlelbl setNumberOfLines:0];
    [subTitlelbl addConstraint:width];
    [subTitlelbl addConstraint:height];
    UIView = // add view + y mettre le lablel + le button
    annView.detailCalloutAccessoryView = subTitlelbl;
    
    
    return annView;
}

@end
