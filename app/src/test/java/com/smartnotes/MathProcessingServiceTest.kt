package com.smartnotes

import com.smartnotes.services.MathProcessingService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MathProcessingServiceTest {
    
    private lateinit var mathService: MathProcessingService
    
    @Before
    fun setup() {
        mathService = MathProcessingService()
    }
    
    @Test
    fun testParseSpokenMath_BasicOperations() {
        assertEquals("2 + 3", mathService.parseSpokenMath("two plus three"))
        assertEquals("5 - 2", mathService.parseSpokenMath("five minus two"))
        assertEquals("4 * 3", mathService.parseSpokenMath("four times three"))
        assertEquals("10 / 2", mathService.parseSpokenMath("ten divided by two"))
    }
    
    @Test
    fun testParseSpokenMath_FractionNotation() {
        val result = mathService.parseSpokenMath("two by three")
        assertTrue(result.contains("/"))
    }
    
    @Test
    fun testParseSpokenMath_IntoMeansMultiply() {
        val result = mathService.parseSpokenMath("two into three")
        assertTrue(result.contains("*"))
    }
    
    @Test
    fun testParseSpokenMath_Powers() {
        assertEquals("3^2", mathService.parseSpokenMath("three squared"))
        assertEquals("2^3", mathService.parseSpokenMath("two cubed"))
    }
    
    @Test
    fun testSolveExpression_BasicArithmetic() {
        val result = mathService.solveExpression("2 + 3")
        assertTrue(result is MathProcessingService.Result.Success)
        assertEquals(5.0, (result as MathProcessingService.Result.Success).value, 0.001)
    }
    
    @Test
    fun testSolveExpression_Division() {
        val result = mathService.solveExpression("10 / 2")
        assertTrue(result is MathProcessingService.Result.Success)
        assertEquals(5.0, (result as MathProcessingService.Result.Success).value, 0.001)
    }
    
    @Test
    fun testSolveExpression_ComplexExpression() {
        val result = mathService.solveExpression("(2 + 3) * 4")
        assertTrue(result is MathProcessingService.Result.Success)
        assertEquals(20.0, (result as MathProcessingService.Result.Success).value, 0.001)
    }
    
    @Test
    fun testSolveExpression_InvalidExpression() {
        val result = mathService.solveExpression("abc + def")
        assertTrue(result is MathProcessingService.Result.Error)
    }
    
    @Test
    fun testExtractMathExpressions() {
        val text = "The answer is 2 + 3 = 5 and also 10 * 2 = 20"
        val expressions = mathService.extractMathExpressions(text)
        assertTrue(expressions.isNotEmpty())
    }
    
    @Test
    fun testConvertToLatex_Powers() {
        val latex = mathService.convertToLatex("x^2")
        assertEquals("x^{2}", latex)
    }
    
    @Test
    fun testConvertToLatex_Fractions() {
        val latex = mathService.convertToLatex("2/3")
        assertTrue(latex.contains("\\frac"))
    }
}
