//
//  ViewController.h
//  vue 2
//
//  Created by calvin  on 31/03/2019.
//  Copyright © 2019 calvin . All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>

@interface ViewController : UIViewController<UIAccelerometerDelegate, MKMapViewDelegate> {
    IBOutlet UILabel *xlabel;
    IBOutlet UILabel *ylabel;
    IBOutlet UILabel *zlabel;
    IBOutlet UILabel *vitesse;
    IBOutlet UIButton *buttonSwitchToLandscape;
    IBOutlet UIButton *retour;
    IBOutlet MKMapView *carte;
    double angle;
    CLLocationCoordinate2D location;
    CLLocationCoordinate2D lastLoc;
    MKPointAnnotation *lastAnnotation;
    double lastlat;
    double lastlong;
    double speed;
    double longi;
    double lat;
    int i;
    MKPointAnnotation *annotationPoint;
}

@property(nonatomic ,strong) MKMapView * carte;

@end

