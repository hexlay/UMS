package hexlay.ums.activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import hexlay.ums.R
import hexlay.ums.adapters.ViewPagerAdapter
import hexlay.ums.fragments.*
import hexlay.ums.helpers.AppHelper
import hexlay.ums.helpers.PreferenceHelper
import hexlay.ums.helpers.setSize
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var appHelper: AppHelper
        private set
    lateinit var preferenceHelper: PreferenceHelper
        private set
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        appHelper = AppHelper(this)
        preferenceHelper = PreferenceHelper(baseContext)
        appHelper.makeFullscreen()
        initToolbar()
        setupNavigationView()
        applyDayNight()
    }


    fun applyDayNight() {
        when (preferenceHelper.darkMode) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        delegate.applyDayNight()
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ScoreFragment(), R.id.nav_scores)
        viewPagerAdapter.addFragment(CalendarFragment(), R.id.nav_calendar)
        viewPagerAdapter.addFragment(ExamFragment(), R.id.nav_exams)
        viewPagerAdapter.addFragment(NotificationFragment(), R.id.nav_notifications)
        viewPagerAdapter.addFragment(ProfileFragment(), R.id.nav_profile)
        view_pager.adapter = viewPagerAdapter
        view_pager.offscreenPageLimit = 4
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                navigation.selectedItemId = viewPagerAdapter.getFragmentId(position)
                toolbar_overlay.isVisible = position == 1
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setupNavigationView() {
        navigation.setOnNavigationItemSelectedListener { item ->
            view_pager.currentItem = viewPagerAdapter.getPositionById(item.itemId)
            true
        }
    }

    private fun initToolbar() {
        setupViewPager()
        toolbar_overlay.setSize(height = appHelper.statusBarHeight + appHelper.actionBarSize)
    }

    fun disableExams() {
        if (view_pager.currentItem == 2) {
            view_pager.currentItem = 0
        }
        viewPagerAdapter.removeFragment(2)
        navigation.menu.removeItem(R.id.nav_exams)
        view_pager.offscreenPageLimit = 3
    }

    fun exitMainActivity() {
        val intent = Intent(this, StarterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

}
