const int buttonPin = 2; // the pin that the pushbutton is attached to
int buttonState = 0; // current state of the button
int lastButtonState = 0; // previous state of the button

// the setup function runs once when you press reset or power the board
void setup() {
Serial.begin(9600); // set the baud rate to 9600, same should be of your Serial Monitor
pinMode(buttonPin, INPUT); // initialize digital pin buttonPin as an input.
}

void loop() { // the loop function runs over and over again forever
// read the pushbutton input pin:
buttonState = digitalRead(buttonPin);

// compare the buttonState to its previous state
if (buttonState != lastButtonState) {
// if the state has changed
if (buttonState == HIGH) {
// if the current state is HIGH then the button went from off to on:
Serial.print("1");
} else {
// if the current state is LOW then the button went from on to off:
Serial.print("0");
}
// Delay a little bit to avoid bouncing
delay(50);
}
// save the current state as the last state, for next time through the loop
lastButtonState = buttonState;
}
