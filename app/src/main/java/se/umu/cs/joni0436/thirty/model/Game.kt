package se.umu.cs.joni0436.thirty.model

import android.os.Parcel
import android.os.Parcelable

private const val AMOUNT_OF_ROUNDS = 10
private const val AMOUNT_OF_ROLLS = 3

class Game() : Parcelable {

    // import 6 dices from dice
    var dices: ArrayList<Dice> =
        arrayListOf(Dice(),Dice(),Dice(),Dice(),Dice(),Dice())

    var gameState: GameState = GameState.GAME_START
    // Choices a user can pick for scores after each round
    var allChoices: ArrayList<String> = arrayListOf(
        "LOW",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
    )
    var scorePerRound: ArrayList<Round> = ArrayList()
    var rolls: Int = 0
    var round: Int = 0

    // gamestate parcel read I got from https://stackoverflow.com/questions/38174961/how-to-read-and-write-enum-into-parcel-on-android
    constructor(parcel: Parcel) : this() {
        rolls = parcel.readInt()
        round = parcel.readInt()
        gameState = parcel.readString()?.let { GameState.valueOf(it) }!!

        scorePerRound = parcel.readArrayList(Round::class.java.classLoader) as ArrayList<Round>
        allChoices = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
        dices = parcel.readArrayList(Dice::class.java.classLoader) as ArrayList<Dice>
        //gameState = GameState.valueOf(parcel.readString()!!)

    }

    /**
     * checks if round is over. Round is over after 3 rolls(AMOUNT_OF_ROLLS)
     */
    private fun isRoundOver(): Boolean {
        return rolls == AMOUNT_OF_ROLLS
    }

    /**
     * Rolls the dices if round is not over, otherwise it changes state to pick score state
     */
    fun rollDices() {
        if(!isRoundOver()) {
            dices.forEach { dice ->
                if (!dice.isDiceSelected) {
                    dice.roll()
                }
            }
            rolls++
        }
        if(isRoundOver()) {
            gameState = GameState.PICKING_SCORE_STATE
        }
    }

    /**
     * User picks score for the round
     */
    fun pickScoreForRound(choice: String) {
        // Kolla vilka tärningar som e valda. i en arraylista
        val pickedDices: ArrayList<Dice> = ArrayList()
        dices.forEach{dice->
            if(dice.isDiceSelected) pickedDices.add(dice)
        }

        handleChoice(choice, pickedDices)
        allChoices.remove(choice)
        scorePerRound.add(Round(choice, pickedDices.mapTo(arrayListOf()) { it.diceVal}))

        rolls = 0
        round++
        if (isGameOver()){
            gameState = GameState.GAME_COMPLETE_STATE
        } else {
            startNextRoll(choice)
        }
    }

    /**
     * checks what choice is taken and depending on it send in that number as an Int
     */
    private fun handleChoice(choice:String, pickedDices: ArrayList<Dice>) {
        when(choice) {
            "LOW" -> validateLowScore(3, pickedDices)
            "4" -> validateScore(4, pickedDices)
            "5" -> validateScore(5, pickedDices)
            "6" -> validateScore(6, pickedDices)
            "7" -> validateScore(7, pickedDices)
            "8" -> validateScore(8, pickedDices)
            "9" -> validateScore(9, pickedDices)
            "10" -> validateScore(10, pickedDices)
            "11" -> validateScore(11, pickedDices)
            "12" -> validateScore(12, pickedDices)
            else -> throw IllegalArgumentException("The choice is not valid")
        }
    }

    /**
     * validates low score
     */
    private fun validateLowScore(lowNumber: Int, pickedDices: ArrayList<Dice>) {
        pickedDices.forEach { dice ->
            if(dice.diceVal > lowNumber) throw IllegalArgumentException("Value need to be lower than $lowNumber")
        }
    }

    /**
     * Starts up a new game and reset all values back to a fresh state
     */
    fun newGame() {
        allChoices = arrayListOf(
            "LOW",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "12",
        )
        scorePerRound = ArrayList()
        rolls = 0
        round = 0
        gameState = GameState.GAME_START
        deSelectAllDices()
        rollDices()
    }
    private fun isGameOver(): Boolean {
        return round == AMOUNT_OF_ROUNDS
    }
    private fun startNextRoll(choice:String) {
            gameState = GameState.ROLLING_STATE
            deSelectAllDices()

    }
    private fun deSelectAllDices() {
        dices.forEach { dice ->
            if(dice.isDiceSelected) dice.toggleDice()
        }
    }

    /**
     * Validates if the pickes choice for score is valid and okay to take.
     */
    private fun validateScore(target: Int, pickedDices:ArrayList<Dice>) {
        pickedDices.sortByDescending { it.diceVal }
        var marked = BooleanArray(pickedDices.size)

        for(i: Int in pickedDices.indices) {
            var totalSum = pickedDices[i].diceVal
            var toMark = ArrayList<Int>()

            if (totalSum == target || marked[i]) {
                marked[i] = true
                continue
            }
            toMark.add(i)

            for(j:Int in i+1 until pickedDices.size) {
                if(marked[j]) continue
                totalSum += pickedDices[j].diceVal
                toMark.add(j)
                if (totalSum > target) {
                    totalSum = pickedDices[i].diceVal + pickedDices[j].diceVal
                    toMark = arrayListOf(i, j)
                }
                if(totalSum == target) {
                    mark(toMark, marked)
                    break
                }
            }
        }
        if(marked.any {!it} ) throw IllegalArgumentException("Dice combination is not valid")

    }

    private fun mark(toMark: ArrayList<Int>, marked: BooleanArray) {
        toMark.forEach{ marked[it] = true}
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(rolls)
        parcel.writeInt(round)
        parcel.writeString(gameState.name)

        parcel.writeList(scorePerRound)
        parcel.writeList(allChoices)
        parcel.writeList(dices)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }

}