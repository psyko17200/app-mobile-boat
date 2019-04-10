//  ViewController.h
//  Pocket drone
//
//  Created by calvin  on 04/04/2019.
//  Copyright © 2019 calvin . All rights reserved.
//


#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>

@interface Visualisation : UIViewController<NSStreamDelegate,MKMapViewDelegate,UITextFieldDelegate>{
    // Connection NMEA Simulateur
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    NSInputStream   *inputStream;
    NSOutputStream  *outputStream;
    NSMutableArray  *messages;
    
    // Map et Coordonnées
    CLLocationCoordinate2D lastLocation;
    MKPointAnnotation *lastAnnotation;
    NSString *lattitude;
    NSString *longitude;
    CLLocationDegrees latitudeDelta;
    CLLocationDegrees longitudeDelta;
    double last_lattitude;
    double last_longitude;
    double new_lattitude;
    double new_longitude;
    double angle;
    double lastAngle;
    
    // Affichage 
    IBOutlet UIButton *deco;
    IBOutlet UIButton *connect;
    IBOutlet MKMapView *carte;
}
@property(nonatomic ,strong) MKMapView * carte;
@property (strong, nonatomic) IBOutlet UITextField *ipAddressText;
@property (strong, nonatomic) IBOutlet UITextField *portText;
@property (weak, nonatomic) IBOutlet UITextView *datalattitude;
@property (weak, nonatomic) IBOutlet UITextView *datalongitude;
@property (weak, nonatomic) IBOutlet UITextView *datavitesse;
@property (strong, nonatomic) IBOutlet UIButton *deco;
@property (strong, nonatomic) IBOutlet UIButton *connect;
@property (strong, nonatomic) IBOutlet UIImageView *logo;
@property (strong, nonatomic) IBOutlet UIImageView *back;
@property (weak, nonatomic) IBOutlet UITextView *lat;
@property (weak, nonatomic) IBOutlet UITextView *lon;
@property (weak, nonatomic) IBOutlet UITextView *vit;
@property (strong, nonatomic) IBOutlet UIAlertController *alert;
@property (strong, nonatomic) IBOutlet UIAlertAction *defaultAction;

@end


