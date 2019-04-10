//
//  SecondViewController.h
//  Pocket drone
//
//  Created by Christophe GUILLEMET on 06/04/2019.
//  Copyright © 2019 calvin . All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>

@interface Pilotage : UIViewController<UIAccelerometerDelegate, MKMapViewDelegate> {
    IBOutlet UILabel *vitesse;
    IBOutlet UIButton *buttonSwitchToLandscape;
    IBOutlet UIButton *retour;
    IBOutlet MKMapView *carte;
    
    // Attributs de l'accéléromêtre + map
    double angle;
    double angleBoat;
    double barre;
    double distance;
    double rafraichissement;
    double difX;
    double difY;
    double lastlat;
    double lastlong;
    double speed;
    double longi;
    double lat;
    int i;
    CLLocationCoordinate2D location;
    CLLocationCoordinate2D lastLoc;
    MKPointAnnotation *lastAnnotation;
    MKPointAnnotation *annotationPoint;
}

@property(nonatomic ,strong) MKMapView * carte;
@property(nonatomic ,strong) IBOutlet UIButton * stop;
@property(nonatomic ,strong) IBOutlet UIButton * home;
@property(nonatomic ,strong) IBOutlet UILabel * speedCompt;
@property(nonatomic ,strong) IBOutlet UILabel * noeud;

@end
