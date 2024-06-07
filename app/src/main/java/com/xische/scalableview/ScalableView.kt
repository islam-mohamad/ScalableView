package com.xische.scalableview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.xische.scalableview.adapter.ItemsAdapter
import com.xische.scalableview.databinding.XischeBottomsheetBinding
import com.xische.scalableview.utils.StackLayoutManagerDep

class ScalableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var _topView: View? = null
    private var lm: StackLayoutManagerDep? = null
    private var latBottomSheetState = STATE_HALF_EXPANDED

    private var binding: XischeBottomsheetBinding? =
        XischeBottomsheetBinding.inflate(LayoutInflater.from(context), this, true)


    private val bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? by lazy {
        binding?.bottomSheet?.let { BottomSheetBehavior.from(it) }
    }

    init {
        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    lm?.setLayoutBehaviour(isCollapse = latBottomSheetState == STATE_COLLAPSED)
                } else if (newState == STATE_EXPANDED || newState == STATE_COLLAPSED) {
                    latBottomSheetState = newState
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val params = binding?.rv?.layoutParams
                params?.height =
                    (binding?.parent?.height ?: 0) - (bottomSheet.height * slideOffset).toInt()
                binding?.rv?.layoutParams = params
            }

        })

    }

    fun addContent(content: View) = binding?.bottomSheet?.run {
        addView(content)
    }

    fun setAdapter(adapter: ItemsAdapter) = binding?.rv?.let {
        lm = StackLayoutManagerDep(this.context, it)
        it.adapter = adapter
        it.layoutManager = lm
        it.layoutParams.width = LayoutParams.MATCH_PARENT
        _topView = it
        setupBottomSheetInitialState(it)
    }

    private fun setupBottomSheetInitialState(topView: View) = binding?.run {
        topView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ((parent.measuredHeight - topView.measuredHeight).toFloat() / parent.measuredHeight).also { expandRatio ->
                    bottomSheetBehavior?.halfExpandedRatio =
                        if (expandRatio <= 0F || expandRatio >= 1) 0.1F else expandRatio
                }
                topView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                bottomSheetBehavior?.state = STATE_HALF_EXPANDED
            }
        })
    }

    fun addContent(content: Int) = binding?.bottomSheet?.run {
        addView(LayoutInflater.from(context).inflate(content, null))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding = null
    }

}