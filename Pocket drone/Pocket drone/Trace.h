//
//  ViewController.h
//  vue3
//
//  Created by Calvin Guillemet on 27/03/2019.
//  Copyright © 2019 Calvin Guillemet. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <MapKit/MKAnnotation.h>


@interface Trace : UIViewController<NSStreamDelegate,MKMapViewDelegate>{
    CLLocationCoordinate2D lastLocation;
    MKPointAnnotation *lastAnnotation;
    IBOutlet MKMapView *carte;
    IBOutlet UIButton *end;
    IBOutlet UIButton *clear;
    NSMutableArray *arrayAnnotation;
    NSMutableArray *arrayTrame;
    NSString *defaultVitesse;
}
@property(nonatomic ,strong) MKMapView * carte;
@property (strong, nonatomic) IBOutlet UIButton *end;
@property (strong, nonatomic) IBOutlet UIButton *clear;
@property (strong, nonatomic) IBOutlet UILabel *vitesse;
@property (strong, nonatomic) IBOutlet UITextField *vitesse_point;
@property (strong, nonatomic) IBOutlet UIAlertController *alert;
@property (strong, nonatomic) IBOutlet UIAlertAction *defaultAction;

-(IBAction)clear:(id)sender;
-(IBAction)send:(id)sender;
@end
