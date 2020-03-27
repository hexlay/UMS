package hexlay.ums.fragments.notifications

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
import hexlay.ums.api.Api
import hexlay.ums.helpers.getActionBarSize
import hexlay.ums.helpers.observe
import hexlay.ums.models.notifications.NotificationBase
import hexlay.ums.services.events.Event
import hexlay.ums.services.events.NotificationRemoveEvent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_notification_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class NotificationPageFragment : Fragment() {

    private lateinit var activity: MainActivity
    private var notificationAdapter: NotificationAdapter? = null
    private var notificationFragmentType: String? = null
    private var disposable: CompositeDisposable? = null

    private var page = 1
    private var maxPages = 1
    private var isWaiting = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        notificationFragmentType = arguments?.getString("notification_type")
        disposable = CompositeDisposable()
        activity = requireActivity() as MainActivity
        return inflater.inflate(R.layout.fragment_notification_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initNotifications()
        initEvents()
        notification_list_refresher.setOnRefreshListener {
            page = 1
            initNotifications()
        }
        if (notificationFragmentType == "unread") {
            notification_list_refresher.setProgressViewOffset(false, 0, getActionBarSize())
            notification_list.setPadding(0, getActionBarSize(), 0, notification_list.paddingBottom)
            receive_notification.isVisible = true
            receive_notification.isChecked = activity.preferenceHelper.getNotifications
            receive_notification.setOnCheckedChangeListener { _, isChecked ->
                activity.preferenceHelper.getNotifications = isChecked
                if (isChecked) {
                    activity.startNotificationJob()
                } else {
                    activity.stopJob(0x1)
                }
            }
        }
    }

    private fun initEvents() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 1)
        notificationAdapter = NotificationAdapter(requireContext())
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

    private fun initNotifications() {
        disposable?.add(Api.make(requireContext()).getNotifications(page = page.toString(), state = notificationFragmentType).observe {
            handleData(it)
            notification_list_refresher.isRefreshing = false
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
                notificationAdapter?.clearlyAddNotifications(data.notifications)
            } else {
                notifications_none.isVisible = true
            }
        } else {
            notificationAdapter?.addNotifications(data.notifications)
        }
        page++
    }

    @Subscribe
    fun onEvent(event: Event) {
        when (event) {
            is NotificationRemoveEvent -> {
                notificationAdapter?.removeNotification(event.notification)
            }
        }
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        disposable?.dispose()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(notificationType: String) = NotificationPageFragment().apply {
            arguments = Bundle(1).apply {
                putString("notification_type", notificationType)
            }
        }
    }


}