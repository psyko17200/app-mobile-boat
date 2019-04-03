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
@synthesize carte,end,clear, vitesse, vitesse_point,alert,defaultAction;

- (void)viewDidLoad {
    [super viewDidLoad];
    defaultVitesse = @"10";
    
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
    
    arrayAnnotation= [[NSMutableArray alloc] init];
    lastLocation = kCLLocationCoordinate2DInvalid;
    lastAnnotation = nil;
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
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
    annotationPoint.title=@"Vitesse du bateau";
    [self.carte addAnnotation:annotationPoint];
    lastAnnotation = annotationPoint;
    
    NSLog(@"Latitude %f",annotationPoint.coordinate.latitude);
    NSLog(@"longitude %f",annotationPoint.coordinate.longitude);
    
    if (CLLocationCoordinate2DIsValid(lastLocation) ) {
        [self plotRouteOnMap:lastLocation atCurrent2DLocation:annotationPoint.coordinate];
    }
    lastLocation = annotationPoint.coordinate;
    
    NSNumber *lat = [NSNumber numberWithDouble:annotationPoint.coordinate.latitude];
    NSNumber *longi = [NSNumber numberWithDouble:annotationPoint.coordinate.longitude];
    
    
    // Ajout de pop up + ajout dans l'array
    alert = [UIAlertController alertControllerWithTitle:@"WayPoint"                                                                         message:@"Veuillez entrer une vitesse"                                                                    preferredStyle:UIAlertControllerStyleAlert];
    [alert addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        textField.placeholder = @"5.2"; // if needs
    }];
    defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault                                                                  handler:^(UIAlertAction * action) {
        
        NSDateFormatter *hourFormatter=[[NSDateFormatter alloc] init];
        [hourFormatter setDateFormat:@"hhmmss.000"];
        NSDateFormatter *dateFormatter=[[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"ddMMyy"];
        
        NSString *vit = self->alert.textFields[0].text;
        if([vit isEqualToString:@""]== true){
            vit = self->defaultVitesse;
        }
        NSString *trame = @"$GPRMC,";
        [self->arrayAnnotation addObject:lat];
        [self->arrayAnnotation addObject:longi];
        [self->arrayAnnotation addObject:vit];
        NSLog(@"%@",self->arrayAnnotation);
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[hourFormatter stringFromDate:[NSDate date]]]];
        trame = [trame stringByAppendingString:@"A,"]; //
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[self convertir:lat]]];
        trame = [trame stringByAppendingString:[self convertir:lat]<0 ? @"S," : @"N," ]; // + Nord, - Sud
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[self convertir:longi]]];
        trame = [trame stringByAppendingString:[self convertir:lat]<0 ? @"O," : @"E," ]; // + Est , - Ouest
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",vit]]; //
        trame = [trame stringByAppendingString:@"degrees,"]; // A faire
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[dateFormatter stringFromDate:[NSDate date]]]];
        trame = [trame stringByAppendingString:@","];
        trame = [trame stringByAppendingString:@","];
        trame = [trame stringByAppendingString:@"A"];
        trame = [trame stringByAppendingString:[self cheksum:trame]];
        NSLog(@"%@", trame);
        
    }];
    
    
    [alert addAction:defaultAction];
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)plotRouteOnMap: (CLLocationCoordinate2D )lastLocation atCurrent2DLocation: (CLLocationCoordinate2D )currentLocation {
    
    CLLocationCoordinate2D *plotLocation = malloc(sizeof(CLLocationCoordinate2D) * 2);
    plotLocation[0] = lastLocation;
    plotLocation[1] = currentLocation;
    MKPolyline *line = [MKPolyline polylineWithCoordinates:plotLocation count:2];
    [carte addOverlay:line];
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
-(IBAction)clear:(id)sender{
    [carte removeAnnotations:carte.annotations];
    [carte removeOverlays:carte.overlays];
    lastLocation = kCLLocationCoordinate2DInvalid;
    [arrayAnnotation removeAllObjects];
}

-(IBAction)send:(id)sender{
    NSLog(@"salut");
}

- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view
{
    alert = [UIAlertController alertControllerWithTitle:@"WayPoint"                                                                         message:@"Veuillez entrer une vitesse"                                                                    preferredStyle:UIAlertControllerStyleAlert];
    [alert addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        textField.placeholder = @"5.2"; // if needs
    }];
    defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault                                                                  handler:^(UIAlertAction * action) {
        NSString *vit = self->alert.textFields[0].text;
        MKPointAnnotation *annotationPoint = (MKPointAnnotation*)view;
        NSNumber *lat = [NSNumber numberWithDouble:annotationPoint.coordinate.latitude];
        NSNumber *longi = [NSNumber numberWithDouble:annotationPoint.coordinate.longitude];
        NSUInteger indexlat = [self->arrayAnnotation indexOfObject:lat];
        NSUInteger indexlong = [self->arrayAnnotation indexOfObject:longi];
        if([vit isEqualToString:@""]== true){
            vit = [self->arrayAnnotation objectAtIndex:indexlong+1];
        }
        if(indexlat == indexlong-1){
            [self->arrayAnnotation replaceObjectAtIndex:indexlong+1 withObject:vit];
        }
    }];
    [alert addAction:defaultAction];
    [self presentViewController:alert animated:YES completion:nil];
    
}

- (NSString*)convertir:(NSNumber *) gpscond{
    NSNumber *coor = gpscond;
    NSString *stringCoor = [NSString stringWithFormat:@"%@",coor];
    NSString *degree = [stringCoor componentsSeparatedByString:@"."][0];
    NSString *minute = [stringCoor componentsSeparatedByString:@"."][1];
    minute = [@"0." stringByAppendingString:minute];
    double valueminute = [minute doubleValue] * 60;
    minute = [[NSString alloc] initWithFormat: @"%f",valueminute ];
    NSString *minutehead = [minute componentsSeparatedByString:@"."][0];
    NSString *minutebody = [minute componentsSeparatedByString:@"."][1];
    minutebody = [minutebody substringToIndex:4];
    degree = [degree stringByAppendingString:minutehead];
    degree = [degree stringByAppendingString:@"."];
    degree = [degree stringByAppendingString:minutebody];
    
    return degree;
}
- (NSString*)cheksum:(NSString *) trame{
    int crc = 0;
    int i;
    
    for (i = 1; i < trame.length ; i ++) {
        crc ^= [trame characterAtIndex:i];
    }
    NSLog(@"%d", crc);
    NSString *crc2 = [NSString stringWithFormat:@"00%d",crc];
    NSString *hex = [NSString stringWithFormat:@"%lX", (unsigned long)[crc2 integerValue]];
    if(hex.length == 1){
        hex = [@"0" stringByAppendingString:hex];
    }
    hex = [@"*" stringByAppendingString:hex];
    return hex;
}




@end
