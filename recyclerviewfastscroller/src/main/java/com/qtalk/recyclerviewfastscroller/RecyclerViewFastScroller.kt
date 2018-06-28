package com.qtalk.recyclerviewfastscroller

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.*
import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * Sets a custom scroller for [RecyclerView].
 *
 * For best results use the FastScroller without a scrollbar set in the [RecyclerView]
 *
 * To set programmatically call the [attachFastScrollerToRecyclerView] once the [RecyclerView.LayoutManager] and [RecyclerView.Adapter] are initialized, else if a child of this view is a [RecyclerView] then the method is called directly,
 * without the programmer explicitly needing to call it.
 *
 * **Direction:** The direction of the [RecyclerViewFastScroller] can be set using the [R.styleable.RecyclerViewFastScroller_fastScrollDirection] view attrib.
 *
 * **Scroll layout drawing:** The drawing of the scrollbar layout can be changed during layout creation using the [R.styleable.RecyclerViewFastScroller_popupPosition] param which takes
 * `PopupPosition.BEFORE_TRACK` and `PopupPosition.AFTER_TRACK` attribs
 *
 * **Layout customizations:**
 *  1. Popup Layout: The popup layout background can be changed both programmatically with [RecyclerViewFastScroller.popupDrawable] and using the [R.styleable.RecyclerViewFastScroller_popupDrawable] view attrib.
 *  2. Handle Layout: The handle layout background can be changed both programmatically with [RecyclerViewFastScroller.handleDrawable] and using the [R.styleable.RecyclerViewFastScroller_handleDrawable] view attrib.
 *  3. Track Layout: The track layout background can be changed both programmatically with [RecyclerViewFastScroller.trackDrawable] and using the [R.styleable.RecyclerViewFastScroller_trackDrawable] view attrib.
 *  4. Popup [TextView] appearance can be changed both programmatically with [RecyclerViewFastScroller.textStyle] and using the [R.styleable.RecyclerViewFastScroller_popupTextStyle] view attrib.
 *
 *  By default the last item of the [RecyclerView] associated with the fastScroller has an extra padding of the height of the first visible item found, to disable this behaviour set the [R.styleable.RecyclerViewFastScroller_addLastItemPadding] as `false`
 *
 *  To disable fastScroll, set the [RecyclerViewFastScroller.isFastScrollEnabled] as `false`, default set is true
 *
 *  **Handle Size:** The fastScroller automatically adjusts the size of the handle, but if [RecyclerViewFastScroller.isFixedSizeHandle] is set as true handle [R.styleable.RecyclerViewFastScroller_handleHeight]
 *  and [R.styleable.RecyclerViewFastScroller_handleWidth] need to be provided else default value of 18dp will be taken for both.
 *
 * @version 1.0
 * */

