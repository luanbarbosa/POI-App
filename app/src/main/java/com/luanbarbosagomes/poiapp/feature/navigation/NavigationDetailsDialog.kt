package com.luanbarbosagomes.poiapp.feature.navigation

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.luanbarbosagomes.poiapp.R
import com.luanbarbosagomes.poiapp.feature.navigation.NavigationStepsAdapter.NavigationStepHolder
import com.luanbarbosagomes.poiapp.provider.navigation.DirectionsResponse.*
import com.luanbarbosagomes.poiapp.provider.poi.Poi
import kotlinx.android.synthetic.main.bottom_sheet_details_navigation.*

class NavigationDetailsDialog(
    context: Context,
    private val poi: Poi,
    private val route: Routes
) : BottomSheetDialog(context, R.style.BottomSheetStyle) {

    override fun show() {
        val view = View.inflate(context, R.layout.bottom_sheet_details_navigation, null)
        setContentView(view)
        BottomSheetBehavior.from(view.parent as View).peekHeight = 800
        setupUi()
        super.show()
    }

    private fun setupUi() {
        titleTv.text = poi.title
        stepList.apply {
            adapter = NavigationStepsAdapter(route.legs.first().steps.toMutableList())
            layoutManager = LinearLayoutManager(context)
        }
    }
}

class NavigationStepsAdapter(
    private val steps: MutableList<Step>
) : RecyclerView.Adapter<NavigationStepHolder>() {

    init {
        addInitialFinalSteps()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationStepHolder =
        NavigationStepHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.navigation_list_item,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = steps.size

    override fun onBindViewHolder(holder: NavigationStepHolder, position: Int) =
        holder.bind(step = steps[position])

    private fun addInitialFinalSteps() {
        steps.add(0, Initial())
        steps.add(Final())
    }

    class NavigationStepHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {

        private val context: Context = rootView.context
        private var instructionTv: TextView = rootView.findViewById(R.id.instructionTv)
        private var distanceTv: TextView = rootView.findViewById(R.id.distanceTv)
        private var topIndicator: View = rootView.findViewById(R.id.topIndicator)
        private var bottomIndicator: View = rootView.findViewById(R.id.bottomIndicator)
        private var icon: ImageView = rootView.findViewById(R.id.icon)

        fun bind(step: Step) {
            instructionTv.parseHtml(step.htmlInstructions)
            distanceTv.text = step.distance.text

            when (step) {
                is Initial -> {
                    topIndicator.visibility = View.INVISIBLE
                    icon.setImageDrawable(context.getDrawable(R.drawable.ic_directions_origin))
                }
                is Final -> {
                    bottomIndicator.visibility = View.INVISIBLE
                    icon.setImageDrawable(context.getDrawable(R.drawable.ic_directions_destination))
                }
                else -> {
                    topIndicator.visibility = View.VISIBLE
                    bottomIndicator.visibility = View.VISIBLE
                    icon.setImageDrawable(context.getDrawable(R.drawable.ic_directions_walk))
                }
            }
        }
    }
}

class Initial : Step(htmlInstructions = "Your location")
class Final : Step(htmlInstructions = "Destination")

private fun TextView.parseHtml(htmlText: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
    else
        Html.fromHtml(htmlText)
}
