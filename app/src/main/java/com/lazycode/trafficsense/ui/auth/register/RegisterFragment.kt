package com.lazycode.trafficsense.ui.auth.register

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import com.lazycode.trafficsense.utils.Result
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentRegisterBinding
import com.lazycode.trafficsense.ui.auth.AuthActivity
import com.lazycode.trafficsense.ui.auth.AuthViewModel
import com.lazycode.trafficsense.ui.auth.login.LoginFragment
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import org.koin.android.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var loadingDialog: AlertDialog

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        setupPlayAnimation()
        setListeners()

        return binding.root  }

    private fun setListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                if (isValid()) {
                    authViewModel.registerUser(
                        binding.edName.text.toString(),
                        binding.edEmail.text.toString(),
                        binding.edPassword.text.toString()
                    ).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loadingDialog.show()
                            }

                            is Result.Success -> {
                                loadingDialog.dismiss()
                                val builder = AlertDialog.Builder(requireContext())
                                builder.setCancelable(false)

                                with(builder)
                                {
                                    setTitle("Sukses Register")
                                    setMessage("Klik OK untuk melanjutkan.")
                                    setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                        changeToLogin()
                                    }
                                    show()
                                }
                            }

                            is Result.Error -> {
                                loadingDialog.dismiss()
                                alertDialogMessage(requireContext(), result.error, "Gagal Register")
                            }
                        }
                    }
                }
            }

            btnLogin.setOnClickListener { changeToLogin() }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                changeToLogin()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun changeToLogin() {
        parentFragmentManager.beginTransaction().apply {
            replace(
                R.id.auth_container,
                LoginFragment(),
                LoginFragment::class.java.simpleName
            )
            commit()
        }
    }

    private fun isValid() = if (binding.edName.text.isNullOrEmpty()) {
        alertDialogMessage(requireContext(), "Masukkan Nama Lengkap dengan benar!")
        false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edEmail.text.toString())
            .matches() || binding.edEmail.text.isNullOrEmpty()
    ) {
        alertDialogMessage(requireContext(), "Masukkan email dengan benar!")
        false
    } else if (binding.edPassword.text.isNullOrEmpty()) {
        alertDialogMessage(requireContext(), "Masukkan password dengan benar!")
        false
    } else if (binding.edPassword.text.toString().length < 8) {
        alertDialogMessage(requireContext(), "Password harus memiliki minimal 8 huruf!")
        false
    } else {
        true
    }


    @SuppressLint("Recycle")
    private fun setupPlayAnimation() {
        val name: Animator =
            ObjectAnimator.ofFloat(binding.edNameLayout, View.ALPHA, 1f).setDuration(150)
        val email: Animator =
            ObjectAnimator.ofFloat(binding.edEmailLayout, View.ALPHA, 1f).setDuration(150)
        val password: Animator =
            ObjectAnimator.ofFloat(binding.edPasswordLayout, View.ALPHA, 1f).setDuration(150)
        val button: Animator =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(150)
        val layoutText: Animator =
            ObjectAnimator.ofFloat(binding.layoutText, View.ALPHA, 1f).setDuration(150)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(name, email, password, button, layoutText)
        animatorSet.startDelay = 150
        animatorSet.start()
    }
}