//todo@shahsurajk write for x direction
class RecyclerViewFastScroller @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG: String = "RVFastScroller"
        private const val ERROR_MESSAGE_NO_RECYCLER_VIEW = "The RecyclerView required for initialization with FastScroller cannot be null"
    }
    private enum class FastScrollDirection(val value: Int){
        HORIZONTAL(1), VERTICAL(0);
        companion object {
            fun getFastScrollDirectionByValue(value: Int = Defaults.fastScrollDirection.value): FastScrollDirection {
                for (fsDirection in values()){
                    if (fsDirection.value == value) return fsDirection
                }
                return Defaults.fastScrollDirection
            }
        }
    }
    private enum class PopupPosition(val value: Int) {
        BEFORE_TRACK(0), AFTER_TRACK(1);
        companion object {
            fun getPopupPostionByValue(value: Int = Defaults.popupPosition.value): PopupPosition {
                for(popupPosition : PopupPosition in values()){
                    if (popupPosition.value == value)
                        return popupPosition
                }
                return Defaults.popupPosition
            }
        }
    }
    //    defaults to be used throughout this class. All these values can be overriden in the individual methods provided for the main class
    private object Defaults {
        internal val popupDrawableInt : Int = R.drawable.custom_bg_primary
        internal val handleDrawableInt : Int = R.drawable.custom_bg_primary
        internal val handleSize : Int = R.dimen.default_handle_size
        internal val textStyle : Int = R.style.FastScrollerTextAppearance
        internal val popupPosition : PopupPosition = PopupPosition.BEFORE_TRACK
        internal val fastScrollDirection : FastScrollDirection = FastScrollDirection.VERTICAL
        internal const val isFixedSizeHandle : Boolean = false
        internal const val isFastScrollEnabled : Boolean = true
        internal const val DEFAULT_ANIM_DURATION : Long = 100
        internal const val DEFAULT_POPUP_VISIBLITY_DURATION = 200L
        internal const val hasEmptyItemDecorator : Boolean = true
    }

    /**
     * Sets a track background drawable to the track used, default is `null`
     * */
    var trackDrawable : Drawable?
        set(value) {
            trackView.background = value
        }
        get() = trackView.background
    /**
     * Sets background drawable to the [TextView] used in the popup
     * */
    var popupDrawable : Drawable?
        set(value) {
            popupTextView.background = value
        }
        get() = popupTextView.background
    /**
     * Sets a drawable to the handle used to scroll with
     **/
    var handleDrawable : Drawable?
        set(value) {
            handleImageView.setImageDrawable(requireNotNull(value) {"No drawable found for the given ID"})
        }
        get() = handleImageView.drawable

    /**
     * Sets a style to the [TextView] used in the popup displayed
     **/
    @StyleRes var textStyle : Int = Defaults.textStyle
        set(value) {
            TextViewCompat.setTextAppearance(popupTextView, value)
        }
    /**
     * todo@shahsurajk handleFixedSizes
     **/
    private var isFixedSizeHandle : Boolean = Defaults.isFixedSizeHandle
    /**
     * If set to `false`, the fastScroll behavior is disabled, Default is `true`
     **/
    var isFastScrollEnabled : Boolean = Defaults.isFastScrollEnabled

    /**
     * The [TextView] which is used to display the popup text.
     **/
    lateinit var popupTextView: TextView

    // --- internal properties
    private var popupPosition : PopupPosition = Defaults.popupPosition
    private var fastScrollDirection : FastScrollDirection = Defaults.fastScrollDirection
    private var hasEmptyItemDecorator : Boolean = Defaults.hasEmptyItemDecorator
    private lateinit var handleImageView : ImageView
    private lateinit var trackView: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private var popupAnimationRunnable: Runnable
    private var isEngaged : Boolean = false
    private var handleStateListener : HandleStateListener? =null
    private var previousTotalVisibleItem : Int = 0

    // property check
    /**
     * Checks if the [FastScrollDirection] is [FastScrollDirection.VERTICAL] or not
     *
     * @return `true` if yes else `false`
     * */
    val isVertical : Boolean = fastScrollDirection == FastScrollDirection.VERTICAL

    //    load attributes to set view based values
    private val attribs : TypedArray? = if (attrs!=null){
        context.theme.obtainStyledAttributes(attrs, R.styleable.RecyclerViewFastScroller, 0, 0)
    }else{
        null
    }

    init {
        //add popup layout and thumb layout.
        addPopupLayout()
        addThumbAndTrack()

        attribs?.let {
            if (attribs.hasValue(R.styleable.RecyclerViewFastScroller_popupPosition)){
                popupPosition = PopupPosition.getPopupPostionByValue(attribs.getInt(R.styleable.RecyclerViewFastScroller_popupPosition, Defaults.popupPosition.value))
            }
            if (attribs.hasValue(R.styleable.RecyclerViewFastScroller_fastScrollDirection)){
                fastScrollDirection = FastScrollDirection.getFastScrollDirectionByValue(attribs.getInt(R.styleable.RecyclerViewFastScroller_fastScrollDirection, Defaults.fastScrollDirection.value))
            }

            isFixedSizeHandle = attribs.getBoolean(R.styleable.RecyclerViewFastScroller_handleHasFixedSize, Defaults.isFixedSizeHandle)

            isFastScrollEnabled = attribs.getBoolean(R.styleable.RecyclerViewFastScroller_fastScrollEnabled, Defaults.isFastScrollEnabled)

            hasEmptyItemDecorator = attribs.getBoolean(R.styleable.RecyclerViewFastScroller_addLastItemPadding, Defaults.hasEmptyItemDecorator)

            trackView.background = attribs.getDrawable(R.styleable.RecyclerViewFastScroller_trackDrawable)

            //align added layouts based on configurations in use.
            alignTrackAndHandle()
            alignPopupLayout()

            // if not defined, set default popupTextView background
            popupTextView.background = if (attribs.hasValue(R.styleable.RecyclerViewFastScroller_popupDrawable) == true){
                loadDrawableFromAttribs(R.styleable.RecyclerViewFastScroller_popupDrawable)
            }else{
                ContextCompat.getDrawable(context, Defaults.popupDrawableInt)
            }

            // set default handleImageView drawable if not defined
            handleImageView.setImageDrawable((loadDrawableFromAttribs(R.styleable.RecyclerViewFastScroller_handleDrawable)?: ContextCompat.getDrawable(context, Defaults.handleDrawableInt)))

            refreshHandleImageViewSize()

            TextViewCompat.setTextAppearance(popupTextView, attribs.getResourceId(R.styleable.RecyclerViewFastScroller_popupTextStyle, Defaults.textStyle))

            attribs.recycle()
        }
        popupAnimationRunnable = Runnable { popupTextView.animateVisibility(false) }
    }

    override fun onDetachedFromWindow() {
        detachFastScrollerFromRecyclerView()
        super.onDetachedFromWindow()
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        // skip the two children add, aka popup and track and check if the RecyclerView is added via XML or not, if added call the attach directly.
        if (childCount > 2){
            for (childAt : Int in 2 until childCount){
                val currentView = getChildAt(childAt)
                if (currentView is RecyclerView){
                    removeView(currentView)
                    addView(currentView, 0)
                    attachFastScrollerToRecyclerView(currentView)
                }
            }
        }
        post {
            val locationArray = IntArray(2)
            //this offset is to nullify the difference in the height between the handle and touch's relative value
            var touchRelativeOffset = 0f
            val touchListener  = OnTouchListener { _, motionEvent ->
                //getting the position of this view on the screen, getting absolute X and Y coordinates
                getLocationInWindow(locationArray)
                val yAbsPosition : Int = locationArray[1]
                val xAbsPosition : Int = locationArray[0]
                when(motionEvent.action){
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN  -> {
                        // disallow parent to spy on touch events
                        requestDisallowInterceptTouchEvent(true)
                        if (motionEvent.action == MotionEvent.ACTION_DOWN){
                            if (!adapterDataObserver.isInitialized()){
                                registerDataObserver()
                            }
                            checkNotNull(recyclerView) { ERROR_MESSAGE_NO_RECYCLER_VIEW }

                            //compute relative touch offset only on DOWN and use the same for MOVE events
                            touchRelativeOffset = motionEvent.rawY - yAbsPosition - handleImageView.y

                            //check if offset is in bounds
                            if (Math.abs(touchRelativeOffset)>handleImageView.height) touchRelativeOffset = 0f

                            //set the engaged flag to prevent the handle from scrolling again as the OnScrolled event in the ScrollListener is called even for programmatic scrolls
                            isEngaged = true

                            if (isFastScrollEnabled){
                                handleStateListener?.onEngaged()
                                //make the popup visible only if fastScroll is enabled
                                popupTextView.animateVisibility()
                            }
                        }
                        //
                        //--------------------------- Common methods to Move and Down events ---------------------------------
                        //                       calculate relative Y position internal to the view, from motion absolute px touch value and absolute start point of the view
                        //
                        val currentRelativeYPos = motionEvent.rawY - yAbsPosition - touchRelativeOffset

                        //move the handle only if fastScrolled, else leave the translation of the handle to the onScrolled method on the listener

                        if (isFastScrollEnabled){
                            moveViewByRelativeYInBounds(handleImageView, currentRelativeYPos)
                            moveViewByRelativeYInBounds(popupTextView, currentRelativeYPos-popupTextView.height)
                            val position = recyclerView.computePositionForOffsetAndScroll(currentRelativeYPos)
                            if (motionEvent.action == MotionEvent.ACTION_MOVE){
                                handleStateListener?.onDragged(handleImageView.y, position)
                            }
                            updateTextInPopup(Math.min((recyclerView.adapter.itemCount)-1, position))
                        }else{
                            recyclerView.scrollBy(0, currentRelativeYPos.toInt())
                        }

                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->{
                        isEngaged = false
                        if (isFastScrollEnabled){
                            handleStateListener?.onReleased()
                            //hide the popup with a default anim delay
                            handler.postDelayed(popupAnimationRunnable, Defaults.DEFAULT_POPUP_VISIBLITY_DURATION)
                        }
                        super.onTouchEvent(motionEvent)
                    }
                    else -> {
                        false
                    }
                }}

            //set the same touch listeners to both handle and track as they have the same functionality
            handleImageView.setOnTouchListener(touchListener)
            trackView.setOnTouchListener(touchListener)
        }
    }
    private fun alignPopupLayout() {
        val lpPopupLayout = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also {
            when (popupPosition) {
                PopupPosition.BEFORE_TRACK -> {
                    it.addRule(LEFT_OF, trackView.id)
                }
                PopupPosition.AFTER_TRACK -> {
                    it.addRule(RIGHT_OF, trackView.id)
                }
            }
        }
        popupTextView.layoutParams = lpPopupLayout
    }
    private fun alignTrackAndHandle() {
        val lp = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val lpTrackLayout: LayoutParams = when (fastScrollDirection) {
            FastScrollDirection.HORIZONTAL -> {
                lp.gravity = Gravity.END
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).also { it.addRule(ALIGN_PARENT_BOTTOM) }

            }
            FastScrollDirection.VERTICAL -> {
                lp.gravity = Gravity.TOP
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).also { it.addRule(ALIGN_PARENT_RIGHT) }
            }
        }
        handleImageView.layoutParams = lp
        trackView.layoutParams = lpTrackLayout
    }
    private fun refreshHandleImageViewSize(newComputedSize : Int = -1){
//        todo@shahsurajk add fork for horizontal layout
        if (newComputedSize==-1){
            handleImageView.layoutParams.width = loadHandleWidth().toInt()
            handleImageView.layoutParams.height = loadHandleHeight().toInt()
        }else{
            TODO("@shahsurajk dynamic sizing of handle")
        }
    }
    private fun addThumbAndTrack(){
        View.inflate(context,R.layout.fastscroller_track_thumb, this)
        handleImageView = findViewById(R.id.thumbIV)
        trackView = findViewById(R.id.trackView)
    }
    private fun addPopupLayout(){
        View.inflate(context, R.layout.fastscroller_popup, this)
        popupTextView = findViewById(R.id.fastScrollPopupTV)
    }

    /**
     * Checks the bounds of the view before moving
     *
     * @param view the view to move and
     * @param finalOffset the offset to move to
     * */
    private fun moveViewByRelativeYInBounds(view: View, finalOffset: Float){
        view.y =  Math.min(Math.max(finalOffset, 0f), (height.toFloat()-view.height.toFloat()))
    }

    /**
     * Custom animator extension, as [ViewPropertyAnimator] doesn't have individual listeners, decluttering, also present in android KTX
     * */
    private inline fun ViewPropertyAnimator.onAnimationCancelled(crossinline body: () -> Unit){
        this.setListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
                body()
            }
        })
    }

    /**
     * Animates the view visiblity based on the [makeVisible] param
     *
     * @param makeVisible
     * */
    private fun View.animateVisibility(makeVisible : Boolean = true) {

        val scaleFactor: Float = if (makeVisible) 1f else 0f
        this.animate().scaleX(scaleFactor).setDuration(Defaults.DEFAULT_ANIM_DURATION).onAnimationCancelled{
            this.animate().scaleX(scaleFactor).duration = Defaults.DEFAULT_ANIM_DURATION
        }
        this.animate().scaleY(scaleFactor).setDuration(Defaults.DEFAULT_ANIM_DURATION).onAnimationCancelled {
            this.animate().scaleY(scaleFactor).duration = Defaults.DEFAULT_ANIM_DURATION
        }
    }
    private fun resizeThumbDrawable(){
        if (isFixedSizeHandle) return

        /*if (orientation == HORIZONTAL){
            thumbDrawable.setSize(thumbDrawable.bounds.width(), computeHandleSize())
        }else{
            thumbDrawable.setSize(computeHandleSize(), thumbDrawable.bounds.height())
        }*/
    }

    //    set of load methods for handy loading from attribs
    private fun loadDimenFromResource(@DimenRes dimenSize: Int) : Float = context.resources.getDimension(dimenSize)
    private fun loadHandleHeight() =
            attribs?.getDimension(R.styleable.RecyclerViewFastScroller_handleHeight, loadDimenFromResource(Defaults.handleSize))
                    ?: loadDimenFromResource(Defaults.handleSize)

    private fun loadHandleWidth() =
            attribs?.getDimension(R.styleable.RecyclerViewFastScroller_handleWidth, loadDimenFromResource(Defaults.handleSize))
                    ?: loadDimenFromResource(Defaults.handleSize)


    private fun loadDrawableFromAttribs(@StyleableRes styleId : Int)
            = attribs?.getDrawable(styleId)

    //    extension functions to get the total visible count of items.
    private fun LinearLayoutManager.getTotalVisibleItemCount(): Int {
        val firstVisibleItemPosition = this.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = this.findLastVisibleItemPosition()
        if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION
        return lastVisibleItemPosition - firstVisibleItemPosition
    }

    private fun LinearLayoutManager.getTotalCompletelyVisibleItemCount(): Int {
        val firstVisibleItemPosition = this.findFirstCompletelyVisibleItemPosition()
        val lastVisibleItemPosition = this.findLastCompletelyVisibleItemPosition()
        if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION
        return lastVisibleItemPosition - firstVisibleItemPosition
    }

    /**
     * [RecyclerView.LayoutManager] has different types of scroll mechanisms, this extension function uses the [LinearLayoutManager.scrollToPositionWithOffset] if its an instance of [LinearLayoutManager]
     * else uses the standard [RecyclerView.LayoutManager.scrollToPosition] method. The offset in [LinearLayoutManager] is the position where the view should be after scrolling relative to the [RecyclerView]
     * */
    private fun RecyclerView.safeScrollToPosition(position: Int){
        when(this.layoutManager){
            is LinearLayoutManager -> (this.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position,0)
            is RecyclerView.LayoutManager -> this.layoutManager.scrollToPosition(position)
        }
    }

    /**
     * Computes the position to make the [RecyclerView] to scroll to.
     *
     * **Calculation:** [relativeRawPos] is divided with difference of the scoll extent using [RecyclerView].compute<X>ScrollExtent, where X is be the direction
     * and the height of the handle and then this value is multiplied by the total item count, this give us the General position. But for [LinearLayoutManager]
     * the current visible items are also taken into consideration
     *
     *
     *@param relativeRawPos the relative raw position, calculated during [MotionEvent.ACTION_MOVE] or [MotionEvent.ACTION_DOWN]
     * */
    private fun RecyclerView.computePositionForOffsetAndScroll(relativeRawPos : Float) : Int {
        val layoutManager : RecyclerView.LayoutManager? = this.layoutManager
        val recyclerViewItemCount = this.adapter?.itemCount ?: 0
        val newOffset = relativeRawPos / ((this.computeVerticalScrollExtent().toFloat()) - handleImageView.height.toFloat())
        when(layoutManager){
            is LinearLayoutManager -> {
                val totalVisibleItems = layoutManager.getTotalCompletelyVisibleItemCount()

                if (totalVisibleItems == RecyclerView.NO_POSITION) return RecyclerView.NO_POSITION

                // the last item would have one less visible item, this is to offset it.
                previousTotalVisibleItem = Math.max(previousTotalVisibleItem, totalVisibleItems)
                //check bounds and then set position
                val position = Math.min(recyclerViewItemCount, Math.max(0, Math.round(newOffset * (recyclerViewItemCount - totalVisibleItems))))

                val toScrollPosition =  Math.min((this.adapter?.itemCount?:0)-(previousTotalVisibleItem+1), position)
                safeScrollToPosition(toScrollPosition)
                return position
            }
            else -> {

                val position = Math.round(newOffset * recyclerViewItemCount)
                safeScrollToPosition(position)
                return position
            }
        }
    }

    /**
     * Updates the text and checks if the interface is implemented or not
     * */
    private fun updateTextInPopup(position: Int){
        if (position !in 0 until (recyclerView.adapter?.itemCount?:1)) { return }

        val adapter = recyclerView.adapter
        when (adapter){
            is OnPopupTextUpdate -> popupTextView.text =adapter.onChange(position).toString()
            is OnPopupViewUpdate -> {adapter.onUpdate(position, popupTextView)}
            null -> {throw IllegalAccessException("No adapter found, if you have an adapter then try placing if before calling the attachFastScrollerToRecyclerView() method")}
            else -> { throw IllegalAccessException("Should implement the OnPopupTextUpdate or OnPopupViewUpdate interface")}
        }
    }

    private val emptySpaceItemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                super.getItemOffsets(outRect, view, parent, state)

                if (parent.getChildAdapterPosition(view) == parent.adapter.itemCount - 1) {
                    val currentVisiblePos: Int = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (currentVisiblePos != RecyclerView.NO_POSITION) {
                        outRect.bottom = (parent.findViewHolderForAdapterPosition(currentVisiblePos)?.itemView?.height
                                ?: 0)
                    }
                }
            }
        }
    }

    /**
     * adds emplty space to the last item of the [RecyclerView]
     **/
    private fun setEmptySpaceItemDecorator(){
        recyclerView.addItemDecoration(emptySpaceItemDecoration)
    }

    private val adapterDataObserver = lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                previousTotalVisibleItem = 0
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                previousTotalVisibleItem = 0
            }
        }
    }
    private fun registerDataObserver(){
        recyclerView.adapter?.registerAdapterDataObserver(adapterDataObserver.value)
    }
    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isEngaged && isFastScrollEnabled) return

            val computeVerticalScrollExtent: Float = recyclerView?.computeVerticalScrollExtent()?.toFloat()
                    ?: 0f
            val computeVerticalScrollRange : Float = recyclerView?.computeVerticalScrollRange()?.toFloat() ?: 1f

            // check if the layout is scrollable. i.e. range is large than extent, else disable fastscrolling and track touches.
            if (computeVerticalScrollExtent<computeVerticalScrollRange){
                handleImageView.animateVisibility()
                handleImageView.isEnabled = true
                trackView.isEnabled = true
            }else{
                handleImageView.animateVisibility(false)
                trackView.isEnabled = false
                handleImageView.isEnabled = false
                return
            }
            val offsetScale = ((recyclerView?.computeVerticalScrollOffset()?.toFloat() ?: 0f)) /
                    ((computeVerticalScrollRange) - (computeVerticalScrollExtent))
            val finalOffset = offsetScale * (computeVerticalScrollExtent - handleImageView.height.toFloat())
            moveViewByRelativeYInBounds(handleImageView, finalOffset)
            moveViewByRelativeYInBounds(popupTextView, finalOffset - popupTextView.height.toFloat())
        }
    }

    private fun initImpl(){
        if (hasEmptyItemDecorator){
            setEmptySpaceItemDecorator()
        }
        registerDataObserver()
        recyclerView.addOnScrollListener(onScrollListener)

//        todo@shahsurajk decide fate of this
        recyclerView.onFlingListener = object : RecyclerView.OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
//                log("onFling-> $velocityY ${recyclerView.minFlingVelocity} ${recyclerView.maxFlingVelocity}")
//                val recyclerView1 = recyclerView
//                recyclerView1?.smoothScrollBy(0, recyclerView1.computeVerticalScrollRange()- recyclerView1.computeVerticalScrollExtent())
                return false
            }
        }
    }
    /**
     * Sets a [HandleStateListener] to this fastscroll
     *
     * @since 1.0
     * @see HandleStateListener
     **/
    fun setHandleStateListener(handleStateListener: HandleStateListener){
        this.handleStateListener = handleStateListener
    }
    /**
     * ### Call this method only if [RecyclerView] is not a child to this view, else can cause memory leaks and undesired behavior
     *
     * To re-init the [RecyclerViewFastScroller] call the [detachFastScrollerFromRecyclerView] and then re-call this method.
     *
     * This method adds a [RecyclerView.OnScrollListener] to the [RecyclerView] and also attaches an itemDecorator which adds margin to the last item of the [RecyclerView.Adapter]
     * this bottom empty margin can be skipped if the [R.styleable.RecyclerViewFastScroller_addLastItemPadding] is set to false during layout creation, this will not add the [RecyclerView.ItemDecoration] as well.
     *
     * The [RecyclerView.OnScrollListener] is used to compute the postion of the Handle during runtime,thus calling [RecyclerView.clearOnScrollListeners] might cause this view to not function properly.
     *
     * A [RecyclerView.AdapterDataObserver] is also added to listen to data changes.
     *
     * @see detachFastScrollerFromRecyclerView
     * @since 1.0
     * */
    @Keep
    fun attachFastScrollerToRecyclerView(recyclerView: RecyclerView){
        this.recyclerView = requireNotNull(recyclerView) { ERROR_MESSAGE_NO_RECYCLER_VIEW }
        initImpl()
    }
    /**
     * This method should only be explicitly called if there's a need to reset the [RecyclerViewFastScroller] else if will be called by this view in the [onDetachedFromWindow] method.
     *
     * This method removes the [RecyclerView.OnScrollListener], the [RecyclerView.ItemDecoration] if set and the [RecyclerView.AdapterDataObserver] associated with it.
     *
     * @see attachFastScrollerToRecyclerView
     * @since 1.0
     **/
    fun detachFastScrollerFromRecyclerView(){
        // unregister the observer to prevent memory leaks only if initialized
        if (adapterDataObserver.isInitialized()){
            recyclerView.adapter?.unregisterAdapterDataObserver(adapterDataObserver.value)
        }
        handleImageView.setOnTouchListener(null)
        popupTextView.setOnTouchListener(null)
        recyclerView.removeOnScrollListener(onScrollListener)
//        todo@shahsurajk think on this
        recyclerView.onFlingListener = null
        if (hasEmptyItemDecorator){
            recyclerView.removeItemDecoration(emptySpaceItemDecoration)
        }
    }
    @Keep
    /**
     * Provides the [TextView] along with the current postion of the [RecyclerViewFastScroller]
     *
     * All the visual, position-based changes should be done using this interface
     *
     * For updating only the text check [OnPopupTextUpdate] interface
     *
     * Both these interfaces cannot be set and [OnPopupTextUpdate] has priority over [OnPopupViewUpdate]
     *
     * @see OnPopupTextUpdate
     * @since 1.0
     **/
    interface OnPopupViewUpdate{
        fun onUpdate(position: Int, popupTextView: TextView)
    }

    @Keep
    /**
     * A simpler callback to just provide the [CharSequence] to be set to the [popupTextView] based on the position.
     *
     * To perform visual changes on the [TextView] check the [OnPopupViewUpdate] interface
     *
     * Both these interfaces cannot be set and [OnPopupTextUpdate] has priority over [OnPopupViewUpdate]

     * @see OnPopupViewUpdate
     * @since 1.0
     **/
    interface OnPopupTextUpdate{
        fun onChange(position: Int) : CharSequence
    }
    @Keep
    /**
     * An interface to listen to different states of the handle in the fastscroller, all the methods in this are only called if [isFastScrollEnabled] is true
     *
     * @since 1.0
     * @see isFastScrollEnabled
     **/
    interface HandleStateListener{
        /**
         * Called when the handle is pressed and engaged for fastscroll behavior
         **/
        fun onEngaged(){}
        /**
         * Called when the handle is dragged to perform the fast scroll operation.
         *
         * @param offset The offset to which the handle is currently scroller to
         * @param postion The computed position which is sent to the [OnPopupViewUpdate] or the [OnPopupTextUpdate] callbacks
         *
         * @see OnPopupViewUpdate
         * @see OnPopupTextUpdate
         * */
        fun onDragged(offset: Float, postion: Int){}
        /**
         * Called when the handled is released, this marks the end of the fast scroll operation.
         * */
        fun onReleased(){}
    }
    private fun log(message : String = "" ){
        Log.d(TAG, message)
    }
}