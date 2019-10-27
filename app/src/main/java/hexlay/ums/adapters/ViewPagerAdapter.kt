package hexlay.ums.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hexlay.ums.fragments.notifications.NotificationPageFragment

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when {
            fragmentList[position] is NotificationPageFragment -> {
                val fragment = (fragmentList[position] as NotificationPageFragment)
                if (fragment.arguments!!.getString("notification_type") == "read") {
                    "წაკითხული"
                } else {
                    "წაუკითხავი"
                }
            }
            else -> super.getPageTitle(position)
        }
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

}