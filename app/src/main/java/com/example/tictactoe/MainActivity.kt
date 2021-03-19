package com.example.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // Represents the internal state of the game
    var mGame = TicTacToeGame()

    // Buttons making up the board
    private lateinit var mBoardButtons: Array<Button?>

    // Game Over
    var mGameOver = false

    var mMode = 1

    var lastWinner = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Buttons making up the board
        mBoardButtons = arrayOf(button0, button1, button2, button3, button4, button5, button6, button7, button8)
        startNewGame()
    }

    //--- Set up the game board.
    fun startNewGame() {
        mGameOver = false
        mGame.clearBoard()
        for(i in mBoardButtons.indices) {
            mBoardButtons[i]!!.text = ""
            mBoardButtons[i]!!.isEnabled = true
        }
        //---Human goes first
        if(lastWinner == 0) {
            information.text = "You go first."
        } else if(lastWinner == 1) {
            information.text = "Android goes first."
            var move = -1
            if(mMode == 0) {
                move = mGame.easyComputerMove
            } else if(mMode == 1) {
                move = mGame.mediumComputerMove
            } else{
                move = mGame.mediumComputerMove
            }
            setMove(TicTacToeGame.COMPUTER_PLAYER, move)
        }
    }

    //--- OnClickListener for Restart a New Game Button
    fun newGame(view: View) {
        button_restart.setOnClickListener { startNewGame() }
    }

    // multiple button click method
    fun onButtonClicked(view: View) {
        val buSelected: Button = view as Button
        var location = 0
        when(buSelected.id) {
            R.id.button0 -> location = 0
            R.id.button1 -> location = 1
            R.id.button2 -> location = 2
            R.id.button3 -> location = 3
            R.id.button4 -> location = 4
            R.id.button5 -> location = 5
            R.id.button6 -> location = 6
            R.id.button7 -> location = 7
            R.id.button8 -> location = 8
        }
        if(mGameOver == false) {
            if(mBoardButtons[location]!!.isEnabled) {
                var winner = mGame.checkForWinner()
                var move = -1
                setMove(TicTacToeGame.HUMAN_PLAYER, location)
                if (winner == 0) {
                    information.text = "It's Android's turn"
                    if(mMode == 0) {
                        move = mGame.easyComputerMove
                    } else if(mMode == 1) {
                        move = mGame.mediumComputerMove
                    } else{
                        move = mGame.mediumComputerMove
                    }
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                    winner = mGame.checkForWinner()
                }
                if (winner == 0) {
                    information.setTextColor(Color.rgb(0, 0, 0))
                    information.text = "It's your turn (X)."
                }
                if (winner == 1) {
                    information.setTextColor(Color.rgb(0, 0, 200))
                    lastWinner = (0..1).random()
                    information.text = "It's a tie!"
                    mGameOver = true
                } else if (winner == 2) {
                    information.setTextColor(Color.rgb(0, 200, 0))
                    lastWinner = 0
                    information.text = "You won!"
                    mGameOver = true
                } else if (winner == 3) {
                    information.setTextColor(Color.rgb(200, 0, 0))
                    lastWinner = 1
                    information.text = "Android won!"
                    mGameOver = true
                }
            }
        }
    }

    private fun setMove(player: Char, location: Int) {
        mGame!!.setMove(player, location)
        mBoardButtons[location]!!.isEnabled = false
        mBoardButtons[location]!!.text = player.toString()
        if(player == TicTacToeGame.HUMAN_PLAYER) {
            mBoardButtons[location]!!.setTextColor(Color.parseColor("#ff0000"))
        } else {
            mBoardButtons[location]!!.setTextColor(Color.parseColor("#00ff00"))
        }
    }

    fun easyMode(view: View) {
        mMode = 0
        startNewGame()
        Toast.makeText(this, "Easy mode is selected", Toast.LENGTH_SHORT).show()
    }
    fun mediumMode(view: View) {
        mMode = 1
        startNewGame()
        Toast.makeText(this, "Medium mode is selected", Toast.LENGTH_SHORT).show()
    }
    fun hardMode(view: View) {
        mMode = 2
        startNewGame()
        Toast.makeText(this, "Hard mode is selected", Toast.LENGTH_SHORT).show()
    }
}