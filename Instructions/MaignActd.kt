package com.euphoria.ballneonogame.gsde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import com.euphoria.ballneonogame.Musdfd
import com.euphoria.ballneonogame.uiasf.theme.PlinkoGameTheme

class MaignActd : ComponentActivity() {
    
    private var musdfd: Musdfd? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        musdfd = Musdfd(applicationContext)
        musdfd?.start()
        
        enableEdgeToEdge()
        setContent {
            PlinkoGameTheme {
                PlksafScressa(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                )
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        musdfd?.pause()
    }
    
    override fun onResume() {
        super.onResume()
        musdfd?.resume()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        musdfd?.release()
        musdfd = null
    }
}
