package hexlay.ums.activites

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import hexlay.ums.R
import hexlay.ums.adapters.ViewPagerAdapter
import hexlay.ums.api.AccessDeniedException
import hexlay.ums.api.Api
import hexlay.ums.api.UnauthorizedException
import hexlay.ums.fragments.*
import hexlay.ums.helpers.*
import hexlay.ums.models.notifications.Notification
import hexlay.ums.services.ConnectivityReceiver
import hexlay.ums.services.NotificationService
import hexlay.ums.services.ScoreService
import hexlay.ums.services.events.Event
import hexlay.ums.services.events.LogoutEvent
import hexlay.ums.services.events.NotificationRemoveEvent
import hexlay.ums.services.events.SubscriptionError
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.noAnimation
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    lateinit var preferenceHelper: PreferenceHelper
        private set
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var connectivityReceiver: ConnectivityReceiver? = null
    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disposable = CompositeDisposable()
        preferenceHelper = PreferenceHelper(baseContext)
        init()
    }

    private fun init() {
        makeFullscreen()
        registerConReceiver()
        initToolbar()
        setupNavigationView()
        applyDayNight()
        startNotificationJob()
        startScoreJob()
        checkForStarterData()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
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
        viewPagerAdapter.addFragment(NotificationFragment(), R.id.nav_notifications)
        viewPagerAdapter.addFragment(ProfileFragment(), R.id.nav_profile)
        disposable?.add(Api.make(baseContext).getStudentExams().observe {
            if (it.isNotEmpty()) {
                viewPagerAdapter.addFragment(ExamFragment(), R.id.nav_exams)
                navigation.menu.findItem(R.id.nav_exams).isVisible = true
                view_pager.offscreenPageLimit = 4
            }
        })
        view_pager.adapter = viewPagerAdapter
        view_pager.offscreenPageLimit = 3
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
        toolbar_overlay.setSize(height = getStatusBarHeight() + getActionBarSize())
    }

    private fun exitMainActivity() {
        startActivity(intentFor<StarterActivity>().noAnimation())
        finish()
    }

    private fun checkForStarterData() {
        if (intent.extras != null && !intent.extras!!.isEmpty) {
            if (intent.hasExtra("notification")) {
                val notification = intent.getParcelableExtra("notification") as Notification
                val dialog = MaterialDialog(this, BottomSheet())
                dialog.show {
                    title(text = notification.data.title)
                    message(text = notification.data.text.toHtml())
                }
                if (notification.state == "unread") {
                    disposable?.add(Api.make(this).markNotification(id = notification.id).observe {
                        EventBus.getDefault().post(NotificationRemoveEvent(notification))
                    })
                }
            }
        }
    }

    fun startNotificationJob() {
        if (preferenceHelper.getNotifications) {
            if (!isJobScheduled(0x1)) {
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

    fun startScoreJob() {
        if (preferenceHelper.getNotificationsScore) {
            if (!isJobScheduled(0x2)) {
                val jobService = ComponentName(this, ScoreService::class.java)
                val syncInfo = JobInfo.Builder(0x2, jobService)
                    .setPeriodic(14400000)
                    .setPersisted(true)
                    .build()
                val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                scheduler.schedule(syncInfo)
            }
        }
    }

    fun stopJob(jobId: Int) {
        if (!isJobScheduled(jobId)) {
            val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.cancel(jobId)
        }
    }

    private fun isJobScheduled(jobId: Int): Boolean {
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        if (Constants.isNougat) {
            return scheduler.getPendingJob(jobId) != null
        } else {
            for (jobInfo in scheduler.allPendingJobs) {
                if (jobInfo.id == jobId) {
                    return true
                }
            }
            return false
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

    @Subscribe
    fun onEvent(event: Event) {
        when (event) {
            is LogoutEvent -> {
                exitMainActivity()
            }
            is SubscriptionError -> {
                if (event.throwable is AccessDeniedException || event.throwable is UnauthorizedException) {
                    preferenceHelper.clearForLogout()
                    EventBus.getDefault().post(LogoutEvent())
                }
                toast(event.throwable.message!!)
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        unregisterReceiver(connectivityReceiver)
        disposable?.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (view_pager.currentItem != 0) {
            view_pager.currentItem = 0
        } else {
            super.onBackPressed()
        }
    }

}
