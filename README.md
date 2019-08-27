## NaturalMouseMotion RS ##

Contains custom flow templates and factory templates for Old School Runescape

- All random flows are 45 doubles in len
- randomFlowLow() - between (1) and (50) 
- randomFlowLowest() -between (1) and (25)
- randomFlowHigh() -  between (50) and (90) 
- randomFlowWide() - between (1) and (110) 
- randomDouble(double min, double max) - Random double in range
- The default natural mouse random() and others are also included.

Some of the features NaturalMouseMotion contains:

  * **Deviation**: Deviation leads the mouse away from direct trajectory, creating and arc instead of straight line
  * **Noise**: Noise creates errors in the movement, this can simulate hand shakiness, someone using a non accurate mouse or bad surface under the mouse.
  * **Speed** and **flow**: Speed and flow are defining the progressing of the mouse at given time, for example it's possible that movement starts slow and then gains speed, or is just variating.
  * **Overshoots**: Overshoots happen if user is not 100% accurate with the mouse and hits an area next to the target instead, requiring to adjust the cursor to reach the actual target.
  * **Coordinate translation**: Coordinate translation allows to specify offset and dimensions to restrict a movement in a different area than the screen or in a virtual screen inside the real screen.

## Demonstration video (2.0.0): ## 
https://www.youtube.com/watch?v=CuG9LvQ0fdQ

## Maven: ##

```
<dependency>
  <groupId>com.github.hypsynz.naturalmouse</groupId>
  <artifactId>naturalmouse</artifactId>
  <version>2.0.9</version>
</dependency>
```

## Gradle: ##

```
 implementation group: 'com.github.hypsynz.naturalmouse', name: 'naturalmouse', version: '2.0.9'
```
