//
//  ViewController.h
//  project
//
//  Created by Guillaume Paillard on 25/03/2019.
//  Copyright © 2019 Guillaume Paillard. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>

@interface ViewController : UIViewController<NSStreamDelegate,MKMapViewDelegate>{
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    
    NSInputStream   *inputStream;
    NSOutputStream  *outputStream;
    
    NSMutableArray  *messages;
    CLLocationCoordinate2D lastLocation;
    MKPointAnnotation *lastAnnotation;
    IBOutlet UIButton *deco;
    IBOutlet UIButton *connect;
    IBOutlet UIButton *send;
    IBOutlet MKMapView *carte;
    
    NSString *lattitude;
    NSString *longitude;
    NSString *last_boat;
    CLLocationDegrees latitudeDelta;
    CLLocationDegrees longitudeDelta;
    double last_lattitude;
    double last_longitude;
    double new_lattitude;
    double new_longitude;
}
@property(nonatomic ,strong) MKMapView * carte;
@property (weak, nonatomic) IBOutlet UITextField *ipAddressText;
@property (weak, nonatomic) IBOutlet UITextField *portText;
@property (weak, nonatomic) IBOutlet UITextField *dataToSendText;
@property (weak, nonatomic) IBOutlet UITextView *dataRecievedTextView;
@property (weak, nonatomic) IBOutlet UITextView *datalattitude;
@property (weak, nonatomic) IBOutlet UITextView *datalongitude;
@property (weak, nonatomic) IBOutlet UITextView *datavitesse;
@property (weak, nonatomic) IBOutlet UILabel *connectedLabel;
@property (strong, nonatomic) IBOutlet UIButton *deco;
@property (strong, nonatomic) IBOutlet UIButton *connect;
@property (strong, nonatomic) IBOutlet UIButton *send;
@property (strong, nonatomic) IBOutlet UIImageView *logo;
@property (weak, nonatomic) IBOutlet UITextView *lat;
@property (weak, nonatomic) IBOutlet UITextView *lon;
@property (weak, nonatomic) IBOutlet UITextView *vit;
@property (strong, nonatomic) IBOutlet UIAlertController *alert;
@property (strong, nonatomic) IBOutlet UIAlertAction *defaultAction;

@end
