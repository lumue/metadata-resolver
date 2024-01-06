package net.lumue.mdresolver.sites.ph

import net.lumue.mdresolver.sites.BasicHttpClient

class PhHttpClient(



) : BasicHttpClient(){
    init {
        addCookie("platform", "pc", ".pornhub.com")
        addCookie("accessAgeDisclaimerPH", "1", ".pornhub.com")
    }

}
fun String.withGenericPhDomain(): String {
    return this.replace("//de.", "//www.").replace("//ge.","//www.")
}