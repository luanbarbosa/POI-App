package com.luanbarbosagomes.poiapp.feature.poi.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.dagger.DaggerMainComponent
import com.luanbarbosagomes.poiapp.provider.poi.Poi
import com.luanbarbosagomes.poiapp.provider.poi.PoiViewModel
import com.luanbarbosagomes.poiapp.utils.BrowserUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
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

    private val disposeBag = CompositeDisposable()

    override fun dismiss() {
        disposeBag.clear()
        super.dismiss()
    }

    override fun show() {
        val view = View.inflate(context, R.layout.bottom_sheet_poi_details, null)
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
            .subscribe { poiDetails ->
                poiDetails?.let { updateUi(it, fullLoad = true) }
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
            routeBtn.setOnClickListener { }
        }
    }

    private fun showHiddenViews() {
        photoList.visibility = View.VISIBLE
        wikipediaBtn.visibility = View.VISIBLE
        routeBtn.visibility = View.VISIBLE
    }

    private fun loadImages(urls: List<String>) {
        photoList.apply {
            adapter = PoiImagesAdapter(urls)
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }
}

class PoiImagesAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<PoiImagesAdapter.PoiImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiImageHolder =
        PoiImageHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.poi_details_image_list_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: PoiImageHolder, position: Int) =
        holder.bind(url = images[position])

    class PoiImageHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {

        private var imageView: ImageView = rootView.findViewById(R.id.poiImage)

        fun bind(url: String) {
            Glide
                .with(imageView.context)
                .load(url)
                .centerCrop()
                .into(imageView)
        }
    }
}
