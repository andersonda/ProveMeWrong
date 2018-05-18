package com.danderson.provemewrong

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.danderson.provemewrong.debatemodel.User
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.ResultCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthenticationActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val AUTHENTICATION_REQUEST = 101
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        if(auth.currentUser == null){
            authenticateUser()
        } else{
            startActivity(Intent(this, OverviewActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = IdpResponse.fromResultIntent(data)

        if(requestCode == AUTHENTICATION_REQUEST){
            if(resultCode == ResultCodes.OK){
                val currentUser = FirebaseAuth.getInstance().currentUser!!
                val reference = database.getReference("/users/${currentUser.uid}")
                reference.setValue(User(currentUser.email!!, currentUser.displayName!!,
                        currentUser.photoUrl.toString()))
                startActivity(Intent(this, OverviewActivity::class.java))
                return
            }
        } else{
            if(response == null){
                return
            }
            if(response.errorCode == ErrorCodes.NO_NETWORK){
                return
            }
            if(response.errorCode == ErrorCodes.NO_NETWORK){
                return
            }
        }
    }

    /**
     * function that authenticates user prior to accessing the main activity
     */
    private fun authenticateUser(){
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(getProviderList())
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.AppTheme)
                        .build(), AUTHENTICATION_REQUEST
        )
    }

    private fun getProviderList(): List<AuthUI.IdpConfig>{
        val providers = mutableListOf<AuthUI.IdpConfig>()
        providers.add(AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build())
        providers.add(AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
        return providers
    }
}
