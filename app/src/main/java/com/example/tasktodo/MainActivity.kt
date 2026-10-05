package com.example.tasktodo

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tasktodo.ui.theme.TaskToDoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// -----------------------------
// TASK DATA
// -----------------------------

data class Task(
    val name: String,
    val date: String,
    val time: String,
    val completed: Boolean = false
)


// -----------------------------
// MAIN ACTIVITY
// -----------------------------

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaskToDoTheme {
                App()
            }
        }
    }
}


// -----------------------------
// APP
// -----------------------------

@Composable
fun App() {

    var currentScreen by remember {
        mutableStateOf("login")
    }

    val tasks = remember {
        mutableStateListOf<Task>()
    }

    var selectedTask by remember {
        mutableStateOf<Task?>(null)
    }

    var taskToEdit by remember {
        mutableStateOf<Task?>(null)
    }

    // Profile picture
    var profileImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    when (currentScreen) {

        // LOGIN
        "login" -> {

            LoginScreen(

                onLoginSuccess = {
                    currentScreen = "todo"
                },

                onSignUpClick = {
                    currentScreen = "signup"
                }
            )
        }


        // SIGN UP
        "signup" -> {

            SignUpScreen(

                onLoginClick = {
                    currentScreen = "login"
                }
            )
        }


        // HOME
        "todo" -> {

            MainTaskScreen(

                tasks = tasks,

                selectedPage = 0,

                onHomeClick = {
                    currentScreen = "todo"
                },

                onAddClick = {
                    currentScreen = "addTask"
                },

                onCompletedClick = {
                    currentScreen = "completed"
                },

                onProfileClick = {
                    currentScreen = "profile"
                },

                onTaskClick = {
                    selectedTask = it
                }
            )
        }


        // COMPLETED
        "completed" -> {

            MainTaskScreen(

                tasks = tasks,

                selectedPage = 2,

                onHomeClick = {
                    currentScreen = "todo"
                },

                onAddClick = {
                    currentScreen = "addTask"
                },

                onCompletedClick = {
                    currentScreen = "completed"
                },

                onProfileClick = {
                    currentScreen = "profile"
                },

                onTaskClick = {
                    selectedTask = it
                }
            )
        }


        // ADD TASK
        "addTask" -> {

            AddTaskScreen(

                onBack = {
                    currentScreen = "todo"
                },

                onSave = { newTask ->

                    tasks.add(newTask)

                    currentScreen = "todo"
                }
            )
        }


        // EDIT TASK
        "editTask" -> {

            if (taskToEdit != null) {

                EditTaskScreen(

                    task = taskToEdit!!,

                    onBack = {
                        currentScreen = "todo"
                    },

                    onSave = { updatedTask ->

                        val index = tasks.indexOf(taskToEdit)

                        if (index != -1) {
                            tasks[index] = updatedTask
                        }

                        taskToEdit = null

                        currentScreen = "todo"
                    }
                )
            }
        }


        // PROFILE
        "profile" -> {

            ProfileScreen(

                profileImageUri = profileImageUri,

                onImageSelected = {
                    profileImageUri = it
                },

                onBack = {
                    currentScreen = "todo"
                },

                onLogout = {

                    selectedTask = null
                    taskToEdit = null

                    currentScreen = "login"
                }
            )
        }
    }


    // -----------------------------
    // TASK POPUP
    // -----------------------------

    if (selectedTask != null) {

        TaskPopup(

            task = selectedTask!!,

            onDismiss = {
                selectedTask = null
            },

            onComplete = {

                val index = tasks.indexOf(selectedTask)

                if (index != -1) {

                    tasks[index] =
                        tasks[index].copy(
                            completed = true
                        )
                }

                selectedTask = null
            },

            onDelete = {

                tasks.remove(selectedTask)

                selectedTask = null
            },

            onEdit = {

                taskToEdit = selectedTask

                selectedTask = null

                currentScreen = "editTask"
            }
        )
    }
}


// =====================================================
// LOGIN SCREEN
// =====================================================

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Task ToDo",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )


        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        Button(
            onClick = {
                onLoginSuccess()
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Login")
        }


        Spacer(
            modifier = Modifier.height(15.dp)
        )


        TextButton(
            onClick = {
                onSignUpClick()
            }
        ) {

            Text("Don't have an account? Sign Up")
        }
    }
}


// =====================================================
// SIGN UP SCREEN
// =====================================================

@Composable
fun SignUpScreen(
    onLoginClick: () -> Unit
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge
        )


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        OutlinedTextField(
            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        OutlinedTextField(
            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(12.dp)
        )


        OutlinedTextField(
            value = confirmPassword,

            onValueChange = {
                confirmPassword = it
            },

            label = {
                Text("Confirm Password")
            },

            visualTransformation =
                PasswordVisualTransformation(),

            modifier = Modifier.fillMaxWidth()
        )


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        Button(
            onClick = {
                onLoginClick()
            },

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Create Account")
        }


        TextButton(
            onClick = {
                onLoginClick()
            }
        ) {

            Text("Already have an account? Login")
        }
    }
}


