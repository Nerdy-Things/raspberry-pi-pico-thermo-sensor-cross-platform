# rasperry-pi-pico-thermo-sensor-cross-platform

## Setup

*Clone the repository and run inside it* 

```bash
git submodule init && git submodule update
```
## Pico code

Use Visual Studio Code and MicroPico plugin. Open the `pico-thermometer` folter in it and follow the instruction below. 
Don't forget to set your Wi-Fi credentials in the `pico-thermometer/main.py` file.

```python
.....
wifi.connect("SSID", "Password") # Enter your credentials
.....
```

## Kotlin-Multiplatform

* `desktop` - In a **terminal**, open `kotlin-multiplatform` and run `./gradlew run`
* `Android` - In **Android Studio** open the folder `kotlin-multiplatform`, and use a *Run* button/menu.
* `iOS` - In **xcode** open the file `iosApp/iosApp.xcodeproj` in xCode and press *Run* 