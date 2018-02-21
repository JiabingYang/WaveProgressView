# WaveProgressView
A custom circle view with one or two waves and a progress text.
The height of the waves depends on the progress and max value.

<img src="/image/WaveProgressViewSample.gif"/>

Features
-----

 - Support one or two waves.
 - Amplitude, direction, width, direction, color and step length are customizable for each wave.
 - Completed text is available at the top of the view.
 - Animation can be turned on or off.

Install
--------

1. Copy the musicplayer-VERSION.aar into the libs directory of your app module.
2. Add the following code into the build.gradle (Module: app):
```groovy
compile fileTree(include: ['*.jar'], dir: 'libs')
compile(name: 'waveprogressview-VERSION', ext: 'aar')
```

Usage
--------

```xml
<com.yjb.view.WaveProgressView
    android:id="@+id/wpv_sample"
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:wpvDirectionOne="left"
    app:wpvDirectionTwo="right"
    app:wpvMax="100"
    app:wpvProgress="50"
    app:wpvSpeed="fast"
    app:wpvWaveAmplitudeRatioOne="0.5"
    app:wpvWaveAmplitudeRatioTwo="0.25"
    app:wpvWaveCountTwo="2" />
```

Sample
--------

See the app module in the project root directory.