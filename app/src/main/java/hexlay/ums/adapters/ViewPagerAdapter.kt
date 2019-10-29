package hexlay.ums.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import hexlay.ums.fragments.notifications.NotificationPageFragment

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList = ArrayList<Fragment>()
    private val fragmentIdList = ArrayList<Int>()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        if (fragmentList.contains(`object`)) {
            return fragmentList.indexOf(`object`)
        }
        return PagerAdapter.POSITION_NONE
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

    fun getFragmentId(position: Int): Int {
        return fragmentIdList[position]
    }

    fun getPositionById(id: Int): Int {
        return fragmentIdList.indexOf(id)
    }

    fun addFragment(fragment: Fragment, id: Int) {
        fragmentList.add(fragment)
        fragmentIdList.add(id)
    }

    fun removeFragment(index: Int) {
        fragmentList.removeAt(index)
        fragmentIdList.removeAt(index)
        notifyDataSetChanged()
    }

}