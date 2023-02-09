package se.umu.cs.joni0436.thirty.model

import android.os.Parcel
import android.os.Parcelable

private val AMOUNT_OF_ROUNDS = 10
private val AMOUNT_OF_ROLLS = 3
private val SIZE_OF_DICE = 6


class Game() : Parcelable {

    // import 6 dices from dice
    var dices: ArrayList<Dice> =
        arrayListOf(Dice(),Dice(),Dice(),Dice(),Dice(),Dice())

    var gameState: GameState = GameState.GAME_COMPLETE_STATE

    // Choices a user can pick for scores after each round
    var availableChoices: ArrayList<String> = arrayListOf(
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
        scorePerRound = parcel.readArrayList(Round::class.java.classLoader) as ArrayList<Round>
        availableChoices = parcel.readArrayList(String::class.java.classLoader) as ArrayList<String>
        //gameState = parcel.readString()?.let { GameState.valueOf(it) }!!
        gameState = GameState.valueOf(parcel.readString()!!)

    }

    fun isRoundOver(): Boolean {
        return rolls == AMOUNT_OF_ROLLS
    }

    // need to check how many roll
    // ++ on rolls
    fun rollDices() {
        if(!isRoundOver()) {
            for (dice: Dice in dices) {
                if(!dice.isDiceSelected) {
                    dice.roll(SIZE_OF_DICE)
                }
            }
            rolls++
        }
        gameState = GameState.PICKING_SCORE_STATE

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(rolls)
        parcel.writeInt(round)
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