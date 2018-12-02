package rubykt

interface ILexer {
    /**
     * Returns the complete text being parsed.
     *
     * @return the text being parsed
     */
    val originalText: CharSequence

    /**
     * Advances the lexer to the next token, skipping whitespace and comment tokens.
     */
    fun advanceLexer()

    /**
     * Returns the type of current token from the lexer.
     *
     * @return the token type, or null when the token stream is over.
     * @see .setTokenTypeRemapper
     */
    val tokenType: TokenType?

    /**
     * See what token type is in `steps` ahead
     * @param steps 0 is current token (i.e. the same [PsiBuilder.getTokenType] returns)
     * @return type element which getTokenType() will return if we call advance `steps` times in a row
     */
    fun lookAhead(steps: Int): TokenType

    /**
     * Returns the index of the current token in the original sequence.
     *
     * @return token index
     */
    val rawTokenIndex: Int

    /**
     * Returns the text of the current token from the lexer.
     *
     * @return the token text, or null when the token stream is over.
     */
    val tokenText: String?

    /**
     * Returns the start offset of the current token, or the file length when the token stream is over.
     *
     * @return the token offset.
     */
    val currentOffset: Int

    /**
     * Checks if the lexer has reached the end of file.
     *
     * @return true if the lexer is at end of file, false otherwise.
     */
    val eof: Boolean
}