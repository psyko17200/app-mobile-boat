//
//  ViewController.m
//  vue3
//
//  Created by Calvin Guillemet on 27/03/2019.
//  Copyright © 2019 Calvin Guillemet. All rights reserved.
//

#import "Trace.h"
#import <MapKit/MKAnnotation.h>
#import "tabBar.h"
@interface Trace ()

@end

@implementation Trace
@synthesize carte, end, clear, vitesse, vitesse_point, alert, defaultAction;

- (void)viewDidLoad {
    [super viewDidLoad];
    defaultVitesse = @"10";
    marker = 0;
    self.carte.showsUserLocation=YES;
    self.carte.delegate=self;
    
    // Initialisation de la map et coordonnées La Rochelle
    double lat = 46.146741;
    double longi = -1.168283;
    CLLocationCoordinate2D location = CLLocationCoordinate2DMake(lat, longi);
    MKCoordinateSpan span = MKCoordinateSpanMake(0.008, 0.008); // zoom
    MKCoordinateRegion region = MKCoordinateRegionMake(location,span);  // charge la location et le zoom
    [carte setRegion: region animated:YES];
    arrayAnnotation= [[NSMutableArray alloc] init];
    lastLocation = kCLLocationCoordinate2DInvalid;
    lastAnnotation = nil;
    
    // Ajout de reconnaissance d'un mouvement pour modifier la vitesse dans la pop-up
    UITapGestureRecognizer *fingerTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleMapFingerTap:)];
    fingerTap.numberOfTapsRequired = 1;
    fingerTap.numberOfTouchesRequired = 1;
    [self.carte addGestureRecognizer:fingerTap];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

// Si l'on touche l'écran pour modifier la vitesse associé à un marker
- (void)handleMapFingerTap:(UIGestureRecognizer *)gestureRecognizer {
    
    if (gestureRecognizer.state != UIGestureRecognizerStateEnded) {
        return;
    }
    
    CGPoint touchPoint = [gestureRecognizer locationInView:self.carte];
    CLLocationCoordinate2D touchMapCoordinate =
    [self.carte convertPoint:touchPoint toCoordinateFromView:self.carte];
    
    MKPointAnnotation *annotationPoint = [[MKPointAnnotation alloc] init];
    annotationPoint.coordinate = touchMapCoordinate;
    annotationPoint.title=@"";
    [self.carte addAnnotation:annotationPoint];
    lastAnnotation = annotationPoint;
    
    if (CLLocationCoordinate2DIsValid(lastLocation) ) {
        [self plotRouteOnMap:lastLocation atCurrent2DLocation:annotationPoint.coordinate];
    }
    lastLocation = annotationPoint.coordinate;
    
    NSNumber *lat = [NSNumber numberWithDouble:annotationPoint.coordinate.latitude];
    NSNumber *longi = [NSNumber numberWithDouble:annotationPoint.coordinate.longitude];
    
    // Ajout de pop up + ajout dans l'array
    alert = [UIAlertController alertControllerWithTitle:@"Marker" message:@"Veuillez entrer une vitesse"                                                                    preferredStyle:UIAlertControllerStyleAlert];
    [alert addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        textField.placeholder = @"5.2"; // if needs
        textField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    }];
    defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
        NSString *vit = self->alert.textFields[0].text;
        if([vit isEqualToString:@""]== true){
            vit = self->defaultVitesse;
        }
        
        [self->arrayAnnotation addObject:lat];
        [self->arrayAnnotation addObject:longi];
        [self->arrayAnnotation addObject:vit];
    }];
    
    
    [alert addAction:defaultAction];
    [self presentViewController:alert animated:YES completion:nil];
    marker = marker + 1;
    [_nb_marker setText:[NSString stringWithFormat:@"%d",marker]];
}

// Tracé entre 2 points 
- (void)plotRouteOnMap: (CLLocationCoordinate2D )lastLocation atCurrent2DLocation: (CLLocationCoordinate2D )currentLocation {
    CLLocationCoordinate2D *plotLocation = malloc(sizeof(CLLocationCoordinate2D) * 2);
    plotLocation[0] = lastLocation;
    plotLocation[1] = currentLocation;
    MKPolyline *line = [MKPolyline polylineWithCoordinates:plotLocation count:2];
    [carte addOverlay:line];
}

// Redéfinition de la méthode pour changer l'affichage du tracé entre 2 points
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

