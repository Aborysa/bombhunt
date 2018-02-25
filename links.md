# Useful Links for Game Creation

## Set Up LibGDX
- Necessary extensions:
    - Free Type
    - Box2d

## Application Installation Failed in Android Studio
I had some trouble testing my app back on the virtual device so I made some researches on the Internet and this is what I found:

The problem seems to be caused by ``Instant Run``

In order to enable ``Instant Run``
: File > Settings > Build,Execution,Deployment > Instant Run > Un-check (Enable Instant Run to hot swap code)
[https://stackoverflow.com/questions/42687607/application-installation-failed-in-android-studio](https://stackoverflow.com/questions/42687607/application-installation-failed-in-android-studio)

## Desktop Mode
The solution consists in editing gradle execution process to use the
task ``desktop:run``.

[https://www.youtube.com/watch?v=wRxPNtG8PVI](https://www.youtube.com/watch?v=wRxPNtG8PVI)

## Fonts
- [Repository of Fonts](https://www.dafont.com/)
- [Online Logo Text Editor](http://flamingtext.com/logo/Design-3D-Text)
- [Adjust Font Size](https://stackoverflow.com/questions/33633395/how-set-libgdx-bitmap-font-size)
- [Free Fonts Type](https://github.com/libgdx/libgdx/wiki/Gdx-freetype)
- [Font Squirrel](https://www.fontsquirrel.com/fonts/list/popular)

## Sounds
- [FreeSounds](https://freesound.org/)
- [ZapSplat](https://www.zapsplat.com/)
- [Online Audio Trimmer](https://audiotrimmer.com/)

## Inputs
- [Manage User Inputs](https://www.youtube.com/watch?v=IsYhkng3r1k)

## Textures
- [Flip Display of a Texture](https://stackoverflow.com/questions/28000623/libgdx-flip-2d-sprite-animation/28000689#28000689)

## Testing On Personal Cellphone
- [Android Studio General Info Build & Run](https://developer.android.com/studio/run/index.html?utm_source=android-studio)
- [Set Up Device](https://developer.android.com/studio/run/device.html)
- [Samsung S7 Developer Mode](https://www.androidcentral.com/how-enable-developer-mode-galaxy-s7)
- [Enable USB Debugging Samsung S7](https://www.verizonwireless.com/support/knowledge-base-203783/)

## Map and Content
- [Open Game Art](https://opengameart.org/)
- [Tiled](https://thorbjorn.itch.io/tiled)

## General Facts
- Fixture User Data
: When creating box2d objects and defining the fixtures, it is preferable to ``.setUserData(this)`` than giving a special string. It will allows the casting of the object on collision.
- Generating Random Numbers - Integer
: [link Stack Overflow](https://stackoverflow.com/questions/5887709/getting-random-numbers-in-java)

    ```{java}
    import java.util.Random;

    Random rand = new Random();

    int  n = rand.nextInt(50) + 1;
    //50 is the maximum and the 1 is our minimum
    ```
- Generating Random Numbers - Float
: [link Stack Overflow](https://stackoverflow.com/questions/6078157/random-nextfloat-is-not-applicable-for-floats)

    ```{java}
    float minX = 50.0f;
    float maxX = 100.0f;

    Random rand = new Random();

    float finalX = rand.nextFloat() * (maxX - minX) + minX;
    ```
- Modify a Value Without Altering its Sign
: [link Tutorial Point](https://www.tutorialspoint.com/java/lang/math_copysign_double.htm)

    ```{java}
    public static double copySign(double magnitude, double sign)
    ```
- Dynamically resizing fixture
: [link Stack Overflow](https://stackoverflow.com/questions/21859786/dynamic-resizing-of-the-body-libgdx)

    ```{java}
    public void resize(float newradius) {
        this.body.destroyFixture(this.fixture);
        fixtureDef.density = (float) (this.mass/(Math.PI*newradius*newradius));
        this.radius = newradius;

        CircleShape circle = new CircleShape();
        circle.setRadius(newradius);
        this.fixtureDef.shape = circle;
        circle.dispose();

        this.fixture = body.createFixture(fixtureDef);
        this.fixture.setUserData(this);
    }
    ```
   
- Rotating image magick
[https://stackoverflow.com/questions/14751011/how-to-rotate-an-image-without-changing-its-size](https://stackoverflow.com/questions/14751011/how-to-rotate-an-image-without-changing-its-size)
```{bash}
for file in *.png; do convert $file -distort SRT 15 rotated-$file; done
```

