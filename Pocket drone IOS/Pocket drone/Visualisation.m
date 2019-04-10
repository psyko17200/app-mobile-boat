//
//  ViewController.m
//  Pocket drone
//
//  Created by calvin  on 04/04/2019.
//  Copyright © 2019 calvin . All rights reserved.
//

#import "AppDelegate.h"
#import "Visualisation.h"
#import <MapKit/MKAnnotation.h>

@interface Visualisation ()

@end

@implementation Visualisation;
@synthesize carte, deco, connect, logo, lat, lon, vit, defaultAction, alert;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Quitter le clavier au toucher d'écran
    [_ipAddressText setDelegate:self];
    [_portText setDelegate:self];
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissKeyboard)];
    [self.view addGestureRecognizer:tap];
    
    // Affichage dans la vue
    _datalattitude.hidden = YES;
    _datalongitude.hidden = YES;
    carte.hidden = YES;
    _datavitesse.hidden = YES;
    deco.hidden = YES;
    _ipAddressText.hidden = NO;
    _portText.hidden = NO;
    
    // Initialisation
    lastLocation = kCLLocationCoordinate2DInvalid;
    lattitude = nil;
    longitude = nil;
    last_lattitude = 0;
    last_longitude = 0;
    new_lattitude = 0;
    new_longitude = 0;
    angle = 0;
    lastAnnotation = nil;

    // Création de la map
    self.carte.delegate=self;
    MKCoordinateSpan span = MKCoordinateSpanMake(0.002, 0.002); // zoom
    MKCoordinateRegion region = MKCoordinateRegionMake(carte.region.center,span);  // charge la location et le zoom
    [carte setRegion: region]; // affiche
    
    // Active la restriction de rotation
    [self restrictRotation:YES];
}

// Quitter le clavier au tap sur écran
- (void)dismissKeyboard {
    [self.view endEditing:YES];
}

// Quitter le clavier avec bouton Return
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

// Tracé de la trajectoire entre 2 points
- (void)plotRouteOnMap: (CLLocationCoordinate2D )lastLocation atCurrent2DLocation: (CLLocationCoordinate2D )currentLocation {
    CLLocationCoordinate2D *plotLocation = malloc(sizeof(CLLocationCoordinate2D) * 2);
    plotLocation[0] = lastLocation;
    plotLocation[1] = currentLocation;
    MKPolyline *line = [MKPolyline polylineWithCoordinates:plotLocation count:2];
    [carte addOverlay:line];
    [carte setCenterCoordinate:plotLocation[1] animated:YES];
}

// A chaque trame reçu
- (void) messageReceived:(NSString *)message {
    [messages addObject:message];
    [carte removeAnnotation:lastAnnotation];
    NSString *vitesseNoeud = nil;
    
    if([message hasPrefix:@"$"]) {
        NSArray *array = [message componentsSeparatedByString:@"$"];
        message = array[1];
        
        // Définit ancien point
        last_lattitude = new_lattitude;
        last_longitude = new_longitude;
        
        // Décortique la trame reçu et converti les valeurs gps
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
        if([arrayCoordGPS[4] isEqualToString:@"S"]){
            new_lattitude = new_lattitude * -1;
        }
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
        if([arrayCoordGPS[6] isEqualToString:@"W"]){
            new_longitude = new_longitude * -1;
        }
        angle = [arrayCoordGPS[8] doubleValue];

        // Actualise la map avec le nouveau point
        CLLocationCoordinate2D location = CLLocationCoordinate2DMake(new_lattitude,new_longitude);
        MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
        annotationPoint.coordinate = location;
        [carte addAnnotation:annotationPoint];
        lastAnnotation = annotationPoint;
        
        // Si un ancien point existe alors trace une trajectoire
        if (CLLocationCoordinate2DIsValid(lastLocation) ) {
            [self plotRouteOnMap:lastLocation atCurrent2DLocation:location];
        }
        
        // Affiche les variables dans la vue
        _datavitesse.text = vitesseNoeud;
        _datalongitude.text = longitude;
        _datalattitude.text = lattitude;
        
        // Attributs a l'ancien point les coordonnées de l'actuel
        lastLocation.latitude = location.latitude;
        lastLocation.longitude = location.longitude;
    }
}

