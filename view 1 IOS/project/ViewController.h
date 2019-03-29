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
    IBOutlet MKMapView *carte;
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

@end
