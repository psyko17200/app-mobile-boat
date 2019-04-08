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

@interface ViewController : UIViewController<UIAccelerometerDelegate, MKMapViewDelegate/*, UITextFieldDelegate*/> {
    IBOutlet UILabel *vitesse;
    IBOutlet UIButton *buttonSwitchToLandscape;
    IBOutlet UIButton *retour;
    IBOutlet MKMapView *carte;
    double angle;
    double angleBoat;
    double barre;
    double distance;
    double rafraichissement;
    double difX;
    double difY;
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
@property(nonatomic ,strong) IBOutlet UIButton * stop;
@property(nonatomic ,strong) IBOutlet UIButton * home;
@property(nonatomic ,strong) IBOutlet UILabel * speedCompt;
@property(nonatomic ,strong) IBOutlet UILabel * noeud;

@end

