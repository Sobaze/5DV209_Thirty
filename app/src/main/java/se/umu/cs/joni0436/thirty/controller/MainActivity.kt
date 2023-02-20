package se.umu.cs.joni0436.thirty.controller

/**
 * Only implemented Ativity to be run in portrait view
 */

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import se.umu.cs.joni0436.thirty.R
import se.umu.cs.joni0436.thirty.databinding.ActivityMainBinding
import se.umu.cs.joni0436.thirty.model.Dice
import se.umu.cs.joni0436.thirty.model.Game
import se.umu.cs.joni0436.thirty.model.GameState

private const val GAME_STATE = "se.umu.cs.joni0436.thirty.stateKey"
//Log.d (TAG, ”In the onStart() event”)
/**
 * Main Activity class that handles the game of rolling dices
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var diceImages: ArrayList<ImageView>
    private lateinit var game: Game

    /**
     * ResultLauncher handling the starting of the gameResultActivity
     */
    private val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        game.newGame()
        updateAll()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        diceImages = arrayListOf(
            binding.dice1,
            binding.dice2,
            binding.dice3,
            binding.dice4,
            binding.dice5,
            binding.dice6
        )
        game = if (savedInstanceState != null) {
            savedInstanceState.getParcelable(GAME_STATE) ?: Game()
        } else {
            Game()
        }
        updateAll()
        buttonListener()
        diceImageListener()
    }

    /**
     * button event listener for starting game/ rolling the dices / and picking score
     */
    private fun buttonListener() {
        binding.rollButton.setOnClickListener{

            when(game.gameState) {
                GameState.GAME_START -> {
                    game.gameState = GameState.ROLLING_STATE
                    updateButton()
                }
                // User can roll dices
                GameState.ROLLING_STATE -> {
                    game.rollDices()
                    // User can pick score from the dices
                    if(game.gameState == GameState.PICKING_SCORE_STATE) {
                        Toast.makeText(this, "Time to select score combo", Toast.LENGTH_SHORT).show()
                        updateButton()
                    }
                    updateDices()
                }
                // User can pick score from the dices
                GameState.PICKING_SCORE_STATE -> {
                    try {
                        game.pickScoreForRound(
                            binding.spinner.getItemAtPosition(binding.spinner.selectedItemPosition)
                                .toString()
                        )
                        updateAll()
                    } catch (error: IllegalArgumentException) {
                        Log.d("error", "${error.message}")
                        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                    }
                    if(game.gameState == GameState.GAME_COMPLETE_STATE) {
                      resultLauncher.launch(GameResultActivity.newIntent(this, game.scorePerRound))
                    }
                    // Game is over and we get to scoreboard
                } GameState.GAME_COMPLETE_STATE -> {
                    resultLauncher.launch(GameResultActivity.newIntent(this, game.scorePerRound))
                }
            }
        }
    }

    /**
     * sets up listener for dice images
     */
    private fun diceImageListener() {
        val allDies = game.dices
        for ((i, currentDice: Dice) in allDies.withIndex()) {
            diceImages[i].setOnClickListener{
                if( game.gameState !== GameState.GAME_COMPLETE_STATE) {
                    game.dices[i].toggleDice()
                    checkIfDiceIsSelected(diceImages[i], currentDice.diceVal, currentDice.isDiceSelected)
                }

            }
        }
    }

    /**
     * gets dices then for each dice in dices roll them and get a value
     */
    private fun updateDices () {
        val allDies = game.dices
        for((i, currentDice: Dice) in allDies.withIndex()) {
            val diceImageForCurrentDice = diceImages[i]

            checkIfDiceIsSelected(diceImageForCurrentDice, currentDice.diceVal, currentDice.isDiceSelected)
        }
    }

    /**
     * check if the dice is selected and thus what color to pick for next function that updates it.
     */
    private fun checkIfDiceIsSelected (img: ImageView, diceNumber: Int, isSelected: Boolean) {
        when (isSelected) {
            true -> updateDiceChanges(img, diceNumber, "grey")
            false -> updateDiceChanges(img, diceNumber, "white")
        }
    }

    /**
     * Updates dice with the correct color.
     *
     */
    private fun updateDiceChanges (img: ImageView, diceNumber: Int, color: String) {
        val res: Resources = resources
        val dicePicturePath: String = color + diceNumber.toString()
        val resID: Int = res.getIdentifier(dicePicturePath, "drawable", packageName)

        when(diceNumber) {
               1 -> img.setImageResource(resID)
               2 -> img.setImageResource(resID)
               3 -> img.setImageResource(resID)
               4 -> img.setImageResource(resID)
               5 -> img.setImageResource(resID)
               6 -> img.setImageResource(resID)
        }
    }

    /**
     * Update spinner view
     */
    private fun updateSpinner() {
        binding.spinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            game.allChoices
        )
    }

    /**
     * updates button
     */
    private fun updateButton() {
        if (game.gameState === GameState.ROLLING_STATE) {
            binding.rollButton.setText(R.string.roll_button)
        } else if (game.gameState === GameState.PICKING_SCORE_STATE) {
            binding.rollButton.setText(R.string.score_button)
        } else if ( game.gameState == GameState.GAME_START) {
            binding.rollButton.setText((R.string.start_button))
        }
    }

    /**
     * updates everything that needs updates
     * that is spinner, dices and button.
     */
    private fun updateAll () {
        updateSpinner()
        updateDices()
        updateButton()
    }


    override fun onSaveInstanceState(outState: Bundle ) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(GAME_STATE, game)
    }
}