package com.github.cwramirezg.micredito.core.presentation.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Double.toCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
    return format.format(this)
}

fun Double.toPercentage(): String {
    val format = NumberFormat.getPercentInstance(Locale("es", "PE"))
    format.minimumFractionDigits = 2
    return format.format(this / 100)
}

@Composable
fun <T> Flow<T>.collectAsEffect(
    block: suspend (T) -> Unit
) {
    LaunchedEffect(this) {
        this@collectAsEffect.collectLatest(block)
    }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return this.matches(Regex("^[0-9]{9}$"))
}

fun String.isValidDni(): Boolean {
    return this.matches(Regex("^[0-9]{8}$"))
}
