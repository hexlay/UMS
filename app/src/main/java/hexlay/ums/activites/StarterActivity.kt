package hexlay.ums.activites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.dbflow5.structure.insert
import hexlay.ums.R
import hexlay.ums.api.Api
import hexlay.ums.helpers.PreferenceHelper
import hexlay.ums.helpers.md5
import hexlay.ums.helpers.observe
import hexlay.ums.services.events.Event
import hexlay.ums.services.events.SubscriptionError
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_auth.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.noAnimation
import org.jetbrains.anko.toast

class StarterActivity : AppCompatActivity() {

    private lateinit var preferenceHelper: PreferenceHelper
    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        preferenceHelper = PreferenceHelper(this)
        disposable = CompositeDisposable()
        checkLoggedIn()
    }

    private fun checkLoggedIn() {
        if (preferenceHelper.connectId != "") {
            startMainActivity()
        } else {
            val dialog = MaterialDialog(this).customView(R.layout.layout_auth)
            dialog.show {
                title(R.string.auth_title)
                noAutoDismiss()
                cancelable(false)
                cancelOnTouchOutside(false)
                positiveButton(R.string.auth_login) {
                    val emailText = email.text.toString()
                    val passwordText = password.text.toString()
                    if (emailText.isEmpty() or passwordText.isEmpty()) {
                        email_input.error = resources.getString(R.string.auth_empty)
                        password_input.error = resources.getString(R.string.auth_empty)
                    } else {
                        auth_loading.isVisible = true
                        disposable?.add(Api.make(context).login(emailText, passwordText).observe {
                            it.insert()
                            preferenceHelper.passwordHash = passwordText.md5()
                            dialog.dismiss()
                            startMainActivity()
                            auth_loading.isVisible = false
                        })
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        val activityIntent = intentFor<MainActivity>().noAnimation()
        if (intent.extras != null && !intent.extras!!.isEmpty) {
            activityIntent.putExtras(intent.extras!!)
        }
        startActivity(activityIntent)
        finish()
    }

    @Subscribe
    fun onEvent(event: Event) {
        when (event) {
            is SubscriptionError -> {
                toast(event.throwable.message!!)
                auth_loading.isVisible = false
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        disposable?.dispose()
        super.onDestroy()
    }

}