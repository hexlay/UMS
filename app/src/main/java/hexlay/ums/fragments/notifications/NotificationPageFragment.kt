package hexlay.ums.fragments.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.activites.MainActivity
import hexlay.ums.adapters.NotificationAdapter
import hexlay.ums.models.notifications.NotificationBase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_notification_list.*
import java.lang.ref.WeakReference

class NotificationPageFragment : Fragment() {

    private lateinit var reference: WeakReference<MainActivity>
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationFragmentType: String
    private var page = 1
    private var maxPages = 1
    private var isWaiting = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationFragmentType = arguments!!.getString("notification_type")!!
        return inflater.inflate(R.layout.fragment_notification_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        initRecyclerView()
        initNotifications()
        notification_list_refresher.setOnRefreshListener {
            page = 1
            initNotifications()
        }
        if (notificationFragmentType == "unread") {
            notification_list.setPadding(0, reference.get()!!.appHelper.actionBarSize, 0, notification_list.paddingBottom)
            receive_notification.isVisible = true
            receive_notification.isChecked = reference.get()!!.preferenceHelper.getNotifications
            receive_notification.setOnCheckedChangeListener { _, isChecked ->
                reference.get()!!.preferenceHelper.getNotifications = isChecked
                if (isChecked) {
                    reference.get()!!.startSync()
                } else {
                    reference.get()!!.stopSync()
                }
            }
        }
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 1)
        notificationAdapter = NotificationAdapter(reference.get()!!.application as UMS)
        notification_list.layoutManager = layoutManager
        notification_list.adapter = notificationAdapter
        notification_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var pastVisibleItems = 0
            var visibleItemCount = 0
            var totalItemCount = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                    if (isWaiting) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount - 10) {
                            isWaiting = false
                            if (page <= maxPages) {
                                initNotifications()
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun initNotifications() {
        (reference.get()!!.application as UMS).umsAPI.getNotifications(page = page.toString(), state = notificationFragmentType).observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it != null) {
                handleData(it)
            }
            notification_list_refresher.isRefreshing = false
        }, {
            (reference.get()!!.application as UMS).handleError(it)
        })
    }

    private fun handleData(data: NotificationBase) {
        isWaiting = true
        maxPages = data.maxPages
        page = data.currentPage
        if (notifications_none.isVisible) {
            notifications_none.isVisible = false
        }
        if (page == 1) {
            if (data.notifications.isNotEmpty()) {
                notificationAdapter.clearlyAddNotifications(data.notifications)
            } else {
                notifications_none.isVisible = true
            }
        } else {
            notificationAdapter.addNotifications(data.notifications)
        }
        page++
    }

    companion object {
        fun newInstance(notificationType: String) = NotificationPageFragment().apply {
            arguments = Bundle(1).apply {
                putString("notification_type", notificationType)
            }
        }
    }


}