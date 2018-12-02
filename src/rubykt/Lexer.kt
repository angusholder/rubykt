package rubykt

class Lexer(
        private val source: String,
        private var pos: Int = 0
) : ILexer {

    override val originalText: CharSequence
        get() = source

    override fun advanceLexer() {
        TODO("not implemented")
    }

    override var tokenType: TokenType? = null

    override fun lookAhead(steps: Int): TokenType {
        TODO("not implemented")
    }

    override var rawTokenIndex: Int = 0
    override val tokenText: String?
        get() = source.substring(currentOffset, currentOffset + currentLength)
    override var currentOffset: Int = 0
        private set
    var currentLength: Int = 0
    override val eof: Boolean
        get() = currentOffset >= source.length
    // Operator precedence:
    //
    // high
    //     ::
    //     []
    //     **
    //     -(unary)  +(unary)  !  ~
    //     *  /  %
    //     +  -
    //     <<  >>
    //     &
    //     |  ^
    //     >  >=  <  <=
    //     <=> ==  === !=  =~  !~
    //     &&
    //     ||
    //     .. ...
    //     =(+=, -=...)
    //     not
    //     and or
    // low

    private fun match(ch: Char): Boolean {
        if (source[pos] == ch) {
            pos++
            return true
        } else {
            return false
        }
    }

    fun lex() {
        val start = pos

        val type = when (source[pos++]) {
            '+' -> {
                if (match('=')) TokenType.PlusEq
                else TokenType.Plus
            }
            '-' -> {
                if (match('=')) TokenType.MinusEq
                else if (match('>')) TokenType.ThinArrow
                else TokenType.Minus
            }
            '*' -> {
                if (match('=')) TokenType.MulEq
                else if (match('*')) {
                    if (match('=')) TokenType.PowEq
                    else TokenType.Pow
                } else TokenType.Mul
            }
            '/' -> {
                if (match('=')) TokenType.DivEq
                else TokenType.Div
            }
            '%' -> {
                if (match('=')) TokenType.PercentEq
                else TokenType.Percent
            }
            '&' -> {
                if (match('=')) TokenType.AndEq
                else if (match('&')) {
                    if (match('=')) TokenType.AndAndEq
                    else TokenType.AndAnd
                } else TokenType.And
            }
            '|' -> {
                if (match('=')) TokenType.OrEq
                else if (match('|')) {
                    if (match('=')) TokenType.OrOrEq
                    else TokenType.OrOr
                } else TokenType.Or
            }
            '^' -> {
                if (match('=')) TokenType.XorEq
                else TokenType.Xor
            }
            '<' -> {
                if (match('=')) {
                    if (match('>')) TokenType.Compare
                    else TokenType.LtEq
                } else if (match('<')) {
                    if (match('=')) TokenType.ShlEq
                    else TokenType.Shl
                } else TokenType.Lt
            }
            '>' -> {
                if (match('=')) TokenType.GtEq
                else if (match('>')) {
                    if (match('=')) TokenType.ShrEq
                    else TokenType.Shr
                } else TokenType.Gt
            }
            '~' -> {
                TokenType.Tilde
            }
            '!' -> {
                if (match('=')) TokenType.BangEq
                else if (match('~')) TokenType.BangTilde
                else TokenType.Bang
            }
            '.' -> {
                if (match('.')) {
                    if (match('.')) TokenType.DotDotDot
                    else TokenType.DotDot
                } else TokenType.Dot
            }
            '=' -> {
                if (match('=')) {
                    if (match('=')) TokenType.EqEqEq
                    else TokenType.EqEq
                } else if (match('~')) TokenType.EqTilde
                else if (match('>')) TokenType.FatArrow
                else TokenType.Eq
            }
            '(' -> TokenType.LParen
            ')' -> TokenType.RParen
            '{' -> TokenType.LBrace
            '}' -> TokenType.RBrace
            '[' -> TokenType.LBracket
            ']' -> TokenType.RBracket
            ',' -> TokenType.Comma
            ';' -> TokenType.Semicolon
            '@' -> TokenType.At
            '$' -> TokenType.Dollar
            ':' -> {
                if (match(':')) TokenType.ColonColon
                else throw NotImplementedError()
            }
            else -> {
                if (isIdentHead(source[pos])) {
                    var end = pos + 1
                    while (end < source.length) {
                        if (!isIdentTail(source[end])) break
                        end += 1
                    }

                    val len = end - start
                    val text = source.substring(start, start + len)
                    parseKeyword(text)
                } else throw NotImplementedError()
            }
        }


    }

    private fun isIdentHead(ch: Char): Boolean = ch == '_' || ch.isLetter()

    private fun isIdentTail(ch: Char): Boolean = ch == '_' || ch.isLetterOrDigit()

    private fun parseKeyword(text: String): TokenType = keywords.getOrDefault(text, TokenType.Ident)

    private val keywords: HashMap<String, TokenType> = hashMapOf(
            "alias" to TokenType.KW_alias,
            "and" to TokenType.KW_and,
            "BEGIN" to TokenType.KW_BEGIN,
            "begin" to TokenType.KW_begin,
            "break" to TokenType.KW_break,
            "case" to TokenType.KW_case,
            "class" to TokenType.KW_class,
            "def" to TokenType.KW_def,
            "defined" to TokenType.KW_defined,
            "do" to TokenType.KW_do,
            "else" to TokenType.KW_else,
            "elsif" to TokenType.KW_elsif,
            "end" to TokenType.KW_end,
            "END" to TokenType.KW_END,
            "ensure" to TokenType.KW_ensure,
            "false" to TokenType.KW_false,
            "for" to TokenType.KW_for,
            "if" to TokenType.KW_if,
            "in" to TokenType.KW_in,
            "module" to TokenType.KW_module,
            "next" to TokenType.KW_next,
            "nil" to TokenType.KW_nil,
            "not" to TokenType.KW_not,
            "or" to TokenType.KW_or,
            "redo" to TokenType.KW_redo,
            "rescue" to TokenType.KW_rescue,
            "retry" to TokenType.KW_retry,
            "return" to TokenType.KW_return,
            "self" to TokenType.KW_self,
            "super" to TokenType.KW_super,
            "then" to TokenType.KW_then,
            "true" to TokenType.KW_true,
            "undef" to TokenType.KW_undef,
            "unless" to TokenType.KW_unless,
            "until" to TokenType.KW_until,
            "when" to TokenType.KW_when,
            "while" to TokenType.KW_while,
            "yield" to TokenType.KW_yield
    )
}