// Supprime tout les points présent sur la map
-(IBAction)clear:(id)sender{
    [carte removeAnnotations:carte.annotations];
    [carte removeOverlays:carte.overlays];
    lastLocation = kCLLocationCoordinate2DInvalid;
    [arrayAnnotation removeAllObjects];
    marker = 0;
    [_nb_marker setText:[NSString stringWithFormat:@"%d",marker]];
}

// Action du bouton envoyer : télécharger .txt des trames NMEA
-(IBAction)send:(id)sender{
    // Récupère la date et l'heure au format qui nous intéresse
    arrayTrame= [[NSMutableArray alloc] init];
    NSDateFormatter *hourFormatter=[[NSDateFormatter alloc] init];
    [hourFormatter setDateFormat:@"hhmmss.000"];
    NSDateFormatter *dateFormatter=[[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"ddMMyy"];
    NSString *totalTrame =@"";
    
    for(int i=0;i<arrayAnnotation.count;i=i+3){
        // Récupère lattitude, longitude et vitesse
        NSNumber *lat = [NSNumber numberWithDouble:[[arrayAnnotation objectAtIndex:i] doubleValue]];
        NSNumber *longi = [NSNumber numberWithDouble:[[arrayAnnotation objectAtIndex:i+1] doubleValue]];
        NSString *vit = [arrayAnnotation objectAtIndex:i+2];
        double deg = 0;
        
        // Calcul le degré
        if(i>0){
            deg = atan2([longi doubleValue] - [[arrayAnnotation objectAtIndex:i-2] doubleValue], [lat doubleValue] - [[arrayAnnotation objectAtIndex:i-3] doubleValue])*180/M_PI;
        }
        
        // Créer une trame NMEA
        NSString *trame = @"$GPRMC,";
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[hourFormatter stringFromDate:[NSDate date]]]];
        trame = [trame stringByAppendingString:@"A,"]; //
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[self convertir:lat]]];
        trame = [trame stringByAppendingString:[self convertir:lat]<0 ? @"S," : @"N," ]; // + Nord, - Sud
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[self convertir:longi]]];
        trame = [trame stringByAppendingString:[self convertir:lat]<0 ? @"O," : @"E," ]; // + Est , - Ouest
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",vit]]; //
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%f,",deg]]; // A faire
        trame = [trame stringByAppendingString:[NSString stringWithFormat:@"%@,",[dateFormatter stringFromDate:[NSDate date]]]];
        trame = [trame stringByAppendingString:@","];
        trame = [trame stringByAppendingString:@","];
        trame = [trame stringByAppendingString:@"A"];
        trame = [trame stringByAppendingString:[self cheksum:trame]];
        [arrayTrame addObject:trame];
        
        totalTrame = [totalTrame stringByAppendingString:[trame stringByAppendingString:@"\n"]];
    }
    [self writeToTextFile:totalTrame]; // on écrit la trame
}

// Ecriture dans un fichier
-(void) writeToTextFile:(NSString *)trame{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *fileName = [NSString stringWithFormat:@"%@/nmealog.txt",documentsDirectory];
    [trame writeToFile:fileName atomically:NO encoding:NSStringEncodingConversionAllowLossy error:nil];
    UIDocumentInteractionController *documentInteractionController =[UIDocumentInteractionController interactionControllerWithURL:[NSURL fileURLWithPath:fileName]];
    documentInteractionController.delegate = self;
    [documentInteractionController presentPreviewAnimated:YES];
}

- (UIViewController *) documentInteractionControllerViewControllerForPreview: (UIDocumentInteractionController *) controller {
    return self;
}

// Création de la pop-up
- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view
{
    alert = [UIAlertController alertControllerWithTitle:@"WayPoint" message:@"Veuillez entrer une vitesse" preferredStyle:UIAlertControllerStyleAlert];
    [alert addTextFieldWithConfigurationHandler:^(UITextField *textField) {
        textField.placeholder = @"5.2"; // if needs
    }];
    defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
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

// Convertir les coordonnées gps
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

// Obtenir somme de contrôle de parité au format hexadécimal
- (NSString*)cheksum:(NSString *) trame{
    int crc = 0;
    int i;
    
    for (i = 1; i < trame.length ; i ++) {
        crc ^= [trame characterAtIndex:i];
    }
    NSString *crc2 = [NSString stringWithFormat:@"00%d",crc];
    NSString *hex = [NSString stringWithFormat:@"%lX", (unsigned long)[crc2 integerValue]];
    if(hex.length == 1){
        hex = [@"0" stringByAppendingString:hex];
    }
    hex = [@"*" stringByAppendingString:hex];
    ;
    return hex;
}


@end
