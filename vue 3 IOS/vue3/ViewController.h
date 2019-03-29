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

@interface ViewController : UIViewController<NSStreamDelegate,MKMapViewDelegate>{
    CLLocationCoordinate2D lastLocation;
    MKPointAnnotation *lastAnnotation;
    IBOutlet MKMapView *carte;
    IBOutlet UIButton *end;
    IBOutlet UIButton *clear;
}
@property(nonatomic ,strong) MKMapView * carte;
@property (strong, nonatomic) IBOutlet UIButton *end;
@property (strong, nonatomic) IBOutlet UIButton *clear;
@property (strong, nonatomic) IBOutlet UILabel *vitesse;
@property (strong, nonatomic) IBOutlet UISlider *slider_vitesse;

@end