// =====================================================
// MAIN TASK SCREEN
// =====================================================

@Composable
fun MainTaskScreen(
    tasks: List<Task>,
    selectedPage: Int,

    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onCompletedClick: () -> Unit,
    onProfileClick: () -> Unit,

    onTaskClick: (Task) -> Unit
) {

    val visibleTasks = if (selectedPage == 2) {

        tasks.filter {
            it.completed
        }

    } else {

        tasks.filter {
            !it.completed
        }
    }


    Scaffold(

        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 20.dp
                    ),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        if (selectedPage == 2)
                            "Completed Tasks"
                        else
                            "My Tasks",

                    style =
                        MaterialTheme.typography.headlineMedium
                )


                IconButton(
                    onClick = {
                        onProfileClick()
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Person,

                        contentDescription =
                            "Profile"
                    )
                }
            }
        },


        bottomBar = {

            BottomAppBar {

                // HOME
                IconButton(
                    onClick = {
                        onHomeClick()
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home"
                        )

                        Text("Home")
                    }
                }


                // ADD
                IconButton(
                    onClick = {
                        onAddClick()
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add"
                        )

                        Text("Add")
                    }
                }


                // COMPLETED
                IconButton(
                    onClick = {
                        onCompletedClick()
                    },

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Completed"
                        )

                        Text("Done")
                    }
                }
            }
        }
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {


            if (visibleTasks.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (selectedPage == 2)
                                "No completed tasks"
                            else
                                "No tasks yet"
                    )
                }

            } else {

                visibleTasks.forEach { task ->

                    TaskItem(

                        task = task,

                        onClick = {
                            onTaskClick(task)
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )
                }
            }
        }
    }
}


// =====================================================
// TASK ITEM
// =====================================================

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape =
            RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp)
        ) {

            Text(
                text = task.name,

                style =
                    MaterialTheme.typography.titleMedium
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            Text(
                text =
                    "Date: ${task.date}"
            )


            Text(
                text =
                    "Time: ${task.time}"
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            Text(
                text =
                    if (task.completed)
                        "Completed"
                    else
                        "Pending"
            )
        }
    }
}


// =====================================================
// TASK POPUP
// =====================================================

@Composable
fun TaskPopup(
    task: Task,

    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(task.name)
        },

        text = {

            Column {

                Text("Date: ${task.date}")

                Text("Time: ${task.time}")

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    "Status: " +
                            if (task.completed)
                                "Completed"
                            else
                                "Pending"
                )
            }
        },


        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        },


        dismissButton = {

            Row {

                if (!task.completed) {

                    TextButton(
                        onClick = onComplete
                    ) {

                        Text("Complete")
                    }
                }


                TextButton(
                    onClick = onEdit
                ) {

                    Text("Edit")
                }


                TextButton(
                    onClick = onDelete
                ) {

                    Text("Delete")
                }
            }
        }
    )
}


