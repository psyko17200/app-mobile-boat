//
//  ViewController.m
//  Pocket drone
//
//  Created by calvin  on 04/04/2019.
//  Copyright © 2019 calvin . All rights reserved.
//

#import "ViewController.h"
#import <MapKit/MKAnnotation.h>

@interface ViewController ()

@end

@implementation ViewController;
@synthesize carte, deco, connect, logo, lat, lon, vit, defaultAction, alert;

- (void)viewDidLoad {
    [super viewDidLoad];
    [_ipAddressText setDelegate:self];
    [_portText setDelegate:self];
    // Cache inutile
    _datalattitude.hidden = YES;
    _datalongitude.hidden = YES;
    
    _ipAddressText.hidden = NO;
    _portText.hidden = NO;
    lastLocation = kCLLocationCoordinate2DInvalid;
    
    lattitude = nil;
    longitude = nil;
    last_boat = @"boat";
    last_lattitude = 0;
    last_longitude = 0;
    new_lattitude = 0;
    new_longitude = 0;
    angle = 0;
    
    lastAnnotation = nil;
    carte.hidden = YES; // Map cacher
    _datavitesse.hidden = YES;
    deco.hidden = YES; // deco button cacher
    
    self.carte.delegate=self;
    MKCoordinateSpan span = MKCoordinateSpanMake(0.001, 0.001); // zoom
    MKCoordinateRegion region = MKCoordinateRegionMake(carte.region.center,span);  // charge la location et le zoom
    [carte setRegion: region animated:YES]; // affiche
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (void)plotRouteOnMap: (CLLocationCoordinate2D )lastLocation atCurrent2DLocation: (CLLocationCoordinate2D )currentLocation {
    //Plot Location route on Map
    CLLocationCoordinate2D *plotLocation = malloc(sizeof(CLLocationCoordinate2D) * 2);
    plotLocation[0] = lastLocation;
    plotLocation[1] = currentLocation;
    MKPolyline *line = [MKPolyline polylineWithCoordinates:plotLocation count:2];
    [carte addOverlay:line];
    [carte setCenterCoordinate:plotLocation[1] animated:YES];
}

- (void) messageReceived:(NSString *)message {
    [messages addObject:message];
    
    [carte removeAnnotation:lastAnnotation];
    NSString *vitesseNoeud = nil;
    if([message hasPrefix:@"$"]) {
        NSArray *array = [message componentsSeparatedByString:@"$"];
        message = array[1];
        
        last_lattitude = new_lattitude;
        last_longitude = new_longitude;
        NSArray *arrayCoordGPS = [message componentsSeparatedByString:@","];
        lattitude = arrayCoordGPS[3];
        longitude = arrayCoordGPS[5];
        vitesseNoeud = arrayCoordGPS[7];
        vitesseNoeud = [vitesseNoeud stringByAppendingString:@" Knots"];
        
        NSArray *arraylattitude = [lattitude componentsSeparatedByString:@"."];
        
        NSString *degreetotal = arraylattitude[0];
        NSString *degree = [degreetotal substringToIndex:degreetotal.length-2];
        NSString *minute = [degreetotal substringFromIndex:degreetotal.length-2];
        NSString *sec = arraylattitude[1];
        minute = [minute stringByAppendingString:@"."];
        minute = [minute stringByAppendingString:sec];
        double valueminute = [minute doubleValue];
        valueminute = valueminute/60;
        minute = [[NSString alloc] initWithFormat: @"%f",valueminute ];
        minute = [minute componentsSeparatedByString:@"."][1];
        lattitude = [degree stringByAppendingString:@"."];
        lattitude = [lattitude stringByAppendingString:minute];
        new_lattitude = [lattitude doubleValue];
        lattitude = [lattitude stringByAppendingString:arrayCoordGPS[4]];
        
        NSArray *arraylongitude = [longitude componentsSeparatedByString:@"."];
        
        NSString *degreetotal2 = arraylongitude[0];
        NSString *degree2 = [degreetotal2 substringToIndex:degreetotal2.length-2];
        NSString *minute2 = [degreetotal2 substringFromIndex:degreetotal2.length-2];
        NSString *sec2 = arraylongitude[1];
        minute2 = [minute2 stringByAppendingString:@"."];
        minute2 = [minute2 stringByAppendingString:sec2];
        double valueminute2 = [minute2 doubleValue];
        valueminute2 = valueminute2/60;
        minute2 = [[NSString alloc] initWithFormat: @"%f",valueminute2 ];
        minute2 = [minute2 componentsSeparatedByString:@"."][1];
        longitude = [degree2 stringByAppendingString:@"."];
        longitude = [longitude stringByAppendingString:minute2];
        new_longitude = [longitude doubleValue];
        longitude = [longitude stringByAppendingString:arrayCoordGPS[6]];
        
        angle = [arrayCoordGPS[8] doubleValue];

        CLLocationCoordinate2D location = CLLocationCoordinate2DMake([lattitude doubleValue],[longitude doubleValue]);
        
        MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
        annotationPoint.coordinate = location;
        [carte addAnnotation:annotationPoint];
        lastAnnotation = annotationPoint;
        
        
        if (CLLocationCoordinate2DIsValid(lastLocation) ) {
            [self plotRouteOnMap:lastLocation atCurrent2DLocation:location];
        }
        
        _datavitesse.text = vitesseNoeud;
        _datalongitude.text = longitude;
        _datalattitude.text = lattitude;
        
        lastLocation.latitude = location.latitude;
        lastLocation.longitude = location.longitude;
    }
}

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
    NSLog(@"stream event %lu", streamEvent);
    
    switch (streamEvent) {
            
        case NSStreamEventOpenCompleted:
            NSLog(@"Stream opened");
            _datavitesse.hidden = NO;
            logo.hidden = YES;
            carte.hidden = NO; // carte apparait en full screen
            deco.hidden = NO; // deco button apparait en full screen
            connect.hidden = YES; // deco button apparait en full screen
            _ipAddressText.hidden = YES;
            _portText.hidden = YES;
            _datalattitude.hidden = NO;
            _datalongitude.hidden = NO;
            lat.hidden = NO;
            lon.hidden = NO;
            vit.hidden = NO;
            break;
        case NSStreamEventHasBytesAvailable:
            
            if (theStream == inputStream)
            {
                uint8_t buffer[1024];
                NSInteger len;
                
                while ([inputStream hasBytesAvailable])
                {
                    len = [inputStream read:buffer maxLength:sizeof(buffer)];
                    if (len > 0)
                    {
                        NSString *output = [[NSString alloc] initWithBytes:buffer length:len encoding:NSASCIIStringEncoding];
                        
                        if (nil != output)
                        {
                            NSLog(@"server said: %@", output);
                            [self messageReceived:output];
                        }
                    }
                }
            }
            break;
            
        case NSStreamEventHasSpaceAvailable:
            NSLog(@"Stream has space available now");
            break;
            
        case NSStreamEventErrorOccurred:
            NSLog(@"%@",[theStream streamError].localizedDescription);
            alert = [UIAlertController alertControllerWithTitle:@"Erreur connection"                                                                         message:@"Entrées invalides ou serveur deconnecté"                                                                    preferredStyle:UIAlertControllerStyleAlert];
            defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault                                                                  handler:^(UIAlertAction * action) {}];
            
            [alert addAction:defaultAction];
            [self presentViewController:alert animated:YES completion:nil];
            break;
            
        case NSStreamEventEndEncountered:
            
            [theStream close];
            [theStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
            NSLog(@"close stream");
            break;
        default:
            NSLog(@"Unknown event");
    }
}

