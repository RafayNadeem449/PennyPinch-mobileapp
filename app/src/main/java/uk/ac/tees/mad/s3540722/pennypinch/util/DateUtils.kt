package uk.ac.tees.mad.s3540722.pennypinch.ui.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val transactionFormat =
        SimpleDateFormat("dd MMM yyyy · hh:mm a", Locale.UK)

    private val monthYearFormat =
        SimpleDateFormat("MMMM yyyy", Locale.UK)

    fun formatTransactionDate(timestamp: Long): String {
        return transactionFormat.format(Date(timestamp))
    }

    fun formatMonthYear(timestamp: Long): String {
        return monthYearFormat.format(Date(timestamp))
    }
}
