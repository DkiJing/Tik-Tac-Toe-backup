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
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.activity_two_player.*

class TwoPlayerActivity : AppCompatActivity() {
    private val model: TwoPlayerViewModel by viewModels()

    private lateinit var settpref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_player)
        model.mBoardButtons = arrayOf(button0, button1, button2, button3, button4, button5, button6, button7, button8)
        model.loadBoard()
        information.text = model.mInfo
        initSettingPrefferences()
        loadPreferences()
        model.mCreatedCounter += 1
        if(model.mCreatedCounter <= 1) {
            startNewGame()
        }
    }

    //--- Set up the game board.
    private fun startNewGame() {
        model.mGameOver = false
        model.mGame.clearBoard()
        for(i in model.mBoardButtons.indices) {
            model.mBoardButtons[i]!!.text = ""
            model.mBoardButtons[i]!!.isEnabled = true
        }
        information.text = getString(R.string.x_turns_first)
        model.saveBoard()
        model.mInfo = information.text as String
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
        if(!model.mGameOver) {
            if(model.mBoardButtons[location]!!.isEnabled) {
                var winner = model.mGame.checkForWinner()
                if (winner == 0) {
                    if (model.hasMoved) {
                        if(information.text == getString(R.string.o_turn)) {
                            setMove(TicTacToeGame.COMPUTER_PLAYER, location)
                            information.text = getString(R.string.x_turn)
                            model.hasMoved = false
                        } else if(information.text == getString(R.string.x_turn)) {
                            setMove(TicTacToeGame.HUMAN_PLAYER, location)
                            information.text = getString(R.string.o_turn)
                            model.hasMoved = false
                        }
                    }
                    if (information.text == getString(R.string.x_turns_first)) {
                        setMove(TicTacToeGame.HUMAN_PLAYER, location)
                        information.text = getString(R.string.o_turn)
                        information.setTextColor(Color.rgb(0, 0, 0))
                    }
                    model.hasMoved = true
                }
                winner = model.mGame.checkForWinner()
                when (winner) {
                    1 -> {
                        information.setTextColor(Color.rgb(0, 0, 200))
                        model.lastWinner = (0..1).random()
                        information.text = getString(R.string.tie_rst)
                        model.x_wins = x_score.text.toString().toInt()
                        model.o_wins = o_score.text.toString().toInt()
                        model.ties = tie_score.text.toString().toInt()
                        model.ties += 1
                        savePreferences(model.x_wins.toString(), model.o_wins.toString(), model.ties.toString())
                        loadPreferences()
                        model.mGameOver = true
                    }
                    2 -> {
                        information.setTextColor(Color.rgb(0, 200, 0))
                        model.lastWinner = 0
                        information.text = getString(R.string.x_rst)
                        model.x_wins = x_score.text.toString().toInt()
                        model.o_wins = o_score.text.toString().toInt()
                        model.ties = tie_score.text.toString().toInt()
                        model.x_wins += 1
                        savePreferences(model.x_wins.toString(), model.o_wins.toString(), model.ties.toString())
                        loadPreferences()
                        model.mGameOver = true
                    }
                    3 -> {
                        information.setTextColor(Color.rgb(200, 0, 0))
                        model.lastWinner = 1
                        information.text = getString(R.string.o_rst)
                        model.x_wins = x_score.text.toString().toInt()
                        model.o_wins = o_score.text.toString().toInt()
                        model.ties = tie_score.text.toString().toInt()
                        model.o_wins += 1
                        savePreferences(model.x_wins.toString(), model.o_wins.toString(), model.ties.toString())
                        loadPreferences()
                        model.mGameOver = true
                    }
                }
            }
            model.saveBoard()
            model.mInfo = information.text as String
        }
    }

    private fun setMove(player: Char, location: Int) {
        model.mGame.setMove(player, location)
        model.mBoardButtons[location]!!.isEnabled = false
        model.mBoardButtons[location]!!.text = player.toString()
        if(player == TicTacToeGame.HUMAN_PLAYER) {
            model.mBoardButtons[location]!!.setTextColor(Color.parseColor("#ff0000"))
        } else {
            model.mBoardButtons[location]!!.setTextColor(Color.parseColor("#00ff00"))
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

    private fun openWebPage(urls: String, context : Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        context.startActivity(intents)
    }
}