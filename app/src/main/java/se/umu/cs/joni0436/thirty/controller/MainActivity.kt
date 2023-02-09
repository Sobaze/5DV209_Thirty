package se.umu.cs.joni0436.thirty.controller



import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import se.umu.cs.joni0436.thirty.databinding.ActivityMainBinding
import se.umu.cs.joni0436.thirty.model.Dice
import se.umu.cs.joni0436.thirty.model.Game
import se.umu.cs.joni0436.thirty.model.GameState

//Log.d (TAG, ”In the onStart() event”)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var diceImages: ArrayList<ImageView>
    private lateinit var game: Game
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
            savedInstanceState.getParcelable(GAME_SERVICE) ?: Game()
        } else {
            Game()
        }

        buttonListener()
        diceImageListener()
    }

    private fun buttonListener() {
        binding.rollButton.setOnClickListener{
            game.gameState == GameState.ROLLING_STATE
            game.rollDices()
            simulateDiceRollsChanging()
            //Toast.makeText(this, "Du rollade tärningen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun diceImageListener() {
        val allDies = game.dices
        for ((i, currentDice: Dice) in allDies.withIndex()) {
            diceImages[i].setOnClickListener{
                if( game.gameState !== GameState.GAME_COMPLETE_STATE) {
                    game.dices[i].toggleDice()
                    checkIfDiceIsSelected(diceImages[i], currentDice.getDiceValue(), currentDice.isDiceSelected)
                }

            }
        }
    }

    // get dices, then for each dice in dices roll them and get a value
    private fun simulateDiceRollsChanging () {
        val allDies = game.dices
        for((i, currentDice: Dice) in allDies.withIndex()) {
            val diceImageForCurrentDice = diceImages[i]

            checkIfDiceIsSelected(diceImageForCurrentDice, currentDice.getDiceValue(), currentDice.isDiceSelected)
        }
    }
    // check if the dice is selected and thus what color to pick for next function that updates it.
    private fun checkIfDiceIsSelected (img: ImageView, diceNumber: Int, isSelected: Boolean) {
        when (isSelected) {
            true -> updateDiceChanges(img, diceNumber, "grey")
            false -> updateDiceChanges(img, diceNumber, "white")
        }
    }
    // Updates the dice image with correct color
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
}