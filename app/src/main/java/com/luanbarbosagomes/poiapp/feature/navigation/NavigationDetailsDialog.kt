package com.luanbarbosagomes.poiapp.feature.navigation

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.luanbarbosagomes.poiapp.App
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.feature.navigation.ActivityNavigation
import com.luanbarbosagomes.poiapp.provider.location.LocationViewModel
import com.luanbarbosagomes.poiapp.provider.poi.Poi
import com.luanbarbosagomes.poiapp.provider.poi.PoiViewModel
import com.luanbarbosagomes.poiapp.utils.BrowserUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.bottom_sheet_details_poi.*
import javax.inject.Inject

class NavigationDetailsDialog(
    context: Context
) : BottomSheetDialog(context, R.style.BottomSheetStyle) {

    override fun show() {
        val view = View.inflate(context, R.layout.bottom_sheet_details_navigation, null)
        setContentView(view)
        BottomSheetBehavior.from(view.parent as View).peekHeight = 800
        super.show()
    }

}