// Récuperation de l'evenement lié à la connexion
- (void)stream:(NSStream *)theStream handleEvent:(NSStreamEvent)streamEvent {
    switch (streamEvent) {
        case NSStreamEventOpenCompleted: // Connexion OK
            // Affichage dans la vue
            _datavitesse.hidden = NO;
            _datalattitude.hidden = NO;
            _datalongitude.hidden = NO;
            lat.hidden = NO;
            lon.hidden = NO;
            vit.hidden = NO;
            carte.hidden = NO;
            deco.hidden = NO;
            logo.hidden = YES;
            connect.hidden = YES;
            _ipAddressText.hidden = YES;
            _portText.hidden = YES;
            _back.hidden = YES;
            self.tabBarController.tabBar.hidden = YES;
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
                            [self messageReceived:output];
                        }
                    }
                }
            }
            break;
            
        case NSStreamEventHasSpaceAvailable:
            break;
            
        case NSStreamEventErrorOccurred: // Connexion échoué
            // Affiche une pop-up d'erreur
            alert = [UIAlertController alertControllerWithTitle:@"Erreur connexion"                                                                         message:@"Entrées invalides ou serveur deconnecté"                                                                    preferredStyle:UIAlertControllerStyleAlert];
            defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault                                                                  handler:^(UIAlertAction * action) {}];
            
            [alert addAction:defaultAction];
            [self presentViewController:alert animated:YES completion:nil];
            break;
            
        case NSStreamEventEndEncountered:
            [theStream close];
            [theStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
            break;
        default:
            break;
    }
}

// Connexion au serveur
- (IBAction)connectToServer:(id)sender {
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, (__bridge CFStringRef) _ipAddressText.text, [_portText.text intValue], &readStream, &writeStream);
    messages = [[NSMutableArray alloc] init];
    [self open];
}

// Deconnexion au serveur
- (IBAction)disconnect:(id)sender {
    [self close];
    [self.carte removeOverlays:self.carte.overlays];
}

// Ouverture du serveur
- (void)open {
    outputStream = (__bridge NSOutputStream *)writeStream;
    inputStream = (__bridge NSInputStream *)readStream;
    [outputStream setDelegate:self];
    [inputStream setDelegate:self];
    [outputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [inputStream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
    [outputStream open];
    [inputStream open];
}

// Fermeture du serveur
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
    
    // Affichage dans la vue
    carte.hidden = YES;
    _datavitesse.hidden = YES;
    _datalattitude.hidden = YES;
    _datalongitude.hidden = YES;
    deco.hidden = YES;
    lat.hidden = YES;
    lon.hidden = YES;
    vit.hidden = YES;
    logo.hidden = NO;
    _back.hidden = NO;
    _portText.hidden = NO;
    connect.hidden = NO;
    _ipAddressText.hidden = NO;
    self.tabBarController.tabBar.hidden = NO;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

// Redéfinition de la méthode pour changer l'affichage du tracé entre 2 points
- (MKOverlayView *)mapView:(MKMapView *)mapView viewForOverlay:(id<MKOverlay>)overlay
{
    if([overlay isKindOfClass:[MKPolyline class]])
    {
        MKPolylineView *lineView = [[MKPolylineView alloc] initWithPolyline:overlay];
        lineView.lineWidth = 5;
        lineView.strokeColor = [UIColor redColor];
        lineView.fillColor = [UIColor redColor];
        return lineView;
    }
    return nil;
}

// Changement de l'image du marker en bateau + rotation de l'image en fonction du degrés reçu
- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id <MKAnnotation>)annotation
{
    MKAnnotationView *annView = [[MKAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"Boatpin"];
    
    annView.canShowCallout = YES;
    annView.calloutOffset = CGPointMake(-5, 5);
    annView.image=[UIImage imageNamed:@"boat"];
    annView.transform = CGAffineTransformRotate(carte.transform, (angle)*M_PI/180);
    return annView;
}

// Restriction de la rotation
-(void) restrictRotation:(BOOL) restriction
{
    AppDelegate* appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    appDelegate.restrictRotation = restriction;
}

@end

