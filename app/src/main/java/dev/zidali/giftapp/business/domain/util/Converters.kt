package dev.zidali.giftapp.business.domain.util

class Converters {

    companion object {

        fun convertIntMonthToTextMonth(month: Int): String {
            when(month) {
                0 -> return "January"
                1 -> return "February"
                2 -> return "March"
                3 -> return "April"
                4 -> return "May"
                5 -> return "June"
                6 -> return "July"
                7 -> return "August"
                8 -> return "September"
                9 -> return "October"
                10 -> return "November"
                11 -> return "December"
            }
            return "Invalid Month"
        }

        fun convertCalendarIntMonthToIntMonth(month: Int): Int {
            when(month) {
                0 -> return 1
                1 -> return 2
                2 -> return 3
                3 -> return 4
                4 -> return 5
                5 -> return 6
                6 -> return 7
                7 -> return 8
                8 -> return 9
                9 -> return 10
                10 -> return 11
                11 -> return 12
            }
            return 0
        }

        fun convertIntMonthToCalendarIntMonth(month: Int): Int {
            when(month) {
                1 -> return 0
                2 -> return 1
                3 -> return 2
                4 -> return 3
                5 -> return 4
                6 -> return 5
                7 -> return 6
                8 -> return 7
                9 -> return 8
                10 -> return 9
                11 -> return 10
                12 -> return 11
            }
            return 0
        }

    }

}