package com.xische.scalableview.utils


import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min


/**
 *  @StackLayoutManagerDep is the working solution of stacking
 *  in collapsed and expand states
 *  */
class StackLayoutManagerDep(context: Context, private val recyclerView: RecyclerView?) :
    LinearLayoutManager(context) {

    private var isCollapsed = true
    private var center = 0
    private var recyclerViewState = -1

    /**
     *  @onLayoutChildren triggers initially,
     *  @isCollapsed determines the focal point for the stacking,
     *  either first or last visible item on the screen
     *  */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (isCollapsed) {
            val child = getChildAt(0)
            center = ((child?.top?:0) + (child?.bottom?:0) + 50) / 2
        } else {
            for (i in 0 until childCount) {
                val child = getChildAt(i) ?: continue
                if (getDecoratedBottom(child) <= height) {
                    center = child.top - 50
                }
            }
        }

        layoutBehaviour()
    }

    /**
     *  triggers on scrolling
     *  */
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val scrolled = super.scrollVerticallyBy(dy, recycler, state)
        layoutBehaviour()
        return scrolled
    }

    /**
     * set the value of variable:
     * @isCollapsed to true to
     * disable scrolling in the collapsed state
     *  */
    override fun canScrollVertically(): Boolean {
        return !isCollapsed
    }

    fun isCollapsedLayout() = isCollapsed

    /**
     *  update the boolean
     *  @isCollapsed and request the layout to regenerate,
     *  to switch between collapse and expand states
     *  */
    fun setLayoutBehaviour(isCollapse: Boolean) {
        isCollapsed = isCollapse
        requestLayout()
    }

    /**
     *  based on
     *  @variable isCollapsed, call the functions
     *  */
    private fun layoutBehaviour() {
        if (isCollapsed) adjustLayoutCollapsed()
        else adjustLayoutExpand()
    }

    /**
     *  @function for collapse state
     *  */
    private fun adjustLayoutCollapsed() {
        /**
         * stop the further execution if
         * @childCount or
         * @itemCount is 0
         * */
        if (childCount == 0 || itemCount == 0) return
        /**
         * @scaledItemsCount to make stack of a specific number items
         */
        var scaledItemsCount = 0

        /**
         * @fixedTranslationY for the fixed value of translationY for the stack effect
         */
        val fixedTranslationY = 185f // TODO need to be percentage

        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val viewCenterY = (view.top + view.bottom) / 2.0f

            val chCount = if(recyclerViewState == RecyclerView.SCROLL_STATE_IDLE) 3 else childCount
            if (viewCenterY >= center && scaledItemsCount < chCount) {
                val distanceFromCenter = abs(center - viewCenterY)
                val scaleFactor =  min(1.0f, 1.0f - distanceFromCenter / (center * 35f))

                view.scaleX = scaleFactor
                view.scaleY = scaleFactor

                view.translationY = -fixedTranslationY * (scaledItemsCount + 1)
                view.translationZ = -(distanceFromCenter / 2.0f)
                view.alpha = 1.0f

                scaledItemsCount++

            } else {
                view.scaleX = 1f
                view.scaleY = 1f
                view.translationY = 0f
                view.translationZ = 0f
                view.alpha = if (scaledItemsCount >= 3) 0.0f else 1.0f
            }

        }

        val params = recyclerView?.layoutParams
        params?.height = (getChildAt(findLastCompletelyVisibleItemPosition())?.y?.toInt()?:0)
        recyclerView?.layoutParams = params
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        recyclerViewState = state
    }

    /**
     *  @function for expand state
     *  */
    private fun adjustLayoutExpand() {
        if (childCount == 0 || itemCount == 0) return

        var scaledItemsCount = 0

        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            val viewCenterY = (view.top + view.bottom) / 2.0f
            val chCount = if(recyclerViewState == RecyclerView.SCROLL_STATE_IDLE) childCount else 3
            if (viewCenterY >= center && scaledItemsCount < chCount) {
                val distanceFromCenter = abs(center - viewCenterY)
                val scaleFactor = min(1.0f, 1.0f - distanceFromCenter / (center * 3.5f))

                view.scaleX = scaleFactor
                view.scaleY = scaleFactor

                // Apply translationY to create stack view effect based on centerY ratio
                view.translationY = -(distanceFromCenter / 1.15f)
                view.translationZ = -(distanceFromCenter / 2.0f)
                view.alpha = 1.0f

                scaledItemsCount++
            } else {
                view.scaleX = 1f
                view.scaleY = 1f
                view.translationY = 0f
                view.translationZ = 0f
                view.alpha = if (scaledItemsCount >= 3) 0.0f else 1.0f
            }
        }
    }

    companion object {
        const val TAG = "SLM"
    }
}