package hexlay.ums.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import hexlay.ums.R
import hexlay.ums.api.Api
import hexlay.ums.helpers.observe
import hexlay.ums.helpers.toHtml
import hexlay.ums.models.notifications.Notification
import hexlay.ums.models.notifications.NotificationData
import kotlinx.android.extensions.LayoutContainer

class NotificationAdapter(private val context: Context) : RecyclerView.Adapter<NotificationAdapter.RViewHolder>() {

    private val notifications = ArrayList<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_view, parent, false)
        return RViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RViewHolder, position: Int) {
        viewHolder.bind(notifications[position].data)
    }

    override fun getItemCount(): Int = notifications.size

    inner class RViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val notificationName: TextView = containerView.findViewById(R.id.notification_name)
        private val notificationText: TextView = containerView.findViewById(R.id.notification_text)
        private val notificationHolder: LinearLayout = containerView.findViewById(R.id.notification_holder)

        init {
            notificationHolder.setOnClickListener {
                val notification = notifications[adapterPosition]
                val dialog = MaterialDialog(it.context, BottomSheet())
                dialog.show {
                    title(text = notification.data.title)
                    message(text = notification.data.text.toHtml())
                }
                if (notification.state == "unread") {
                    Api.make(context).markNotification(id = notification.id).observe {
                        notifications.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                    }
                }
            }
        }

        fun bind(notification: NotificationData) {
            notificationName.text = notification.title
            notificationName.isSelected = true
            notificationText.text = notification.text.toHtml()
        }

    }

    fun clearlyAddNotifications(data: List<Notification>) {
        notifications.clear()
        notifications.addAll(data)
        notifyDataSetChanged()
    }

    fun addNotifications(data: List<Notification>) {
        notifications.addAll(data)
        notifyDataSetChanged()
    }

    fun removeNotification(notification: Notification) {
        val position = notifications.indexOf(notification)
        notifications.removeAt(position)
        notifyItemRemoved(position)
    }

}