- (IBAction)connectToServer:(id)sender {
    
    NSLog(@"Setting up connection to %@ : %i", _ipAddressText.text, [_portText.text intValue]);
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef) _ipAddressText.text, [_portText.text intValue], &readStream, &writeStream);
    
    messages = [[NSMutableArray alloc] init];
    [self open];
}

- (IBAction)disconnect:(id)sender {
    [self close];
    [self.carte removeOverlays:self.carte.overlays];
}

- (void)open {
    
    NSLog(@"Opening streams.");
    
    outputStream = (__bridge NSOutputStream *)writeStream;
    inputStream = (__bridge NSInputStream *)readStream;
    
    [outputStream setDelegate:self];
    [inputStream setDelegate:self];
    
    [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    
    [outputStream open];
    [inputStream open];
    
}

- (void)close {
    NSLog(@"Closing streams.");
    [inputStream close];
    [outputStream close];
    [inputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [outputStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [inputStream setDelegate:nil];
    [outputStream setDelegate:nil];
    inputStream = nil;
    outputStream = nil;
    carte.hidden = YES; // Map cacher
    _datavitesse.hidden = YES;
    logo.hidden = NO;
    _datalattitude.hidden = YES;
    _datalongitude.hidden = YES;
    deco.hidden = YES; // deco button caché
    connect.hidden = NO; // deco button apparait en full screen
    _ipAddressText.hidden = NO;
    _portText.hidden = NO;
    lat.hidden = YES;
    lon.hidden = YES;
    vit.hidden = YES;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id<MKOverlay>)overlay
{
    if([overlay isKindOfClass:[MKPolyline class]])
    {
        MKPolylineView *lineView = [[MKPolylineView alloc] initWithPolyline:overlay];
        lineView.lineWidth = 4;
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
    annView.calloutOffset = CGPointMake(-5, 5);
    annView.image=[UIImage imageNamed:@"boat"];
    annView.transform = CGAffineTransformRotate(carte.transform, (-angle)*M_PI/180);
    return annView;
}
@end

