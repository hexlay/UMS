package hexlay.ums.activites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.dbflow5.structure.insert
import com.google.android.material.textfield.TextInputEditText
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.helpers.PreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.layout_auth.*
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
            val dialogView = dialog.getCustomView()
            val email = dialogView.findViewById<TextInputEditText>(R.id.email)
            val password = dialogView.findViewById<TextInputEditText>(R.id.password)
            dialog.show {
                title(R.string.auth_title)
                noAutoDismiss()
                cancelable(false)
                cancelOnTouchOutside(false)
                positiveButton(R.string.auth_login) {
                    if (email.text!!.isEmpty() or password.text!!.isEmpty()) {
                        email_input.error = resources.getString(R.string.auth_empty)
                        password_input.error = resources.getString(R.string.auth_empty)
                    } else {
                        (application as UMS).umsAPI.login(email.text.toString(), password.text.toString()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe {
                            if (it != null) {
                                it.insert()
                                dialog.dismiss()
                                startMainActivity()
                            } else {
                                toast(R.string.auth_error)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

}