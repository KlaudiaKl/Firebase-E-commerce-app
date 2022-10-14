package com.klaudiakrochmal.firestore.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.klaudiakrochmal.firestore.R
import com.klaudiakrochmal.firestore.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.registerTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.loginButton.setOnClickListener {
            logInRegisteredUser()
        }
        return binding.root
    }

    fun validateUserInput():Boolean{
        if (binding.emailTv.text.isNullOrBlank() && binding.passwordTv.text.isNullOrBlank()){
            Toast.makeText(requireContext(), "Provide valid credentials", Toast.LENGTH_SHORT).show()
            return false
        }
        else return true
    }

    fun logInRegisteredUser(){
        if (validateUserInput()){
            val email = binding.emailTv.text.toString().trim()
            val password = binding.passwordTv.text.toString().trim()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    task->
                    if(task.isSuccessful){
                        Log.d("LOGGED", "logged in successfully")
                        findNavController().navigate(R.id.action_loginFragment_to_userHomeFragment)
                    }
                    else{
                        Toast.makeText(requireContext(), getString(R.string.invalid_login_or_passord), Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

  /*  fun signInWithGoogle(){
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId()
            )
    }*/

}