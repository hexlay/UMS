package hexlay.ums.activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import hexlay.ums.R
import hexlay.ums.adapters.ViewPagerAdapter
import hexlay.ums.fragments.CalendarFragment
import hexlay.ums.fragments.ProfileFragment
import hexlay.ums.fragments.ScoreFragment
import hexlay.ums.helpers.AppHelper
import hexlay.ums.helpers.setSize
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var appHelper: AppHelper
        private set
    private lateinit var scoreFragment: ScoreFragment
    private lateinit var calendarFragment: CalendarFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        appHelper = AppHelper(this)
        initToolbar()
        setupNavigationView()
        appHelper.makeFullscreen()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(scoreFragment)
        adapter.addFragment(calendarFragment)
        adapter.addFragment(profileFragment)
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 3
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        navigation.selectedItemId = R.id.nav_scores
                        toolbar_overlay.isVisible = false
                    }
                    1 -> {
                        navigation.selectedItemId = R.id.nav_calendar
                        toolbar_overlay.isVisible = true
                    }
                    2 -> {
                        navigation.selectedItemId = R.id.nav_subjects
                        toolbar_overlay.isVisible = false
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun setupNavigationView() {
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_scores -> view_pager.currentItem = 0
                R.id.nav_calendar -> view_pager.currentItem = 1
                R.id.nav_subjects -> view_pager.currentItem = 2
            }
            true
        }
    }

    private fun initToolbar() {
        scoreFragment = ScoreFragment()
        calendarFragment = CalendarFragment()
        profileFragment = ProfileFragment()
        setupViewPager()
        toolbar_overlay.setSize(height = appHelper.statusBarHeight + appHelper.actionBarSize)
    }

    fun exitMainActivity() {
        val intent = Intent(this, StarterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

}
