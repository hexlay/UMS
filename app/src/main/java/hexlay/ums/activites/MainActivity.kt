package hexlay.ums.activites

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
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
import hexlay.ums.services.ConnectivityReceiver
import hexlay.ums.services.NotificationService
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    lateinit var appHelper: AppHelper
        private set
    lateinit var preferenceHelper: PreferenceHelper
        private set
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var connectivityReceiver: ConnectivityReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        appHelper = AppHelper(baseContext)
        preferenceHelper = PreferenceHelper(baseContext)
        makeFullscreen()
        registerConReceiver()
        initToolbar()
        setupNavigationView()
        applyDayNight()
        startSync()
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
        val intent = intentFor<StarterActivity>()
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    fun startSync() {
        if (preferenceHelper.getNotifications) {
            if (!appHelper.isSyncing) {
                val jobService = ComponentName(this, NotificationService::class.java)
                val syncInfo = JobInfo.Builder(0x1, jobService)
                    .setPeriodic(14400000)
                    .setPersisted(true)
                    .build()
                val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                scheduler.schedule(syncInfo)
            }
        }
    }

    fun stopSync() {
        if (appHelper.isSyncing) {
            val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.cancel(0x1)
        }
    }

    private fun registerConReceiver() {
        connectivityReceiver = ConnectivityReceiver()
        val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        filter.addAction("android.net.wifi.STATE_CHANGE")
        registerReceiver(connectivityReceiver, filter)
    }

    private fun makeFullscreen() {
        val decorView = window.decorView
        var flags = decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        decorView.systemUiVisibility = flags
    }

    override fun onDestroy() {
        unregisterReceiver(connectivityReceiver)
        super.onDestroy()
    }

}
