package compiler.exceptions

import org.antlr.v4.runtime.misc.ParseCancellationException

class SemanticException(message: String) : ParseCancellationException(message)
