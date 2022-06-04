package com.example.sallyandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sallyandroid.ui.theme.SallyandroidTheme
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SallyandroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    DemoScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello ${name}!")
}

@Composable
fun DemoScreen(viewModel: DemoScreenViewModel = viewModel()) {
    val uiState = viewModel.uiState.text
    Greeting(name = uiState)
}

data class DemoScreenViewState(
    val text: String = "initial"
)

class DemoScreenViewModel : ViewModel() {

    var uiState by mutableStateOf(DemoScreenViewState())
        private set

    init {
        viewModelScope.launch {
            val data = getData()
            uiState = DemoScreenViewState(data)
        }
    }

    private fun getData(): String {

        //MAGICAL channel for local host
        val managedChannel = ManagedChannelBuilder.forAddress("10.0.2.2", 9090)
            .usePlaintext()
            .build()

        val blockStub: SallyServiceGrpc.SallyServiceBlockingStub =
            SallyServiceGrpc.newBlockingStub(managedChannel)

        val getRequest = GetSallyRequest.newBuilder().setFoo("someFoo").build()

        val data = blockStub.getSally(getRequest)
        return data.bar
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SallyandroidTheme {
        Greeting("Android")
    }
}