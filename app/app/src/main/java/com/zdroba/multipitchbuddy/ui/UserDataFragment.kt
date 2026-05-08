package com.zdroba.multipitchbuddy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.zdroba.multipitchbuddy.App
import com.zdroba.multipitchbuddy.R
import com.zdroba.multipitchbuddy.network.RetrofitClient
import kotlinx.coroutines.launch

class UserDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as App
        val authService = app.authService
        val syncOrchestrator = app.syncOrchestrator

        lifecycleScope.launch {
            try {
                val user = authService.me()
                view.findViewById<TextView>(R.id.txt_email).text = user.email
                view.findViewById<TextView>(R.id.txt_username).text = user.username
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
            }
        }

        view.findViewById<Button>(R.id.btn_logout).setOnClickListener {
            lifecycleScope.launch {
                authService.logout()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.auth_container, LoginFragment())
                    .commit()

                val tabLayout = requireParentFragment()
                    .view
                    ?.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_layout)

                tabLayout?.visibility = View.VISIBLE
            }
        }

        view.findViewById<Button>(R.id.btn_sync_upload).setOnClickListener {
            lifecycleScope.launch {
                try {
                    syncOrchestrator.upload()
                }catch (e: retrofit2.HttpException) {
                    val error = RetrofitClient.parseError(e.response()!!)
                    val message = error?.message ?: "Something went wrong (${e.code()})"
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                } catch (e: java.net.UnknownHostException) {
                    Snackbar.make(requireView(), "No internet connection", Snackbar.LENGTH_LONG).show()
                } catch (e: java.net.SocketTimeoutException) {
                    Snackbar.make(requireView(), "Request timed out", Snackbar.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Snackbar.make(requireView(), "Unexpected error: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        view.findViewById<Button>(R.id.btn_sync_download).setOnClickListener {
            lifecycleScope.launch {
                try {
                    syncOrchestrator.download()
                } catch (e: retrofit2.HttpException) {
                    val error = RetrofitClient.parseError(e.response()!!)
                    val message = error?.message ?: "Something went wrong (${e.code()})"
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
                } catch (e: java.net.UnknownHostException) {
                    Snackbar.make(requireView(), "No internet connection", Snackbar.LENGTH_LONG).show()
                } catch (e: java.net.SocketTimeoutException) {
                    Snackbar.make(requireView(), "Request timed out", Snackbar.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.e("SYNC", "Upload failed", e)
                    Snackbar.make(requireView(), "Unexpected error: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}