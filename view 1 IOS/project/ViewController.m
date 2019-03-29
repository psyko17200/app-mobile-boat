//
//  ViewController.m
//  project
//  Bonus: Rotation du bateau, animation (carte , apparition map)
//  A faire : Apres avoir fini vue 3 supprimer tout ce qui est en rapport avec lenvoie de données
//
//  Created by Guillaume Paillard on 25/03/2019.
//  Copyright © 2019 Guillaume Paillard. All rights reserved.
//
#import "ViewController.h"
#import "Annotation.h"
#import <MapKit/MKAnnotation.h>

@interface ViewController ()

@end

@implementation ViewController;
@synthesize carte;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _connectedLabel.text = @"Disconnected";
    lastLocation = kCLLocationCoordinate2DInvalid;
    lastAnnotation = nil;
    //creation de la map
    self.carte.showsUserLocation=YES;
    self.carte.delegate=self;
    CLLocationCoordinate2D location = CLLocationCoordinate2DMake(48.8534,2.3488); // location
    MKCoordinateSpan span = MKCoordinateSpanMake(0.0005, 0.0005); // zoom
    MKCoordinateRegion region = MKCoordinateRegionMake(location,span);  // charge la location et le zoom
    [carte setRegion: region animated:YES]; // affiche
}

- (IBAction) sendMessage {
    
    NSString *response  = [NSString stringWithFormat:@"msg:%@", _dataToSendText.text];
    NSData *data = [[NSData alloc] initWithData:[response dataUsingEncoding:NSASCIIStringEncoding]];
    [outputStream write:[data bytes] maxLength:[data length]];
    
}

- (void)plotRouteOnMap: (CLLocationCoordinate2D )lastLocation atCurrent2DLocation: (CLLocationCoordinate2D )currentLocation {
    //Plot Location route on Map
    CLLocationCoordinate2D *plotLocation = malloc(sizeof(CLLocationCoordinate2D) * 2);
    plotLocation[0] = lastLocation;
    plotLocation[1] = currentLocation;
    MKPolyline *line = [MKPolyline polylineWithCoordinates:plotLocation count:2];
    [carte addOverlay:line];
    [carte setCenterCoordinate:plotLocation[0]];
}

- (void) messageReceived:(NSString *)message {
    [messages addObject:message];
    
    [carte removeAnnotation:lastAnnotation];
    NSString *lattitude = nil;
    NSString *longitude = nil;
    NSString *vitesseNoeud = nil;
    if([message hasPrefix:@"$"]) {
        NSArray *array = [message componentsSeparatedByString:@"$"];
        message = array[1];
        
        NSArray *arrayCoordGPS = [message componentsSeparatedByString:@","];
        lattitude = arrayCoordGPS[3];
        longitude = arrayCoordGPS[5];
        vitesseNoeud = arrayCoordGPS[7];
        
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
        longitude = [longitude stringByAppendingString:arrayCoordGPS[6]];
        
        CLLocationCoordinate2D location = CLLocationCoordinate2DMake([lattitude doubleValue],[longitude doubleValue]);
        MKCoordinateSpan span = MKCoordinateSpanMake(0.0005, 0.0005);
        MKCoordinateRegion region = MKCoordinateRegionMake(location,span);
        [carte setRegion: region animated:YES];
        
        MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
        annotationPoint.coordinate = location;
        [carte addAnnotation:annotationPoint];
        lastAnnotation = annotationPoint;
        
        
        if (CLLocationCoordinate2DIsValid(lastLocation) ) {
            [self plotRouteOnMap:lastLocation atCurrent2DLocation:location];
        }
        
        _datavitesse.text = vitesseNoeud; //noeud*1.852=km/h
        _datalongitude.text = longitude;
        _datalattitude.text = lattitude;
        _dataRecievedTextView.text = message;
        
        lastLocation.latitude = location.latitude;
        lastLocation.longitude = location.longitude;
    }
    //NSLog(@"%@", message);
}

- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    
    NSLog(@"stream event %lu", streamEvent);
    
    switch (streamEvent) {
            
        case NSStreamEventOpenCompleted:
            NSLog(@"Stream opened");
            _connectedLabel.text = @"Connected";
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
            break;
            
        case NSStreamEventEndEncountered:
            
            [theStream close];
            [theStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
            _connectedLabel.text = @"Disconnected";
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
    
    _connectedLabel.text = @"Connected";
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
    
    _connectedLabel.text = @"Disconnected";
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
        lineView.lineWidth = 2;
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
    
    return annView;
}
@end
