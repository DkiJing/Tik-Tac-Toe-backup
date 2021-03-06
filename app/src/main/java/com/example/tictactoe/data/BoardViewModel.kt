package com.example.tictactoe.data

import android.graphics.Color
import android.media.MediaPlayer
import android.widget.Button
import androidx.lifecycle.ViewModel
import com.example.tictactoe.controller.TicTacToeGame

class BoardViewModel : ViewModel() {
    // Represents the internal state of the game
    var mGame = TicTacToeGame()
    // Buttons making up the board
    lateinit var mBoardButtons: Array<Button?>
    lateinit var mediaPlayer: MediaPlayer
    var mBoardText: Array<String> = arrayOf("", "", "", "", "", "", "", "", "")
    var mBoardStates: Array<Boolean> = arrayOf(false, false, false, false, false, false, false, false, false)
    // Game Over
    var mGameOver = false
    var mCreatedCounter = 0
    var mMode = 1
    var lastWinner = 0
    var player_wins = 0
    var computer_wins = 0
    var ties = 0
    var mInfo = "info"
    var mAudioOn = false

    fun saveBoard() {
        for(i in mBoardButtons.indices) {
            mBoardText[i] = mBoardButtons[i]!!.text as String
            mBoardStates[i] = mBoardButtons[i]!!.isEnabled
        }
    }

    fun loadBoard() {
        for(i in mBoardButtons.indices) {
            mBoardButtons[i]!!.text = mBoardText[i]
            mBoardButtons[i]!!.isEnabled = mBoardStates[i]
            if(mBoardText[i] == "X") {
                mBoardButtons[i]!!.setTextColor(Color.parseColor("#ff0000"))
            } else if(mBoardText[i] == "O") {
                mBoardButtons[i]!!.setTextColor(Color.parseColor("#00ff00"))
            }
        }
    }
}