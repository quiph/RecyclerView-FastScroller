# <img src ="https://upload.wikimedia.org/wikipedia/commons/b/b5/Kotlin-logo.png" width=24> RecyclerViewFastScroller

![Github release](https://img.shields.io/github/release/quiph/recyclerview-fastscroller.svg)
![Maven version](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/quiph/ui/recyclerviewfastscroller/maven-metadata.xml.svg) ![Code size](https://img.shields.io/github/languages/code-size/quiph/recyclerview-fastscroller.svg?colorB=4bc51d) [![Android Weekly #317](https://img.shields.io/badge/Android%20Weekly-%23317-1eafe6.svg)](https://androidweekly.net/issues/issue-317) [![Kotlin Weekly #101](https://img.shields.io/badge/Kotlin%20Weekly-%23101-9e5ef2.svg)](https://mailchi.mp/kotlinweekly/kotlin-weekly-100-sytgpg6yuv?e=55ca282aa1)

A simple, easy to use and configurable fast scroller for `RecyclerView`

<img src = "https://github.com/quiph/RecyclerView-FastScroller/raw/master/graphics/recording_contacts.gif" width=280> <img src = "https://github.com/quiph/RecyclerView-FastScroller/raw/master/graphics/recording_countries.gif" width=280> <img src = "https://github.com/quiph/RecyclerView-FastScroller/raw/master/graphics/recording_numbers.gif" width=280>

## Adding the dependency
```groovy
implementation 'com.quiph.ui:recyclerviewfastscroller:0.2.0'
```
## Java-only project?
As Kotlin compiles to Java, there's no need to externally add the Kotlin runtime or any other Kotlin dependencies when using this. Also the library is **100% compatible with Java** and requires **no migration** of the base project to Kotlin. 

## Usage:

The base layout type for this fast scroller is a `RelativeLayout` so creating a simple
vertical fast scroller is as simple as adding elements as children to the `RecyclerViewFastScroller` layout tag
```xml
<com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
        android:layout_width="match_parent"
        android:id="@+id/fastscroller"
       android:layout_height="match_parent">
        <android.support.v7.widget.RecyclerView
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"/>
        ....
        other view tags can also come here
        ....
</com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller>
```
    
Since the fast scroller extends a `RelativeLayout` other view tags can also be added to it, with the rule being that 
the `RecyclerView` on which the fast scroller functionality needs to be added be the first element in the `ViewGroup`

#### Programmatically adding the RecyclerView

If the `RecyclerView` to be added to the fast scroller is not available during layout creation time, the same can be done programmatically 
by calling the `attachFastScrollerToRecyclerView` method on the `RecyclerView`

### Linking with items:
To reflect the item on the index of the top-most visible item, make the adapter implement the `OnPopupTextUpdate` interface, which overrides the method
`onChange(int index)` which passes the index of the element whose info needs to be displayed in the popup. 

The `CharSequence` to be displayed should be returned in this method. 

Ex:

```kotlin
class MyAdapter : RecyclerView.Adapter<SomeViewHolder>, OnPopupTextUpdate{
// ....
override fun onChange(position: Int): CharSequence {
   val header = // compute value for header using position
   return header                  
   }
}
``` 

### Getting fast scroller callbacks:

To get the callback from the fast scroller for different states, a listener can be added using the `setHandleStateListener` which accepts an interface of type
`HandleStateListener` which has the following callback methods: 
* `onEngaged` - Called  when the fast scroller is engaged
* `onDragged` - Called on every movement of the fast scroller, note: this does not get called when the handle moves programmatically, i.e when then the Scroll is programmatic
* `onReleased` - Called when the fast scroller is released. 

## Customizable XML Attributes: 

* `trackDrawable` - Adds a custom drawable to the scrolling track, defaults to `null` 
* `handleDrawable` - Adds a custom drawable to the scrolling handle of the fast scroller 
* `popupDrawable` - Adds a custom drawable to the popup used to show the index of the element fast scrolled at
* `popupTextStyle` - Sets the style for the popup text shown
* `fastScrollEnabled` - Boolean flag to enable/ disable the fast scroller, the fast scroller view and track are hidden when disabled
* `popupPosition` - An enum to define where the popup should be shown for the fast scroller, one of 
   1. `beforeTrack` - Positions the popup to be shown before the scroll track
   2. `afterTrack` - Position it after the scroll track
* `handleWidth` - Use to custom set the width of the fast scroll handle - Defaults to 18dp
* `handleHeight` - Use to custom set the height of the fast scroll handle - Defaults to 18dp
* `handleHasFixedSize` - TODO - currently setting this to false doesn't do anything, as the size of the handle is independent of the item count
* `addLastItemPadding` - By default the last item of the `RecyclerView` associated with the fast scroller has an extra padding of the height of the first visible item found, to disable this behaviour set this as `false`
* `supportSwipeToRefresh` - To support smooth scrolling for RecyclerViews nested within a `SwipeRefreshLayout`
* `fastScrollDirection` - TODO - currently the fast scroller only works in the `vertical` direction

## Advanced usage:

* Different color popups can be shown based on the position of the item shown, to do this, implement the `OnPopupViewUpdate` which overrides the 
`onUpdate(position: Int, popupTextView: TextView)` which return void, but has an instance of the `TextView` used in popup, this can be used to change the background. 

Check the sample file [AdvancedFragment](https://github.com/quiph/RecyclerView-FastScroller/blob/master/sample/src/main/java/com/qtalk/sample/fragments/AdvancedFragment.kt) and [AdvancedAdapter](https://github.com/quiph/RecyclerView-FastScroller/blob/master/sample/src/main/java/com/qtalk/sample/adapters/AdvancedAdapter.kt) for example usage

Ex: 
```kotlin
class MyAdapter : RecyclerView.Adapter<SomeViewHolder>, OnPopupViewUpdate{

    override fun onChange(position: Int, popupTextView: TextView) {
       // Do something with the TextView here
       popupTextView.background = Color.RED // change some values etc
       }
}
```
The `popupDrawable` attribute and the `popupTextStyle` attributes can be used to create different kinds of elements, shapes and text appearance combinations, for example like the popup similar to the Google Dialer app:

<img src = "https://raw.githubusercontent.com/quiph/RecyclerView-FastScroller/master/graphics/screenshot_contacts1.png" width = 320>

Check the sample to view the implementation. Many such shapes and text styles can be created.

#### Proguard: 
There is no need for any additional proguard rules when using this. 

### Building the source: 

To build the `aar` using using gradle, simply run the build command, `./gradlew build` this will build and place the aars in the `outputs/aar` folder inside the library
module. The final path may look something like: 
```groovy
"${rootProject.projectDir}/recyclerviewfastscroller/build/outputs/aar"
```

This path has a number of aars in it, (debug and release variants namely). To use these aars in the sample, simple uncomment the following line in `build.gradle` for sample
```groovy
// implementation files("${rootProject.projectDir}/recyclerviewfastscroller/build/outputs/aar/recyclerviewfastscroller-release.aar")
```

Don't forget to comment the project/ module dependency above it. Re-sync the project and run the sample to test how the compiled version would behave. 

#### Uploading to maven central: 

We will be using the Nexus Software Repository for pushing our aars to maven-central, there are different methods to do this, 
another simple way is to upload to bintray and then push to maven-central from there, which one to use can completely depend upon the developer.

Detailed explanation [here](https://gist.github.com/shahsurajk/471a10b63207e44bbbaa4badd9706770). 

Once the environment is setup as mentioned in the gist, run the following command: 
```bash
./gradlew clean build uploadArchives
```

#### TODO: 

* Add support for `horizontal` fast scrolling
* Make handle size flexible to item count in adapter
* Fix 0 item bug, which makes the fast scroller visible