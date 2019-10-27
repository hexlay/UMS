package hexlay.ums.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hexlay.ums.R
import hexlay.ums.activites.MainActivity
import hexlay.ums.adapters.ViewPagerAdapter
import hexlay.ums.fragments.notifications.NotificationPageFragment
import hexlay.ums.helpers.setMargins
import kotlinx.android.synthetic.main.fragment_notification.*
import java.lang.ref.WeakReference

class NotificationFragment : Fragment() {

    private lateinit var reference: WeakReference<MainActivity>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        notification_tabs.setMargins(top = reference.get()!!.appHelper.statusBarHeight)
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(NotificationPageFragment.newInstance("unread"))
        adapter.addFragment(NotificationPageFragment.newInstance("read"))
        notification_pager.adapter = adapter
        notification_pager.offscreenPageLimit = 2
        notification_tabs.setupWithViewPager(notification_pager)
    }

}