package com.example.covidtracker

class StateList {
    var statesName = mapOf("Andaman and Nicobar Islands" to "AN",
            "Andhra Pradesh" to "AP",
            "Arunachal Pradesh" to "AR",
            "Assam" to "AS",
            "Bihar" to "BR",
            "Chandigarh" to "CH",
            "Chhattisgarh" to  "CT",
            "Dadra and Nagar Haveli and Daman and Diu" to "DN",
            "Delhi" to "DL",
            "Goa" to "GA",
            "Gujarat" to "GJ",
            "Haryana" to "HR",
            "Himachal Pradesh" to "HP",
            "Jammu and Kashmir" to "JK",
            "Jharkhand" to "JH",
            "Karnataka" to "KA",
            "Kerala" to "KL",
            "Ladakh" to "LA",
            "Lakshadweep" to "LD",
            "Madhya Pradesh" to "MP",
            "Maharashtra" to "MH",
            "Manipur" to "MN",
            "Meghalaya" to "ML",
            "Mizoram" to "MZ",
            "Nagaland" to "NL",
            "Odisha" to "OR",
            "Puducherry" to "PY",
            "Punjab" to "PB",
            "Rajasthan" to "RJ",
            "Sikkim" to "SK",
            "Tamil Nadu" to "TN",
            "Telangana" to "TG",
            "Tripura" to "TR",
            "Uttar Pradesh" to "UP",
            "Uttarakhand" to "UT",
            "West Bengal" to "WB")

    fun getStateCode(stateName:String): String? {
        var stateCode =  statesName.get(stateName)
        return stateCode
    }
}