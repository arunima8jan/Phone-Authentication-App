package android.example.phoneauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        verify_button.setOnClickListener {
            val phnNo=phone_number.text.toString()
            if(phnNo.isNotEmpty())
            {
            sendVerificationCode(phnNo)
            }
            else{
                Toast.makeText(applicationContext,"Please enter phone number",Toast.LENGTH_LONG).show()
            }
        }
        signin_button.setOnClickListener {
            val otp=verification_code.text.toString()
            if(otp.isNotEmpty())
            {
           verifyVerificationCode(otp)
            }
            else{
                Toast.makeText(applicationContext,"Please enter OTP",Toast.LENGTH_LONG).show()
            }
        }
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code=credential.smsCode
                if(code!=null)
                {
                    verification_code.setText(code)
                }

            }

            override fun onVerificationFailed(e: FirebaseException) {

                Toast.makeText(applicationContext,"Auth Failed",Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            )
            {
              storedVerificationId = verificationId
                resendToken = token
               }
        }
 }
    private fun sendVerificationCode(phoneNo: String)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+phoneNo,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks
        )

    }
    private fun verifyVerificationCode(code: String){
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signUp(credential)
    }
    private fun signUp(credential: PhoneAuthCredential){

            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                    val user = task.result?.user
                        Toast.makeText(applicationContext,"Signed in",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this,PhoneVerify::class.java))
                        finish()

                    } else {

                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(applicationContext,"Code is incorrect",Toast.LENGTH_LONG).show()
                            verification_code.setText("")
                        }
                    }

        }

    }


}
