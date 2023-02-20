package se.umu.cs.joni0436.thirty.model

import android.os.Parcel
import android.os.Parcelable

class Dice(): Parcelable  {
    // dice has a number from 1-6
    //var diceVal = 0
    var diceVal: Int = IntRange(1, 6).random()
    // a dice should be able to be selected
    var isDiceSelected: Boolean = false

    constructor(parcel: Parcel) : this() {
        diceVal = parcel.readInt()
        isDiceSelected = parcel.readByte() != 0.toByte()
    }

    fun roll() {
        diceVal = IntRange(1, 6).random()
    }

    fun getDiceValue(): Int {
        return this.diceVal
    }

    fun toggleDice() {
        isDiceSelected = !isDiceSelected
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(diceVal)
        parcel.writeByte(if (isDiceSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dice> {
        override fun createFromParcel(parcel: Parcel): Dice {
            return Dice(parcel)
        }

        override fun newArray(size: Int): Array<Dice?> {
            return arrayOfNulls(size)
        }
    }


}