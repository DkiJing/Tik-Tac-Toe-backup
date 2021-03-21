package com.example.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.activity_two_player.*

class TwoPlayerActivity : AppCompatActivity() {

    // Represents the internal state of the game
    var mGame = TicTacToeGame()

    // Buttons making up the board
    private lateinit var mBoardButtons: Array<Button?>

    // Game Over
    var mGameOver = false

    lateinit var settpref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private var lastWinner = 0
    private var o_wins = 0
    private var x_wins = 0
    private var ties = 0
    private var hasMoved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_player)
        mBoardButtons = arrayOf(button0, button1, button2, button3, button4, button5, button6, button7, button8)
        initSettingPrefferences()
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
        information.text = getString(R.string.x_turns_first)
        loadPreferences()
    }

    //--- OnClickListener for Restart a New Game Button
    fun newGame(view: View) {
        button_restart.setOnClickListener { startNewGame() }
    }

    @SuppressLint("CommitPrefEdits")
    private fun initSettingPrefferences() {
        settpref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        editor = settpref.edit()
    }

    private fun savePreferences(x: String?, o: String?, t: String) {
        val pref = getSharedPreferences("TWOPLAYERSCORE", Context.MODE_PRIVATE)
        pref.edit().putString("x", x).apply()
        pref.edit().putString("o", o).apply()
        pref.edit().putString("tie", t).apply()
    }

    private fun loadPreferences() {
        val pref = getSharedPreferences("TWOPLAYERSCORE", Context.MODE_PRIVATE)
        x_score.text = pref.getString("x", "0")
        o_score.text = pref.getString("o", "0")
        tie_score.text = pref.getString("tie", "0")
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
                if (winner == 0) {
                    if (hasMoved == true) {
                        if(information.text == getString(R.string.o_turn)) {
                            setMove(TicTacToeGame.COMPUTER_PLAYER, location)
                            information.text = getString(R.string.x_turn)
                            hasMoved = false
                        } else if(information.text == getString(R.string.x_turn)) {
                            setMove(TicTacToeGame.HUMAN_PLAYER, location)
                            information.text = getString(R.string.o_turn)
                            hasMoved = false
                        }
                    }
                    if (information.text == getString(R.string.x_turns_first)) {
                        setMove(TicTacToeGame.HUMAN_PLAYER, location)
                        information.text = getString(R.string.o_turn)
                        information.setTextColor(Color.rgb(0, 0, 0))
                    }
                    hasMoved = true
                }
                winner = mGame.checkForWinner()
                if (winner == 1) {
                    information.setTextColor(Color.rgb(0, 0, 200))
                    lastWinner = (0..1).random()
                    information.text = getString(R.string.tie_rst)
                    x_wins = x_score.text.toString().toInt()
                    o_wins = o_score.text.toString().toInt()
                    ties = tie_score.text.toString().toInt()
                    ties += 1
                    savePreferences(x_wins.toString(), o_wins.toString(), ties.toString())
                    loadPreferences()
                    mGameOver = true
                } else if (winner == 2) {
                    information.setTextColor(Color.rgb(0, 200, 0))
                    lastWinner = 0
                    information.text = getString(R.string.x_rst)
                    x_wins = x_score.text.toString().toInt()
                    o_wins = o_score.text.toString().toInt()
                    ties = tie_score.text.toString().toInt()
                    x_wins += 1
                    savePreferences(x_wins.toString(), o_wins.toString(), ties.toString())
                    loadPreferences()
                    mGameOver = true
                } else if (winner == 3) {
                    information.setTextColor(Color.rgb(200, 0, 0))
                    lastWinner = 1
                    information.text = getString(R.string.o_rst)
                    x_wins = x_score.text.toString().toInt()
                    o_wins = o_score.text.toString().toInt()
                    ties = tie_score.text.toString().toInt()
                    o_wins += 1
                    savePreferences(x_wins.toString(), o_wins.toString(), ties.toString())
                    loadPreferences()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_settings -> {
                showSettings()
                return true
            }
            R.id.action_about -> {
                openWebPage(getString(R.string.wiki_uri), this)
                return true
            }
            R.id.menu_exit -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettings() {
        val dialog = MaterialDialog(this).noAutoDismiss().customView(R.layout.settings)
        val mode = settpref
        val audio = settpref.getString("audio", "off")
        // audio settings initialization
        if(audio == "on") {
            dialog.findViewById<RadioGroup>(R.id.audio_group).check(R.id.turn_on_audio)
        } else {
            dialog.findViewById<RadioGroup>(R.id.audio_group).check(R.id.turn_off_audio)
        }
        // get new preferences
        // Apply
        dialog.findViewById<TextView>(R.id.positive_button).setOnClickListener {
            val selectedMode = if(dialog.findViewById<RadioGroup>(R.id.play_mode_group).checkedRadioButtonId ==
                    dialog.findViewById<RadioButton>(R.id.one_player).id
            ) "one" else "two"
            val selectedAudio = if(dialog.findViewById<RadioGroup>(R.id.audio_group).checkedRadioButtonId ==
                    dialog.findViewById<RadioButton>(R.id.turn_on_audio).id
            ) "on" else "off"
            dialog.dismiss()
            if(selectedMode == "one") {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            editor.putString("mode", selectedMode).apply()
            editor.putString("audio", selectedAudio).apply()
        }
        // Cancel
        dialog.findViewById<TextView>(R.id.negative_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun openWebPage(urls: String, context : Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        context.startActivity(intents)
    }
}