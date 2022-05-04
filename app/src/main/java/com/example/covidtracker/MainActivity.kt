package com.example.covidtracker

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import org.json.JSONException
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    lateinit var tvConfirmed: TextView
    lateinit var tvConfirmedAdd: TextView
    lateinit var tvActive: TextView
    lateinit var tvRecovered: TextView
    lateinit var tvRecoveredAdd: TextView
    lateinit var tvDeaths: TextView
    lateinit var tvDeathsAdd: TextView
    lateinit var tvVaccination1: TextView
    lateinit var tvVaccination2: TextView
    lateinit var tvTested: TextView
    lateinit var tvTestedAdd: TextView
    lateinit var tvDate: TextView
    lateinit var tvPopulation: TextView
    lateinit var chart:PieChart
    lateinit var simpleSpinner: Spinner
    lateinit var tryBtn:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_CovidTracker)
        setContentView(R.layout.activity_main)

        tvConfirmed = findViewById(R.id.confValue)
        tvConfirmedAdd = findViewById(R.id.confGrowth)
        tvActive = findViewById(R.id.activeValue)
        tvRecovered = findViewById(R.id.recovValue)
        tvRecoveredAdd = findViewById(R.id.recovGrowth)
        tvDeaths = findViewById(R.id.deathValue)
        tvDeathsAdd = findViewById(R.id.deathGrowth)
        tvVaccination1 = findViewById(R.id.vaciDose1)
        tvVaccination2 = findViewById(R.id.vaciDose2)
        tvTested = findViewById(R.id.testValue)
        tvTestedAdd = findViewById(R.id.testGrowth)
        tvDate = findViewById(R.id.tvDate)
        tvPopulation = findViewById(R.id.populationTv)
        chart = findViewById(R.id.pie_chart)
        simpleSpinner = findViewById(R.id.simpleSpinner)
