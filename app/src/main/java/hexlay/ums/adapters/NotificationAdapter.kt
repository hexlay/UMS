package hexlay.ums.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.helpers.toHtml
import hexlay.ums.models.notifications.Notification
import hexlay.ums.models.notifications.NotificationData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.extensions.LayoutContainer
import java.lang.ref.WeakReference

class NotificationAdapter(val application: UMS) : RecyclerView.Adapter<NotificationAdapter.RViewHolder>() {

    private var reference: WeakReference<UMS> = WeakReference(application)
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
                    reference.get()!!.umsAPI.markNotification(id = notification.id).observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
                        notifications.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                    }, { throwable ->
                        reference.get()!!.handleError(throwable)
                    })
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

}