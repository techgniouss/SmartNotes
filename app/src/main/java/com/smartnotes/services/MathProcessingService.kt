package com.smartnotes.services

import org.mariuszgromada.math.mxparser.Expression
import java.util.regex.Pattern

class MathProcessingService {

    fun parseSpokenMath(spokenText: String): String {
        var result = spokenText.lowercase()

        result = result.replace(Regex("\\bplus\\b"), "+")
        result = result.replace(Regex("\\bminus\\b"), "-")
        result = result.replace(Regex("\\btimes\\b"), "*")
        result = result.replace(Regex("\\bmultiplied by\\b"), "*")
        result = result.replace(Regex("\\bdivided by\\b"), "/")
        result = result.replace(Regex("\\bover\\b"), "/")
        
        result = result.replace(Regex("\\binto\\b"), "*")
        
        result = result.replace(Regex("\\bby\\b(?=\\s*\\d)")) { "/" }
        
        result = result.replace(Regex("\\bsquared\\b"), "^2")
        result = result.replace(Regex("\\bcubed\\b"), "^3")
        result = result.replace(Regex("\\bto the power of\\b"), "^")
        result = result.replace(Regex("\\bpower\\b"), "^")
        
        result = result.replace(Regex("\\bsquare root of\\b"), "sqrt(")
        result = result.replace(Regex("\\broot\\b"), "sqrt(")
        
        result = result.replace(Regex("\\bopen parenthesis\\b"), "(")
        result = result.replace(Regex("\\bclose parenthesis\\b"), ")")
        result = result.replace(Regex("\\bopen bracket\\b"), "(")
        result = result.replace(Regex("\\bclose bracket\\b"), ")")
        
        result = result.replace(Regex("\\bone\\b"), "1")
        result = result.replace(Regex("\\btwo\\b"), "2")
        result = result.replace(Regex("\\bthree\\b"), "3")
        result = result.replace(Regex("\\bfour\\b"), "4")
        result = result.replace(Regex("\\bfive\\b"), "5")
        result = result.replace(Regex("\\bsix\\b"), "6")
        result = result.replace(Regex("\\bseven\\b"), "7")
        result = result.replace(Regex("\\beight\\b"), "8")
        result = result.replace(Regex("\\bnine\\b"), "9")
        result = result.replace(Regex("\\bzero\\b"), "0")
        result = result.replace(Regex("\\bten\\b"), "10")

        result = result.replace(Regex("\\bequals\\b"), "=")
        result = result.replace(Regex("\\bequal to\\b"), "=")
        
        return result.trim()
    }

    fun solveExpression(expression: String): Result {
        return try {
            val cleanedExpression = expression.replace("=", "").trim()
            
            val expr = Expression(cleanedExpression)
            val result = expr.calculate()
            
            if (result.isNaN() || result.isInfinite()) {
                Result.Error("Invalid expression")
            } else {
                Result.Success(result, formatResult(result))
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error evaluating expression")
        }
    }

    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }

    fun extractMathExpressions(text: String): List<String> {
        val expressions = mutableListOf<String>()
        val mathPattern = Pattern.compile(
            "[0-9+\\-*/^()\\s.]+[=][0-9+\\-*/^()\\s.]*|[0-9+\\-*/^()\\s.]+"
        )
        val matcher = mathPattern.matcher(text)
        
        while (matcher.find()) {
            val match = matcher.group()
            if (match.length > 2 && match.any { it in "+-*/^=" }) {
                expressions.add(match.trim())
            }
        }
        
        return expressions
    }

    fun convertToLatex(expression: String): String {
        var latex = expression
        
        latex = latex.replace(Regex("\\^(\\d+)")) { "^{${it.groupValues[1]}}" }
        
        latex = latex.replace(Regex("sqrt\\(([^)]+)\\)")) { "\\sqrt{${it.groupValues[1]}}" }
        
        latex = latex.replace(Regex("(\\d+)/(\\d+)")) { "\\frac{${it.groupValues[1]}}{${it.groupValues[2]}}" }
        
        latex = latex.replace("*", "\\times ")
        latex = latex.replace("/", "\\div ")
        
        return latex
    }

    sealed class Result {
        data class Success(val value: Double, val formatted: String) : Result()
        data class Error(val message: String) : Result()
    }
}
