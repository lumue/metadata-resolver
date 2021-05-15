package io.github.lumue.mdresolver.core

import java.lang.RuntimeException

class ResolveException(s: String, e: Throwable?) : RuntimeException(s,e) {
    constructor(s: String) : this(s,null)

}


class NoResolverFound(s: String) : RuntimeException(s) {

}

