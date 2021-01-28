package com.example.declarativepesel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PeselEntry()
            }
        }
    }

    @Composable
    fun PeselEntry() {
        val pesel = savedInstanceState { "" }
        val incorrect = savedInstanceState { true }
        val birth = savedInstanceState { "" }
        val sex = savedInstanceState { "" }
        val correctControlSum = savedInstanceState { false }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(24.dp)
            ) {
                TextField(
                        label = {
                            Text("Pesel")
                        },
                        maxLines = 1,
                        value = pesel.value,
                        onValueChange = { pesel.value = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(230.dp)
                )
                Button(
                        onClick = {validatePesel(pesel.value, incorrect, birth, sex, correctControlSum)},
                        modifier = Modifier
                                .padding(12.dp)
                                .width(100.dp)
                ) {
                    Text("Validate")
                }
            }
            if(incorrect.value){
                PeselNotValid()
            }
            else {
                Column {
                    BirthDate(birth = birth.value)
                    Sex(sex = sex.value)
                    ControlSum(correctControlSum = correctControlSum.value)
                }
            }
        }
    }


    private fun validatePesel(pesel: String,
                      incorrect: MutableState<Boolean>,
                      birth: MutableState<String>,
                      sex: MutableState<String>,
                      correctControlSum: MutableState<Boolean>){
        incorrect.value = !pesel.matches(Regex("[0-9]{11}"))
            if(incorrect.value)
                return


        sex.value = checkSex(pesel)
        birth.value = checkBirth(pesel)
        correctControlSum.value = checkControlSum(pesel)
    }

    private fun checkSex(pesel: String): String {
        if (pesel[9].toInt() % 2 == 0) {
            return  "Female"
        } else {
            return "Male"
        }
    }

    private fun checkBirth(pesel: String): String{
        var month = pesel.substring(2, 4).toInt()
        var year = 0
        when(month) {
            in 1..12 -> {
                year = 1900
            }
            in 81..92 -> {
                year = 1800
                month -= 80
            }
            in 21..32 -> {
                year = 2000
                month -= 20
            }
            in 41..52 -> {
                year = 2100
                month -= 40
            }
            in 61..72 -> {
                year = 2200
                month -= 60
            }
        }

        return "${pesel.substring(4, 6)}/$month/${year+pesel.substring(0, 2).toInt()}"
    }

    private fun checkControlSum(pesel: String): Boolean {
        val numbers = pesel.toCharArray().map { c -> Character.getNumericValue(c) }
        val wages = arrayOf(1, 3, 7, 9, 1, 3, 7, 9, 1, 3, 1)

        val sum = numbers.zip(wages).map { v -> v.first * v.second}.sum()

        if(sum % 10 == 0) {
            return true
        }
        return false
    }

    @Composable
    fun BirthDate(birth: String) {
        Text("Birth date: $birth")
    }

    @Composable
    fun Sex(sex: String) {
        Text("Sex: $sex")
    }


    @Composable
    fun ControlSum(correctControlSum: Boolean) {
        Text("Control sum: ${if(correctControlSum) "Correct" else "Wrong"}")
    }

    @Composable
    fun PeselNotValid() {
        Text("Pesel should consist of 11 digits")
    }


    @Preview
    @Composable
    fun PeselEntryPreview() {
        PeselEntry()
    }
}