//        tryBtn = findViewById(R.id.tryBtn)
        var stateList = StateList()

        if(!isNetworkAvailable())
        showDialogBox()

        simpleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedState:String = parent!!.getItemAtPosition(position).toString()
                var stateCode = stateList.getStateCode(selectedState).toString()
                if (stateCode != null) {
                    getStateStats(stateCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun getStateStats(stateName:String) {
        val url = "https://api.covid19tracker.in/data/static/data.min.json"
        val queue = Volley.newRequestQueue(this@MainActivity)
        val request =
                JsonObjectRequest(Request.Method.GET, url, null, { response ->
                    try {
                        val dataObj = response.getJSONObject(stateName)
                        val summaryObj = dataObj.getJSONObject("total")
                        val summaryObj1 = dataObj.getJSONObject("meta")
                        val date: String = summaryObj1.getString("last_updated")
                        val confirmed: Int = summaryObj.getInt("confirmed")
                        val deaths: Int = summaryObj.getInt("deceased")
                        val recovered: Int = summaryObj.getInt("recovered")
                        val tested: Int = summaryObj.getInt("tested")
                        val vaccinated1: Int = summaryObj.getInt("vaccinated1")
                        val vaccinated2: Int = summaryObj.getInt("vaccinated2")
                        var active: Int = confirmed - recovered - deaths
                        val population = summaryObj1.getInt("population")
                        val unvaccinated = population - vaccinated1

                        if(active < 0)
                            active = 0

                        tvConfirmed.text = NumberFormat.getInstance().format(confirmed).toString()
                        tvActive.text = NumberFormat.getInstance().format(active).toString()
                        tvRecovered.text = NumberFormat.getInstance().format(recovered).toString()
                        tvDeaths.text = NumberFormat.getInstance().format(deaths).toString()
                        tvVaccination1.text =
                                "Dose 1: ${NumberFormat.getInstance().format(vaccinated1)}"
                        tvVaccination2.text =
                                "Dose 2: ${NumberFormat.getInstance().format(vaccinated2)}"
                        tvTested.text = NumberFormat.getInstance().format(tested).toString()
                        tvDate.text = "Updated on ${date.substring(0,10)}"
                        tvPopulation.text = "Population: ${NumberFormat.getInstance().format(population).toString()}"

                        setupChart(confirmed,active,recovered,deaths,vaccinated2,vaccinated1,unvaccinated)

                        if (!(dataObj.has("delta"))) {

                            tvConfirmedAdd.text = ""
                            tvDeathsAdd.text = ""
                            tvRecoveredAdd.text = ""
                            tvTestedAdd.text = ""

                        }
                        else {
                            val currentDayObj = dataObj.getJSONObject("delta")
                            if(currentDayObj.has("confirmed")){
                                if(currentDayObj.getString("confirmed") != "null") {
                                    val confirmedAdd: Int = currentDayObj.getInt("confirmed")
                                    tvConfirmedAdd.text =
                                            "(+ ${NumberFormat.getInstance().format(confirmedAdd)})"
                                }
                                else tvConfirmedAdd.text =""
                            }else tvConfirmedAdd.text =""

                            if(currentDayObj.has("deceased")) {
                                if(currentDayObj.getString("deceased") != "null") {
                                    val deathsAdd: Int = currentDayObj.getInt("deceased")
                                    tvDeathsAdd.text =
                                            "(+ ${NumberFormat.getInstance().format(deathsAdd)})"
                                }
                                else tvDeathsAdd.text = ""
                            }else tvDeathsAdd.text = ""

                            if(currentDayObj.has("recovered")){
                                if(currentDayObj.getString("recovered") != "null") {
                                    val recoveredAdd: Int = currentDayObj.getInt("recovered")
                                    tvRecoveredAdd.text =
                                            "(+ ${NumberFormat.getInstance().format(recoveredAdd)})"
                                }
                                else tvRecoveredAdd.text = ""
                            } else tvRecoveredAdd.text = ""

                            if(currentDayObj.has("tested")){
                                if(currentDayObj.getString("tested") != "null") {
                                    val testsAdd: Int = currentDayObj.getInt("tested")
                                    tvTestedAdd.text =
                                            "(+ ${NumberFormat.getInstance().format(testsAdd)})"
                                }
                                else tvTestedAdd.text = ""
                            } else tvTestedAdd.text = ""

                        }


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, { error ->
                    {
                        Toast.makeText(
                                this,
                                "Failed to get response. Please try after sometime.",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        queue.add(request)
    }

    fun setupChart(confirmed: Int, active:Int , recovered: Int, deaths: Int, vaccinated: Int, partiallyVaccinated:Int, unvaccinated:Int) {

        var dataEntries = mutableListOf<PieEntry>()
        var type = listOf("Confirmed","Active Cases", "Recovered", "Deaths", "Vaccinated","PartiallyVaccinated", "Unvaccinated")
        var values = listOf(confirmed.toFloat(), active.toFloat(), recovered.toFloat(), deaths.toFloat(), vaccinated.toFloat(), partiallyVaccinated.toFloat(), unvaccinated.toFloat())

        for (i in type.indices) {
            dataEntries.add(PieEntry(values.elementAt(i),type.elementAt(i)))
        }

        chart.animateXY(1000,1000)

        val pieDataSet = PieDataSet(dataEntries,"Data label")
        pieDataSet.setColors(
                resources.getColor(R.color.yellow),
                resources.getColor(R.color.teal_200),
                resources.getColor(R.color.green_pie),
                resources.getColor(R.color.red),
                resources.getColor(R.color.orange),
                resources.getColor(R.color.light_orange),
                resources.getColor(R.color.light_gray)
        )

        chart.legend.isEnabled = false
        chart.description.isEnabled = false

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)
        chart.setHoleColor(R.color.color_primary_dark)
        chart.setDrawEntryLabels(false)
        chart.data = pieData
    }

    fun showDialogBox(){
        val noInternetDialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.nointernet_alert)
        noInternetDialog.setPositiveButton("Try Again"){_,_ ->
            if(!isNetworkAvailable()) {
                noInternetDialog.show()
            }
            else{
                recreate()
                Toast.makeText(this, "Connected to Internet",Toast.LENGTH_SHORT).show()
            }
        }
        noInternetDialog.create()
        noInternetDialog.show()
        /*val dialog = Dialog(this)
        dialog.setContentView(R.layout.test_layout)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btn:Button = dialog.findViewById(R.id.tryBtn2)
        btn.setOnClickListener {
            recreate()
            dialog.cancel()
        }
        *//*dialog.create()*//*
        if(!isNetworkAvailable())
        dialog.show()*/
    }

    fun isNetworkAvailable():Boolean{
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return false
    }

}

