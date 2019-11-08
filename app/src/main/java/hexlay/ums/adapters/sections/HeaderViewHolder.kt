package hexlay.ums.adapters.sections

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var sectionName: TextView = itemView.findViewById(R.id.section_title)

}