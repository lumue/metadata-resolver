package io.github.lumue.mdresolver.sites.ph

import io.github.lumue.mdresolver.sites.BasicHttpClient

class PhHttpClient(
        username: String = "",
        password: String = ""
) : BasicHttpClient(

        username = username,
        password = password
)