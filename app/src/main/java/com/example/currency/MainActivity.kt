package com.example.currency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.currency.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    var baseCurrency = "EUR"
    var convertedToCurrency = "USD"
    var conversionRate = 0f
     lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spinnerSetup()
        textChangedStuff()

    }
    private fun textChangedStuff() {

        et_firstConversion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Type a value", Toast.LENGTH_SHORT).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Main", "Before Text Changed")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("Main", "OnTextChanged")
            }

        })

    }

    private fun getApiResult() {
        if (et_firstConversion != null && et_firstConversion.text.isNotEmpty() && et_firstConversion.text.isNotBlank()) {
            var API =
                "https://api.fastforex.io/fetch-multi?from=$baseCurrency&to=$convertedToCurrency&api_key=2c0a9983a9-08bde4e0af-rvn2kk"
            if (baseCurrency == convertedToCurrency) {
                Toast.makeText(
                    applicationContext,
                    "Please pick a currency to convert",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                GlobalScope.launch(Dispatchers.IO) {

                    try {

                        val apiResult = URL(API).readText()
                        val jsonObject = JSONObject(apiResult)
                        conversionRate =
                            jsonObject.getJSONObject("results").getString(convertedToCurrency)
                                .toFloat()

                        Log.d("Main", "$conversionRate")
                        Log.d("Main", apiResult)

                        withContext(Dispatchers.Main) {
                            val text =
                                ((et_firstConversion.text.toString()
                                    .toFloat()) * conversionRate).toString()
                            et_secondConversion?.setText(text)

                        }

                    } catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }

            }
        }
    }
    private fun spinnerSetup() {
        val spinner=binding.spinnerFirstConversion
        val spinner2=binding.spinnerSecondConversion

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter

        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner2.adapter = adapter

        }

        spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                baseCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }

        })

        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                convertedToCurrency = parent?.getItemAtPosition(position).toString()
                getApiResult()
            }
        })
    }
}

