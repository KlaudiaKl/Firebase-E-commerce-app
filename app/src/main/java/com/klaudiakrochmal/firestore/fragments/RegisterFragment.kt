package com.klaudiakrochmal.firestore.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.klaudiakrochmal.firestore.R
import com.klaudiakrochmal.firestore.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.loginTv.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        validateUserInput()
        binding.registerButton.setOnClickListener {

            val name = binding.firstNameEt.text.toString()
            val lastName = binding.lastNameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            val confirmedPassword = binding.confirmPasswordEt.text.toString()
            if (validateUserInput() && areAllFieldsFilled(
                    name,
                    lastName,
                    email,
                    password,
                    confirmedPassword
                )
            ) registerUser()
            else Log.d("VALIDATION ERR", "validation is false")
        }
        return binding.root
    }

    //only if this returns true it's ok to add user to db
    fun validateUserInput(): Boolean {
        isNameLongEnough()
        validateEmail()
        validatePassword()

        return isNameLongEnough() &&
                validateEmail() &&
                validatePassword()
    }

    //all fields must not be empty
    fun areAllFieldsFilled(
        name: String,
        lastName: String,
        email: String,
        password: String,
        confirmedPassword: String
    ): Boolean {
        return !(TextUtils.isEmpty(name) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(email) && TextUtils.isEmpty(
            password
        ) && TextUtils.isEmpty(confirmedPassword))

    }

    //both last and first name must be more than two chars
    fun isNameLongEnough(): Boolean {
        var flag: Boolean
        binding.firstNameEt.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 2) {
                binding.firstNameBox.error = getString(R.string.too_short)
            } else {
                binding.firstNameBox.isErrorEnabled = false
                flag = true
            }
        }
        binding.lastNameEt.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 2) {
                binding.lastNameBox.isErrorEnabled = true
                binding.lastNameBox.error = getString(R.string.too_short)
                flag = false
            } else {
                binding.lastNameBox.isErrorEnabled = false
                flag = true
            }
        }
        if (binding.firstNameEt.text.toString().length > 2 && binding.lastNameEt.text.toString().length > 2) {
            Log.d("NAME", "true")
            return true
        } else return false
    }


    fun isEmailValid(email: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
    }

    //check if input is a valid email address
    fun validateEmail(): Boolean {
        var flag = false
        binding.emailEt.doOnTextChanged { text, start, before, count ->
            if (!isEmailValid(text)) {
                binding.emailBox.isErrorEnabled = true
                binding.emailBox.error = getString(R.string.provide_valid_email)
            } else {
                binding.emailBox.isErrorEnabled = false
                flag = true
            }
        }
        if (isEmailValid(binding.emailEt.text.toString())) {
            Log.d("MAIL", "true")
            return true
        } else return false

    }

    //checking if password is at least 8 chars, checking if the confirmed password is the same as password
    fun validatePassword(): Boolean {
        var password: String = ""
        var confirmedPassword: String = ""
        var flag1 = false
        var flag2 = false
        binding.passwordEt.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 8) {
                binding.passwordBox.isErrorEnabled = true
                binding.passwordBox.error = getString(R.string.password_length)
                flag1 = false
            } else {
                binding.passwordBox.isErrorEnabled = false
                password = text.trim().toString()
                Log.d("PASSWORD", password.toString())
                flag1 = true
            }
        }

        binding.confirmPasswordEt.doOnTextChanged { text, start, before, count ->
            confirmedPassword = text!!.trim().toString()

            if (password != confirmedPassword) {
                binding.confirmPasswordBox.isErrorEnabled = true
                binding.confirmPasswordBox.error = "Passwords not matching"
                flag2 = false
            } else {
                binding.confirmPasswordBox.isErrorEnabled = false
                flag2 = true
            }
        }
        if (password.length < 8 && confirmedPassword == password) {
            Log.d("PASSWORD_BOOL", "true")
            return true
        } else return false
    }


    //https://firebase.google.com/docs/auth/android/password-auth
    fun registerUser() {
        val email: String = binding.emailEt.text.toString().trim()
        val password: String = binding.passwordEt.text.toString().trim { it <= ' ' }

        binding.progressBar.isVisible = true
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                OnCompleteListener {
                    if (it.isSuccessful) {
                        val user: FirebaseUser = it.result!!.user!!
                        Log.d("AUTH SUCCESSFUL", "user added")
                        findNavController().navigate(R.id.action_registerFragment_to_userHomeFragment)

                    } else {
                        Log.d("REGISTRATION ERROR", it.exception!!.message.toString())
                        Toast.makeText(requireContext(), it.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                    }

                })
        binding.progressBar.isVisible = false
    }


}