package com.demoss.mypaint

import org.junit.Test

const val EMPTY_STRING = ""

class SkinGoalTest {

    ///////////////////////////////////////////////////////////////////////////
    // Test data
    ///////////////////////////////////////////////////////////////////////////
    val routineIds = listOf<Int>(1, 2, 3, 4, 5)

    val skinGoals = listOf<SkinGoal>(
        SkinGoal(listOf(1, 3, 5)),
        SkinGoal(listOf(1, 2, 4)),
        SkinGoal(listOf(2, 4, 5)),
        SkinGoal(listOf(3, 4, 5))
    )

    val achievementsSet = listOf<AchievementSet>(
        AchievementSet(
            0, 0, listOf(
                Achievement(4, 1),
                Achievement(4, 2),
                Achievement(4, 4),
                Achievement(4, 8),
                Achievement(4, 12)
            ), skinGoals[0]
        ),

        AchievementSet(
            0, 0, listOf(
                Achievement(4, 1),
                Achievement(4, 2),
                Achievement(4, 4),
                Achievement(4, 8)
            ), skinGoals[1]
        ),

        AchievementSet(
            0, 0, listOf(
                Achievement(3, 1),
                Achievement(3, 2),
                Achievement(3, 4)
            ), skinGoals[2]
        )
    )

    val weeks = listOf<Week>(
        Week(
            listOf(
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5))
            ), mutableListOf()
        ),
        Week(
            listOf(UseLog(listOf(1, 3, 5)), UseLog(listOf(1, 3, 5)), UseLog(listOf(1, 3, 5)), UseLog(listOf(1, 3, 5))),
            mutableListOf()
        ),
        Week(
            listOf(
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5))
            ), mutableListOf()
        ),
        Week(
            listOf(
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5)),
                UseLog(listOf(1, 2, 3, 4, 5))
            ), mutableListOf()
        )
    )

    ///////////////////////////////////////////////////////////////////////////
    // Main part - logic
    ///////////////////////////////////////////////////////////////////////////
    @Test
    fun run() {
        for (week in weeks) {
            achievementsFor@ for (achieveSet in achievementsSet) {
                var counterPerThisWeek = 0
                for (log in week.useLogs) {
                    if (log.isLogFitsGoal(achieveSet.goal)) counterPerThisWeek++
                    if (achieveSet.firstDate == EMPTY_STRING) achieveSet.firstDate = log.date
                    if (counterPerThisWeek == achieveSet.achievements[achieveSet.currentAchievementPosition].trainingsPerWeek) {
                        ///////////////////////////////////////////////////////////////////////////
                        // here we have reached achievement, week, log of day [when reached]
                        // counterPerThisWeek may be replaced with list of logs
                        // and use it's size like counter [in this case we will have all logs for day, dates reached from - to]
                        ///////////////////////////////////////////////////////////////////////////
                        achieveSet.achievements[achieveSet.currentAchievementPosition].apply {
                            if (achieveSet.currentWeek + 1 == trainingsPerWeek) {
                                achievedTimes++
                                date = log.date
                            }
                        }
                        // current week has custom setter that increments currentAchievementPosition when needed
                        achieveSet.currentWeek++
                        // we can't use named rules from lambda, that's why I didn't use apply or something like that for achieveSet
                        continue@achievementsFor
                    }
                }
                achieveSet.currentWeek = 0
                achieveSet.currentAchievementPosition = 0
            }
        }

        // here is our results
        achievementsSet.forEach {
            print(it.currentAchievementPosition)
        }
    }
}

private fun UseLog.isLogFitsGoal(goal: SkinGoal): Boolean =
    this.routineIds.containsAll(goal.routineIds)

data class SkinGoal(val routineIds: List<Int>)

data class UseLog(val routineIds: List<Int>) {
    val date: String = "some date"
}

data class Week(val useLogs: List<UseLog>, val achievements: MutableList<Achievement>)

data class Achievement(val trainingsPerWeek: Int, val weeks: Int) {

    var achievedTimes: Int = 0
    var date: String = EMPTY_STRING
}

class AchievementSet(
    _currentWeek: Int,
    var currentAchievementPosition: Int,
    val achievements: List<Achievement>,
    val goal: SkinGoal
) {

    var firstDate: String = EMPTY_STRING
    set(value) { if (field == EMPTY_STRING) field = value }

    var currentWeek = _currentWeek
        set(value) {
            if (value == achievements[currentAchievementPosition].weeks) currentAchievementPosition++
            // cycling of achievements
            if (currentAchievementPosition == achievements.size) {
                currentAchievementPosition = 0
                field = 0
            } else {
                field = value
            }
        }
}