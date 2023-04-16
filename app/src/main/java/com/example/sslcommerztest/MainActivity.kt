package com.example.sslcommerztest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCAdditionalInitializer
import com.sslwireless.sslcommerzlibrary.model.initializer.SSLCommerzInitialization
import com.sslwireless.sslcommerzlibrary.model.response.SSLCTransactionInfoModel
import com.sslwireless.sslcommerzlibrary.model.util.SSLCCurrencyType
import com.sslwireless.sslcommerzlibrary.model.util.SSLCSdkType
import com.sslwireless.sslcommerzlibrary.view.singleton.IntegrateSSLCommerz
import com.sslwireless.sslcommerzlibrary.viewmodel.listener.SSLCTransactionResponseListener


class MainActivity : AppCompatActivity(), SSLCTransactionResponseListener {

    lateinit var edtAmount: EditText
    lateinit var btnPay: Button
    private var sslCommerzInitialization: SSLCommerzInitialization? = null
    private var additionalInitializer: SSLCAdditionalInitializer? = null
    var TAG = "Payment info"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtAmount = findViewById(R.id.edtAmount)
        btnPay = findViewById(R.id.btnPay)

        btnPay.setOnClickListener {
            val amount = edtAmount.text.toString()

            if (amount.isNotEmpty()) {
                initPayment(amount)
            }
        }
    }

    private fun initPayment(amount: String) {
        sslCommerzInitialization = SSLCommerzInitialization(
            "test643b75eb8098a",
            "test643b75eb8098a@ssl",
            amount.toDouble(),
            SSLCCurrencyType.BDT,
            "$amount",
            "",
            SSLCSdkType.TESTBOX
        )


        additionalInitializer = SSLCAdditionalInitializer()
        additionalInitializer!!.valueA = "User id: 1234"

        IntegrateSSLCommerz
            .getInstance(this)
            .addSSLCommerzInitialization(sslCommerzInitialization)
            .addAdditionalInitializer(additionalInitializer)
            .buildApiCall(this)
    }

    override fun transactionSuccess(transactionInfoModel: SSLCTransactionInfoModel?) {
        if (transactionInfoModel!!.riskLevel.equals("0")) {
            Log.d(TAG, "Transaction Successfully completed")
            Log.d(TAG, transactionInfoModel.tranId)
            Log.d(TAG, transactionInfoModel.bankTranId)
            Log.d(TAG, transactionInfoModel.amount)
            Log.d(TAG, transactionInfoModel.tranDate)
            
            Log.d("completed", transactionInfoModel.tranDate)

        } else
            Log.d(TAG, "Risk message: " + transactionInfoModel.riskTitle)
        Toast.makeText(this,"Transaction status: successful (" + transactionInfoModel.riskTitle + ")",Toast.LENGTH_SHORT).show()
        edtAmount.text = null
    }

    override fun transactionFail(s: String?) {
        Log.e(TAG, "Transaction Failed")
        edtAmount.text = null
        Toast.makeText(this, "Transaction status:$s", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@MainActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun closed(s: String?) {
        Log.e(TAG, "Transaction Failed: $s")
        edtAmount.text = null
        Toast.makeText(this, "Transaction status:$s", Toast.LENGTH_SHORT).show()
    }

}