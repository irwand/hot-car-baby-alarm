const int buttonPin = 2; // the pin that the pushbutton is attached to
int buttonState = 0; // current state of the button
int lastButtonState = 0; // previous state of the button
const char* serialState = 0;
// the setup function runs once when you press reset or power the board
void setup() {
  Serial.begin(9600); // set the baud rate to 9600, same should be of your Serial Monitor
  pinMode(buttonPin, INPUT);
}

void loop() { // the loop function runs over and over again forever
  // read the pushbutton input pin:
  buttonState = digitalRead(buttonPin);
  if (buttonState == HIGH) {
    serialState = "1";
  } else {
    serialState = "0";
  }
  if (buttonState != lastButtonState) {
    Serial.print(serialState);
    // Delay a little bit to avoid bouncing
    delay(50);
  }
  
  if (Serial.available() > 0) {
    Serial.print(serialState);
    Serial.readString();
  }
  lastButtonState = buttonState;
}
