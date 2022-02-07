package com.mobilityk.core.exception

class CommException (val reason: String ) : Exception(reason) {
    override val message: String?
        get() = this.reason
}