package com.zdroba.multipitchbuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        val authService = app.authService

        val emailLayout = view.findViewById<TextInputLayout>(R.id.email_layout)
        val passwordLayout = view.findViewById<TextInputLayout>(R.id.password_layout)
        val emailInput = view.findViewById<TextInputEditText>(R.id.input_email)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.input_password)

        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val rememberMe = view.findViewById<CheckBox>(R.id.checkbox_remember_me).isChecked

            var valid = true
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Valid email required"
                valid = false
            } else emailLayout.error = null

            if (password.isEmpty()) {
                passwordLayout.error = "Password required"
                valid = false
            } else passwordLayout.error = null

            if (!valid) return@setOnClickListener

            progress.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            lifecycleScope.launch {
                try {
                    authService.login(email, password, rememberMe)
                    (parentFragment as? ProfileFragment)?.onAuthSuccess()
                    view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_layout)?.visibility =
                        View.GONE
                } catch (e: retrofit2.HttpException) {
                    val error = RetrofitClient.parseError(e.response()!!)
                    val message = error?.message ?: "Something went wrong (${e.code()})"
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                } catch (e: java.net.UnknownHostException) {
                    Snackbar.make(requireView(), "No internet connection", Snackbar.LENGTH_LONG).show()
                } catch (e: java.net.SocketTimeoutException) {
                    Snackbar.make(requireView(), "Request timed out", Snackbar.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Snackbar.make(requireView(), "Unexpected error: ${e.message}", Snackbar.LENGTH_LONG).show()
                } finally {
                    progress.visibility = View.GONE
                    btnLogin.isEnabled = true
                }
            }
        }
    }
}