package hexlay.ums.activites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.dbflow5.structure.insert
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.helpers.PreferenceHelper
import hexlay.ums.helpers.md5
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.layout_auth.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.noAnimation
import org.jetbrains.anko.toast

class StarterActivity : AppCompatActivity() {

    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(this)
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
                        (application as UMS).umsAPI.login(emailText, passwordText).observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
                            if (it != null) {
                                it.insert()
                                preferenceHelper.passwordHash = passwordText.md5()
                                dialog.dismiss()
                                startMainActivity()
                            } else {
                                toast(R.string.auth_error)
                            }
                            auth_loading.isVisible = false
                        }, {
                            auth_loading.isVisible = false
                            (application as UMS).handleError(it)
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

}