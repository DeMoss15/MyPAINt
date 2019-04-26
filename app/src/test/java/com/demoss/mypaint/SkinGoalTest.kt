package com.demoss.mypaint

import org.junit.Test

class SkinGoalTest {

    val routineIds = listOf<Int>(1, 2, 3, 4, 5)

    val skinGoals = listOf<SkinGoal>(
        SkinGoal(listOf(1, 3, 5)),
        SkinGoal(listOf(1, 2, 4)),
        SkinGoal(listOf(2, 4, 5)),
        SkinGoal(listOf(3, 4, 5))
    )

    val achievementsSet = mapOf<SkinGoal, AchievementSet>(
        skinGoals[0] to AchievementSet(
            0, 0, listOf(
                Achievement(4, 1),
                Achievement(4, 2),
                Achievement(4, 4),
                Achievement(4, 8),
                Achievement(4, 12)
            ), skinGoals[0]
        ),

        skinGoals[1] to AchievementSet(
            0, 0, listOf(
                Achievement(4, 1),
                Achievement(4, 2),
                Achievement(4, 4),
                Achievement(4, 8)
            ), skinGoals[1]
        ),

        skinGoals[2] to AchievementSet(
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

    @Test
    fun run() {
        weeks.map { week ->
            val skinGoalAppearancesOnWeek = mutableMapOf<SkinGoal, Int>()
            skinGoals.forEach {
                skinGoalAppearancesOnWeek[it] = 0
            }
            // how much days according to each goal
            week.useLogs.forEach { log ->
                skinGoals.forEach { goal ->
                    skinGoalAppearancesOnWeek[goal]?.let {
                        skinGoalAppearancesOnWeek[goal] = it + if (log.isLogFitsGoal(goal)) 1 else 0
                    }
                }
            }
            // check for achievement
            skinGoalAppearancesOnWeek.forEach { (goal, appPerThisWeek) ->
                achievementsSet[goal]?.apply {
                    with(achievements[currentAchievement]) {
                        if (appPerThisWeek >= trainingsPerWeek) {
                            currentWeek++
                            if (currentWeek == weeks) currentAchievement++
                        } else {
                            currentWeek = 0
                            currentAchievement = 0
                        }
                    }
                }
            }
        }

        achievementsSet.forEach {
            print(it.value.currentAchievement)
        }
    }

    private fun UseLog.isLogFitsGoal(goal: SkinGoal): Boolean =
        this.routineIds.containsAll(goal.routineIds)

    data class SkinGoal(val routineIds: List<Int>)

    data class UseLog(val routineIds: List<Int>)

    data class Week(val useLogs: List<UseLog>, val achievements: MutableList<Achievement>)

    data class Achievement(val trainingsPerWeek: Int, val weeks: Int)

    data class AchievementSet(
        var currentWeek: Int,
        var currentAchievement: Int,
        val achievements: List<Achievement>,
        val goal: SkinGoal
    )
}