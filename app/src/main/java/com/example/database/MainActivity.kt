package com.example.database

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.ui.theme.ClientTheme

class MainActivity : ComponentActivity() {

    private var studentService: IStudentAPI? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            studentService = IStudentAPI.Stub.asInterface(p1)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            studentService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientTheme {
                HomeScreen {  }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent("istudentapi")
        val pack = IStudentAPI::class.java.`package`
        pack?.let {
            Log.d("SonLN", "onStart: $pack")
            intent.setPackage(it.name)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }
}

@Composable
fun HomeScreen(onButtonClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ButtonItem(text = "Get ALL", onClick = { onButtonClick("Get ALL") })
        ButtonItem(text = "Top 10 by subject", onClick = { onButtonClick("Top 10 by subject") })
        ButtonItem(text = "Top 10 by SumA", onClick = { onButtonClick("Top 10 by SumA") })
        ButtonItem(text = "Top 10 by SumB", onClick = { onButtonClick("Top 10 by SumB") })
        ButtonItem(text = "Search", onClick = { onButtonClick("Search") })
    }
}

@Composable
fun ButtonItem(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .padding(vertical = 8.dp)
            .height(50.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClientTheme {
        Greeting("Android")
    }
}