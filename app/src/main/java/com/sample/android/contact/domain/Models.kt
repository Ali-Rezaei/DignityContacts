package com.sample.android.contact.domain

class Contact @JvmOverloads constructor(
        val name: String,
        val phoneNumbers: List<ContactPhoneNumber>? = null,
        val briefName: String? = null,
        val accentName: String? = null,
        val flagResIds: List<Int>? = null,
        // State of the item
        var isExpanded: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        if (other !is Contact) {
            return false
        }
        return other.name == name
    }
}

class ContactPhoneNumber(
        var number: String,
        val typeLabel: String,
        val flagResId: Int) {

    override fun equals(other: Any?): Boolean {
        if (other !is ContactPhoneNumber) {
            return false
        }
        return other.number.replace("\\s".toRegex(), "") ==
                number.replace("\\s".toRegex(), "")
    }
}