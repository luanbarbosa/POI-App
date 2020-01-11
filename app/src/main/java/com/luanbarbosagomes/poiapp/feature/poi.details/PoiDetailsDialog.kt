package com.luanbarbosagomes.poiapp.feature.poi.details

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.dagger.DaggerMainComponent
import com.luanbarbosagomes.poiapp.provider.poi.Poi
import com.luanbarbosagomes.poiapp.provider.poi.PoiViewModel
import kotlinx.android.synthetic.main.bottom_sheet_poi_details.*
import javax.inject.Inject

class PoiDetailsDialog(
    context: Context,
    private val poi: Poi
) : BottomSheetDialog(context, R.style.BottomSheetStyle) {

    init {
        DaggerMainComponent.create().inject(this)
    }

    @Inject
    lateinit var poiViewModel: PoiViewModel

    override fun show() {
        val view = View.inflate(context, R.layout.bottom_sheet_poi_details, null)
        setContentView(view)
        BottomSheetBehavior.from(view.parent as View).peekHeight = 300
        loadPoiInfo()
        super.show()
    }

    private fun loadPoiInfo() {
        titleTv.text = poi.title
    }
}