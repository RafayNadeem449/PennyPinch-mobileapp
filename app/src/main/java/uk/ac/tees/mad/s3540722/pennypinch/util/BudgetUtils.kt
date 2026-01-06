package uk.ac.tees.mad.s3540722.pennypinch

import java.util.Calendar

object BudgetUtils {

    fun currentRange(periodType: String): Pair<Long, Long> {
        return if (periodType == "WEEKLY") currentWeekRange() else currentMonthRange()
    }

    private fun currentWeekRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.DAY_OF_YEAR, 7)
        val end = cal.timeInMillis

        return start to end
    }

    private fun currentMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()

        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis

        return start to end
    }
}
