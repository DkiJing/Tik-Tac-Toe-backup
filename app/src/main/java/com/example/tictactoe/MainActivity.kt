package com.example.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val model: BoardViewModel by viewModels()

    private lateinit var settpref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Buttons making up the board
        model.mBoardButtons = arrayOf(button0, button1, button2, button3, button4, button5, button6, button7, button8)
        model.loadBoard()
        information.text = model.mInfo
        initSettingPreferences()
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
        //---Human goes first
        if(model.lastWinner == 0) {
            information.text = getString(R.string.user_turn_first)
        } else if(model.lastWinner == 1) {
            information.text = getString(R.string.computer_turn_first)
            var move = -1
            when (model.mMode) {
                0 -> {
                    move = model.mGame.easyComputerMove
                }
                1 -> {
                    move = model.mGame.mediumComputerMove
                }
                2 -> {
                    move = model.mGame.getHardComputerMove(model.lastWinner)
                }
            }
            setMove(TicTacToeGame.COMPUTER_PLAYER, move)
        }
        model.saveBoard()
        model.mInfo = information.text as String
        loadPreferences()
    }

    //--- OnClickListener for Restart a New Game Button
    fun newGame(view: View) {
        button_restart.setOnClickListener { startNewGame() }
    }

    fun playAudio(audio: Int) {
        when (audio) {
            0 -> {
                val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.place)
                model.mediaPlayer = MediaPlayer.create(applicationContext, uri)
                model.mediaPlayer.start()
            }
            1 -> {
                val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.win)
                model.mediaPlayer = MediaPlayer.create(applicationContext, uri)
                model.mediaPlayer.start()
            }
            2 -> {
                val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.lose)
                model.mediaPlayer = MediaPlayer.create(applicationContext, uri)
                model.mediaPlayer.start()
            }
        }
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
                setMove(TicTacToeGame.HUMAN_PLAYER, location)
                var winner = model.mGame.checkForWinner()
                var move = -1
                if (winner == 0) {
                    information.text = getString(R.string.computer_turn)
                    when (model.mMode) {
                        0 -> {
                            move = model.mGame.easyComputerMove
                        }
                        1 -> {
                            move = model.mGame.mediumComputerMove
                        }
                        2 -> {
                            move = model.mGame.getHardComputerMove(model.lastWinner)
                        }
                    }
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move)
                    winner = model.mGame.checkForWinner()
                }
                when (winner) {
                    0 -> {
                        information.setTextColor(Color.rgb(0, 0, 0))
                        information.text = getString(R.string.user_turn)
                    }
                    1 -> {
                        information.setTextColor(Color.rgb(0, 0, 200))
                        model.lastWinner = (0..1).random()
                        information.text = getString(R.string.tie_rst)
                        model.player_wins = player_score.text.toString().toInt()
                        model.computer_wins = computer_score.text.toString().toInt()
                        model.ties = tie_score.text.toString().toInt()
                        model.ties += 1
                        savePreferences(model.player_wins.toString(), model.computer_wins.toString(), model.ties.toString())
                        loadPreferences()
                        model.mGameOver = true
                    }
                    2 -> {
                        information.setTextColor(Color.rgb(0, 200, 0))
                        model.lastWinner = 0
                        information.text = getString(R.string.user_rst)
                        model.player_wins = player_score.text.toString().toInt()
                        model.computer_wins = computer_score.text.toString().toInt()
                        model.ties = tie_score.text.toString().toInt()
                        model.player_wins += 1
                        savePreferences(model.player_wins.toString(), model.computer_wins.toString(), model.ties.toString())
                        loadPreferences()
                        model.mGameOver = true
                        if(model.mAudioOn) {
                            playAudio(1)
                        }
                    }
                    3 -> {
                        information.setTextColor(Color.rgb(200, 0, 0))
                        model.lastWinner = 1
                        information.text = getString(R.string.computer_rst)
                        model.player_wins = player_score.text.toString().toInt()
                        model.computer_wins = computer_score.text.toString().toInt()
                        model.ties = tie_score.text.toString().toInt()
                        model.computer_wins += 1
                        savePreferences(model.player_wins.toString(), model.computer_wins.toString(), model.ties.toString())
                        loadPreferences()
                        model.mGameOver = true
                        if(model.mAudioOn) {
                            playAudio(2)
                        }
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
        if(model.mAudioOn) {
            playAudio(0)
        }
    }

    fun easyMode(view: View) {
        model.mMode = 0
        startNewGame()
    }
    fun mediumMode(view: View) {
        model.mMode = 1
        startNewGame()
    }
    fun hardMode(view: View) {
        model.mMode = 2
        startNewGame()
    }

    @SuppressLint("CommitPrefEdits")
    private fun initSettingPreferences() {
        settpref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        editor = settpref.edit()
        val audio = settpref.getString("audio", "off")
        if(audio == "on") {
            model.mAudioOn = true
        } else if(audio == "off") {
            model.mAudioOn = false
        }
    }

    private fun savePreferences(p: String?, a: String?, t: String) {
        val pref = getSharedPreferences("SCORE", Context.MODE_PRIVATE)
        pref.edit().putString("player", p).apply()
        pref.edit().putString("android", a).apply()
        pref.edit().putString("tie", t).apply()
    }

    private fun loadPreferences() {
        val pref = getSharedPreferences("SCORE", Context.MODE_PRIVATE)
        player_score.text = pref.getString("player", "0")
        computer_score.text = pref.getString("android", "0")
        tie_score.text = pref.getString("tie", "0")
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
            if(selectedMode == "two") {
                intent = Intent(this, TwoPlayerActivity::class.java)
                startActivity(intent)
                finish()
            }
            if(selectedAudio == "on") {
                model.mAudioOn = true
            } else if(selectedAudio == "off") {
                model.mAudioOn = false
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