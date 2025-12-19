package com.screetime.core.model

data class Task(
    val id: String = "",
    val familyId: String = "",
    val title: String = "",
    val description: String = "",
    val rewardMinutes: Int = 0,
    val childId: String? = null,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val approvedAt: Long? = null,
    val approvedBy: String? = null,
    val rejectedAt: Long? = null,
    val rejectedBy: String? = null,
    val rejectionReason: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceType: RecurrenceType? = null,
    val deadline: Long? = null
)

enum class TaskStatus {
    PENDING,        // Created, assigned to child
    COMPLETED,      // Child marked as done
    APPROVED,       // Parent approved
    REJECTED,       // Parent rejected
    EXPIRED         // Past deadline
}

enum class RecurrenceType {
    DAILY,
    WEEKLY,
    MONTHLY
}

// Default tasks template
object DefaultTasks {
    val tasks = listOf(
        TaskTemplate("Make bed", "Make your bed neatly", 10),
        TaskTemplate("Brush teeth (morning)", "Brush your teeth in the morning", 5),
        TaskTemplate("Brush teeth (evening)", "Brush your teeth before bed", 5),
        TaskTemplate("Homework", "Complete your homework", 30),
        TaskTemplate("Reading (20 min)", "Read for 20 minutes", 20),
        TaskTemplate("Clean room", "Clean and organize your room", 15),
        TaskTemplate("Help with dishes", "Help wash or dry the dishes", 10),
        TaskTemplate("Physical activity (30 min)", "Exercise or play outside for 30 minutes", 20),
        TaskTemplate("Prepare school bag", "Pack your school bag for tomorrow", 5),
        TaskTemplate("Set the table", "Help set the table for meals", 5),
        TaskTemplate("Take out trash", "Take out the trash", 10),
        TaskTemplate("Water plants", "Water the household plants", 5),
        TaskTemplate("Practice instrument (30 min)", "Practice your musical instrument", 25),
        TaskTemplate("Study (1 hour)", "Study for school subjects", 40)
    )
}

data class TaskTemplate(
    val title: String,
    val description: String,
    val defaultRewardMinutes: Int
)
