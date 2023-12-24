package net.lumue.mdresolver.sites.ph

import net.lumue.mdresolver.sites.BasicHttpClient

class PhHttpClient(

) : BasicHttpClient(){
    init {
        addCookie("platform", "pc", "pornhub.com")
        addCookie("accessAgeDisclaimerPH", "1", "pornhub.com")
    }

}
