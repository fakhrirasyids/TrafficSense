package com.lazycode.trafficsense.ui.auth.login

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import com.lazycode.trafficsense.utils.Result
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentLoginBinding
import com.lazycode.trafficsense.ui.auth.AuthActivity
import com.lazycode.trafficsense.ui.auth.AuthViewModel
import com.lazycode.trafficsense.ui.auth.register.RegisterFragment
import com.lazycode.trafficsense.ui.main.BottomMainActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loadingDialog: AlertDialog

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        setupPlayAnimation()
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (isValid()) {
                    authViewModel.loginUser(
                        binding.edEmail.text.toString(),
                        binding.edPassword.text.toString()
                    ).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loadingDialog.show()
                            }

                            is Result.Success -> {
                                if (result.data.user?.phoneNumber == null) {
                                    authViewModel.savePreferences(
                                        accessToken = result.data.accessToken.toString(),
                                        email = result.data.user?.email.toString(),
                                        fullname = result.data.user?.name.toString(),
                                    )
                                } else {
                                    authViewModel.savePreferences(
                                        accessToken = result.data.accessToken.toString(),
                                        email = result.data.user.email.toString(),
                                        fullname = result.data.user.name.toString(),
                                        phone = result.data.user.phoneNumber.toString(),
                                        profilePic = result.data.user.profilePictureUrl.toString(),
                                    )
                                }

                                loadingDialog.dismiss()

//                                val builder = AlertDialog.Builder(requireContext())
//                                builder.setCancelable(false)
//
//                                with(builder)
//                                {
//                                    setTitle("Sukses Login")
//                                    setMessage("Klik OK untuk melanjutkan.")
//                                    setPositiveButton("OK") { dialog, _ ->
//                                        dialog.dismiss()
                                        val iMain =
                                            Intent(
                                                requireActivity(),
                                                BottomMainActivity::class.java
                                            )
                                        requireActivity().finishAffinity()
                                        requireActivity().startActivity(iMain)
//                                    }
//                                    show()
//                                }
                            }

                            is Result.Error -> {
                                loadingDialog.dismiss()
                                alertDialogMessage(requireContext(), "Unauthorized user", "Gagal")
                            }
                        }
                    }
                }
            }

            btnRegister.setOnClickListener { changeToRegister() }
        }
    }

    private fun isValid() = if (binding.edEmail.text.isNullOrEmpty()
    ) {
        alertDialogMessage(requireContext(), "Masukkan username dengan benar!")
        false
    } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edEmail.text.toString())
            .matches() || binding.edEmail.text.isNullOrEmpty()
    ) {
        alertDialogMessage(requireContext(), "Masukkan email dengan benar!")
        false
    } else if (binding.edPassword.text.isNullOrEmpty()) {
        alertDialogMessage(requireContext(), "Masukkan password dengan benar!")
        false
    } else {
        true
    }


    private fun changeToRegister() {
        parentFragmentManager.beginTransaction().apply {
            replace(
                R.id.auth_container,
                RegisterFragment(),
                RegisterFragment::class.java.simpleName
            )
            commit()
        }
    }

    @SuppressLint("Recycle")
    private fun setupPlayAnimation() {
        val email: Animator =
            ObjectAnimator.ofFloat(binding.edEmailLayout, View.ALPHA, 1f).setDuration(150)
        val password: Animator =
            ObjectAnimator.ofFloat(binding.edPasswordLayout, View.ALPHA, 1f).setDuration(150)
        val button: Animator =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(150)
        val layoutText: Animator =
            ObjectAnimator.ofFloat(binding.layoutText, View.ALPHA, 1f).setDuration(150)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(email, password, button, layoutText)
        animatorSet.startDelay = 150
        animatorSet.start()
    }
}