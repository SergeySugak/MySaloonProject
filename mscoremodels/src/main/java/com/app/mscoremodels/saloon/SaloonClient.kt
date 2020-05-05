package com.app.mscoremodels.saloon

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class SaloonClient constructor() {
    var name: String = ""
    var phone: String = ""
    var email: String = ""

    constructor(name: String,
                phone: String,
                email: String): this(){
        this.name = name
        this.phone = phone
        this.email = email
    }
}