package se.umu.cs.joni0436.thirty.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import se.umu.cs.joni0436.thirty.R
import se.umu.cs.joni0436.thirty.databinding.ActivityGameResultBinding
import se.umu.cs.joni0436.thirty.model.Round

/**
 * Activity class for handling the results showing.
 */
class GameResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rounds =intent.getParcelableArrayListExtra<Round>(EXTRA_ROUNDS)

        if (rounds != null) {
            scorelist(rounds)
            inputTotalScore(rounds)
            restartGameButtonListener()
        }
    }

    /**
     * sets up a listener on the button to restart the game
     */
    private fun restartGameButtonListener() {
        binding.newGameButton.setOnClickListener{
            finish()
        }
    }

    /**
    * counts out the total score for the game and puts it in
     */
    private fun inputTotalScore(rounds: ArrayList<Round>) {
        var totalScore = 0
        rounds.forEach { round ->
            totalScore += round.scoreCombinations.sum()
        }
        binding.myTotalScore.text = getString(R.string.my_total_score, totalScore)
    }

    // Creates a score list that shows every round and what combinations were used.
    private fun scorelist (rounds:ArrayList<Round>) {
        binding.roundOverview.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            rounds.mapIndexed { i, (userChoice, scorePerRound) ->
                getString(
                    R.string.score_for_rounds,
                    i + 1,
                    scorePerRound.sum(),
                    userChoice,
                    scorePerRound
                )
            }
        )
    }

    /**
      * Companion object providing a helper function.
     */
    companion object {
        private const val EXTRA_ROUNDS = "se.umu.cs.joni0436.thirty.rounds"

        fun newIntent(packageContext: Context, rounds: ArrayList<Round>): Intent {
            return Intent(packageContext, GameResultActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_ROUNDS, rounds)
            }
        }
    }
}