// =====================================================
// ADD TASK SCREEN
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onBack: () -> Unit,
    onSave: (Task) -> Unit
) {

    var taskName by remember {
        mutableStateOf("")
    }

    var selectedDate by remember {
        mutableStateOf<Long?>(null)
    }

    var selectedTime by remember {
        mutableStateOf<String?>(null)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Add Task",

            style =
                MaterialTheme.typography.headlineMedium
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        OutlinedTextField(

            value = taskName,

            onValueChange = {
                taskName = it
            },

            label = {
                Text("Task Name")
            },

            modifier =
                Modifier.fillMaxWidth()
        )


        Spacer(
            modifier =
                Modifier.height(15.dp)
        )


        Button(
            onClick = {
                showDatePicker = true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                if (selectedDate == null)
                    "Select Date"
                else
                    "Date Selected"
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Button(
            onClick = {
                showTimePicker = true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text(
                selectedTime ?: "Select Time"
            )
        }


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        Button(
            onClick = {

                if (
                    taskName.isNotBlank() &&
                    selectedDate != null &&
                    selectedTime != null
                ) {

                    val formattedDate =
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(
                            Date(selectedDate!!)
                        )


                    val newTask = Task(

                        name = taskName,

                        date = formattedDate,

                        time = selectedTime!!
                    )


                    onSave(newTask)
                }
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Save Task")
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        TextButton(
            onClick = onBack
        ) {

            Text("Cancel")
        }
    }


    // DATE PICKER

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()


        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        selectedDate =
                            datePickerState.selectedDateMillis

                        showDatePicker = false
                    }
                ) {

                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showDatePicker = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }


    // TIME PICKER

    if (showTimePicker) {

        val timePickerState =
            rememberTimePickerState()


        AlertDialog(

            onDismissRequest = {
                showTimePicker = false
            },

            title = {
                Text("Select Time")
            },

            text = {

                TimePicker(
                    state = timePickerState
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        selectedTime =
                            String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            )

                        showTimePicker = false
                    }
                ) {

                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showTimePicker = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}


// =====================================================
// EDIT TASK SCREEN
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    task: Task,

    onBack: () -> Unit,

    onSave: (Task) -> Unit
) {

    var taskName by remember {
        mutableStateOf(task.name)
    }

    var selectedDate by remember {
        mutableStateOf<Long?>(null)
    }

    var selectedTime by remember {
        mutableStateOf(task.time)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }


    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp)
    ) {

        Text(
            text = "Edit Task",

            style =
                MaterialTheme.typography.headlineMedium
        )


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        OutlinedTextField(

            value = taskName,

            onValueChange = {
                taskName = it
            },

            label = {
                Text("Task Name")
            },

            modifier =
                Modifier.fillMaxWidth()
        )


        Spacer(
            modifier =
                Modifier.height(15.dp)
        )


        Button(

            onClick = {
                showDatePicker = true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Change Date")
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Button(

            onClick = {
                showTimePicker = true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Change Time")
        }


        Spacer(
            modifier =
                Modifier.height(25.dp)
        )


        Button(

            onClick = {

                val newDate =

                    if (selectedDate != null) {

                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(
                            Date(selectedDate!!)
                        )

                    } else {

                        task.date
                    }


                onSave(

                    Task(

                        name = taskName,

                        date = newDate,

                        time = selectedTime,

                        completed = task.completed
                    )
                )
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Text("Save Changes")
        }


        TextButton(
            onClick = onBack
        ) {

            Text("Cancel")
        }
    }


    // DATE PICKER

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()


        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        selectedDate =
                            datePickerState.selectedDateMillis

                        showDatePicker = false
                    }
                ) {

                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showDatePicker = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }


    // TIME PICKER

    if (showTimePicker) {

        val timePickerState =
            rememberTimePickerState()


        AlertDialog(

            onDismissRequest = {
                showTimePicker = false
            },

            title = {
                Text("Change Time")
            },

            text = {

                TimePicker(
                    state = timePickerState
                )
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        selectedTime =
                            String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                timePickerState.hour,
                                timePickerState.minute
                            )

                        showTimePicker = false
                    }
                ) {

                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showTimePicker = false
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}


// =====================================================
// PROFILE SCREEN
// =====================================================

@Composable
fun ProfileScreen(

    profileImageUri: Uri?,

    onImageSelected: (Uri) -> Unit,

    onBack: () -> Unit,

    onLogout: () -> Unit
) {

    val imagePickerLauncher =
        rememberLauncherForActivityResult(

            contract =
                ActivityResultContracts.GetContent()

        ) { uri ->

            if (uri != null) {

                onImageSelected(uri)
            }
        }


    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(25.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        // TOP

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text("Back")
            }


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            Text(
                text = "Profile",

                style =
                    MaterialTheme.typography.headlineMedium
            )


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            IconButton(
                onClick = onLogout
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Logout,

                    contentDescription =
                        "Logout"
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(50.dp)
        )


        // PROFILE IMAGE

        if (profileImageUri == null) {

            Box(

                modifier =
                    Modifier
                        .size(140.dp)
                        .clip(CircleShape),

                contentAlignment =
                    Alignment.Center
            ) {

                Surface(
                    modifier =
                        Modifier.fillMaxSize(),

                    shape =
                        CircleShape,

                    color =
                        MaterialTheme.colorScheme
                            .surfaceVariant
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Person,

                            contentDescription =
                                "Profile",

                            modifier =
                                Modifier.size(70.dp)
                        )
                    }
                }
            }

        } else {

            val bitmap =
                remember(profileImageUri) {

                    try {

                        val inputStream =
                            androidx.compose.ui.platform
                                .LocalContext
                                .current
                                .contentResolver
                                .openInputStream(
                                    profileImageUri
                                )

                        BitmapFactory
                            .decodeStream(inputStream)
                            ?.asImageBitmap()

                    } catch (e: Exception) {

                        null
                    }
                }


            if (bitmap != null) {

                Image(

                    bitmap = bitmap,

                    contentDescription =
                        "Profile Picture",

                    modifier =
                        Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                )

            } else {

                Icon(
                    imageVector =
                        Icons.Default.Person,

                    contentDescription =
                        "Profile",

                    modifier =
                        Modifier.size(100.dp)
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        // CHANGE PHOTO BUTTON

        Button(

            onClick = {

                imagePickerLauncher.launch(
                    "image/*"
                )
            }
        ) {

            Icon(
                imageVector =
                    Icons.Default.PhotoCamera,

                contentDescription =
                    "Change Photo"
            )


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            Text("Change Profile Photo")
        }


        Spacer(
            modifier =
                Modifier.height(40.dp)
        )


        Text(
            text = "My Profile",

            style =
                MaterialTheme.typography
                    .headlineSmall
        )


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        Text(
            text = "Task ToDo User"
        )


        Spacer(
            modifier =
                Modifier.height(40.dp)
        )


        // LOGOUT BUTTON

        OutlinedButton(

            onClick = onLogout,

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector =
                    Icons.Default.Logout,

                contentDescription =
                    "Logout"
            )


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            Text("Logout")
        }
    }
}