package com.mobilityk.core.exception

class AlreadyExistUserException (val reason: String ) : Exception(reason) {
    override val message: String?
        get() = this.reason
}