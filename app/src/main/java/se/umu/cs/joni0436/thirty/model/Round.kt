package se.umu.cs.joni0436.thirty.model

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @param userChoice - Represents the option the user picks to combine the total sum from the dices
 * @param scoreCombinations - Is an arraylist showing the value of each dice for this round
 */
data class Round(val userChoice: String?, val scoreCombinations: ArrayList<Int>): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readArrayList(Int::class.java.classLoader) as ArrayList<Int>
    ){

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userChoice)
        parcel.writeList(scoreCombinations)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Round> {
        override fun createFromParcel(parcel: Parcel): Round {
            return Round(parcel)
        }

        override fun newArray(size: Int): Array<Round?> {
            return arrayOfNulls(size)
        }
    }
}
