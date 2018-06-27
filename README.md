#RecyclerViewFastScroller

A simple, easy to use and configurable fast scroller for `RecyclerView`

##Usage:

The base layout type for this fast scroller is a `RelativeLayout` so creating a simple
vertical fast scroller is as simple as adding elements as children to the `RecyclerViewFastScroller` layout tag

    <com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
            android:layout_width="match_parent"
            android:id="@+id/fastscroller"
           android:layout_height="match_parent">
            <android.support.v7.widget.RecyclerView
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"/>
            ....
            other views can also come here
            ....
    </com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller>
    
Since the fast scroller extends a `RelativeLayout` other view can also be added to it, with the rule of 
the `RecyclerView` on which the fast scroller functionality needs to be added is the first element in the view group

####Programmatically adding the RecyclerView

If the `RecyclerView` to be added to the fast scroller is not available during layout creation time, the same can be done programmatically 
by calling the `attachFastScrollerToRecyclerView` method on the `RecyclerView`

###Linking with items:
To reflect the item on the index of the top-most visible item, add make the adapter implement the `OnPopupTextUpdate` interface, which override the method
`onChange(int index)` which passes the index of the element whose info needs to be displayed in the popup. 

The `CharSequence` to be displayed should be returned in this method. 

Ex:

    class MyAdapter : RecyclerView.Adapter .... implements OnPopupTextUpdate{
    ....
    override fun onChange(position: Int): CharSequence {
       val header = ......
       return header                  
       }
    } 

###Getting fast scroller callbacks:

To get the callback from the fast scroller for different states, a listener can be added using the `setHandleStateListener` which accepts an interface of type
`HandleStateListener` which has the following callback methods: 
* `onEngaged` - Called  when the fast scroller is enaged
* `onDragged` - Called on every movement of the fast scroller, note: this does not get called when the handle moves programmatically, i.e when then the Scroll is programmatic
* `onReleased` - Called when the fast scroller is released. 

##Customizable XML Attributes: 

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
* `handleHasFixedSize` - TODO - currently setting this to false doesn`t do anything, as the size of the handle is independent of the item count
* `addLastItemPadding` - By default the last item of the `RecyclerView` associated with the fast scroller has an extra padding of the height of the first visible item found, to disable this behaviour set this as `false`
* `fastScrollDirection` - TODO - currently the fast scroller only works in the `vertical` direction

##Advanced usage:

* Different color popups can be shown based on the position of the item shown, to do this, implement the `OnPopupViewUpdate` which overrides the 
`onUpdate(position: Int, popupTextView: TextView)` which return void, but has an instance of the `TextView` used in popup, this can be used to change the background. 

Ex: 
    
    class MyAdapter : RecyclerView.Adapter .... implements OnPopupViewUpdate{
        ....
        override fun onChange(position: Int, popupTextView: TextView) {
           // Do something with the TextView here
           popupTextView.setBackground(....)
           }
    }
#### Proguard: 
There is no need for any additional proguard rules when using this. 

#### TODO: 

* Add support for `horizontal` fast scrolling
* Make handle size flexible to item count in adapter
* Fix 0 item bug, which makes the fast scroller visible 