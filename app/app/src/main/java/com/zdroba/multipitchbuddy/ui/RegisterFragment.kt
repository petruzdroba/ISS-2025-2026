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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.R
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        val authService = app.authService

        val emailLayout = view.findViewById<TextInputLayout>(R.id.email_layout)
        val usernameLayout = view.findViewById<TextInputLayout>(R.id.username_layout)
        val passwordLayout = view.findViewById<TextInputLayout>(R.id.password_layout)
        val confirmPasswordLayout = view.findViewById<TextInputLayout>(R.id.confirm_password_layout)

        val emailInput = view.findViewById<TextInputEditText>(R.id.input_email)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.input_username)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.input_password)
        val confirmPasswordInput = view.findViewById<TextInputEditText>(R.id.input_confirm_password)

        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val btnRegister = view.findViewById<Button>(R.id.btn_register)

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            val email = emailInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            val rememberMe = view.findViewById<CheckBox>(R.id.checkbox_remember_me).isChecked

            var valid = true

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Valid email required"
                valid = false
            } else emailLayout.error = null

            if (username.length < 3) {
                usernameLayout.error = "Min 3 characters"
                valid = false
            } else usernameLayout.error = null

            if (password.length < 8) {
                passwordLayout.error = "Min 8 characters"
                valid = false
            } else passwordLayout.error = null

            if (password != confirmPassword) {
                confirmPasswordLayout.error = "Passwords do not match"
                valid = false
            } else confirmPasswordLayout.error = null

            if (!valid) return@setOnClickListener

            progress.visibility = View.VISIBLE
            btnRegister.isEnabled = false

            lifecycleScope.launch {
                try {
                    authService.register(email, username, password, rememberMe)
                    (parentFragment as? ProfileFragment)?.onAuthSuccess()
                } catch (e: Exception) {
                    emailLayout.error = "Registration failed"
                } finally {
                    progress.visibility = View.GONE
                    btnRegister.isEnabled = true
                }
            }
        }
    }
}