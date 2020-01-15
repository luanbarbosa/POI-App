package com.luanbarbosagomes.poiapp.feature.main.details

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
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

class PoiDetailsDialog(
    context: Context,
    private val poi: Poi
) : BottomSheetDialog(context, R.style.BottomSheetStyle) {

    init {
        App.daggerMainComponent.inject(this)
    }

    @Inject
    lateinit var poiViewModel: PoiViewModel

    @Inject
    lateinit var locationViewModel: LocationViewModel

    private val disposeBag = CompositeDisposable()

    override fun dismiss() {
        disposeBag.clear()
        super.dismiss()
    }

    override fun show() {
        val view = View.inflate(context, R.layout.bottom_sheet_details_poi, null)
        setContentView(view)
        BottomSheetBehavior.from(view.parent as View).peekHeight = 600

        contentLoadingIndicator.show()
        loadPoiInfo()
        super.show()
    }

    private fun loadPoiInfo() {
        updateUi(poi, fullLoad = false)
        poiViewModel
            .poiDetailsObservable(poi)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { poiDetails, error ->
                when (error) {
                    null -> updateUi(poiDetails, fullLoad = true)
                    else -> {
                        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
            .addTo(disposeBag)

    }

    private fun updateUi(poi: Poi, fullLoad: Boolean) {
        if (fullLoad) {
            poi.imageUrls?.let { loadImages(it) }
            contentLoadingIndicator.hide()
            showHiddenViews()
        }

        with(poi) {
            titleTv.text = title
            descriptionTv.text = description
            wikipediaBtn.setOnClickListener { BrowserUtils.openOnTab(context, wikipediaUrl) }
            routeBtn.setOnClickListener { showNavigationScreen() }
        }
    }

    private fun showHiddenViews() {
        photoList.visibility = View.VISIBLE
        wikipediaBtn.visibility = View.VISIBLE
        routeBtn.visibility = View.VISIBLE
    }

    private fun loadImages(urls: List<String>) {
        photoList.apply {
            adapter =
                PoiImagesAdapter(
                    urls
                ) { clickedImg ->
                    BrowserUtils.openOnTab(context, clickedImg)
                }
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }

    private fun showNavigationScreen() {
        context.startActivity(
            Intent(context, ActivityNavigation::class.java).apply {
                putExtra(
                    ActivityNavigation.CURRENT_LOCATION,
                    locationViewModel.lastLocation
                )
                putExtra(
                    ActivityNavigation.POI,
                    Gson().toJson(poi)
                )
            }
        )
    }
}

class PoiImagesAdapter(
    private val images: List<String>,
    private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<PoiImagesAdapter.PoiImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiImageHolder =
        PoiImageHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.poi_details_image_list_item,
                parent,
                false
            ),
            clickListener
        )

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: PoiImageHolder, position: Int) =
        holder.bind(url = images[position])

    class PoiImageHolder(
        rootView: View,
        val clickListener: (String) -> Unit
    ) : RecyclerView.ViewHolder(rootView) {

        private var imageView: ImageView = rootView.findViewById(R.id.poiImage)

        fun bind(url: String) {
            Glide
                .with(imageView.context)
                .load(url)
                .centerCrop()
                .into(imageView)
            imageView.setOnClickListener { clickListener(url) }
        }
    }
}
