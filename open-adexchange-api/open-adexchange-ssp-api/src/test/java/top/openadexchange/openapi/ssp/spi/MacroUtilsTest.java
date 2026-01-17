package top.openadexchange.openapi.ssp.spi;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import top.openadexchange.openapi.ssp.spi.model.MacroContext;

class MacroUtilsTest {

    private Pattern macroPattern;
    private MacroContext context;

    @BeforeEach
    void setUp() {
        // 模式：${macroName}
        macroPattern = Pattern.compile("\\$\\{(\\w+)\\}");
        
        Map<String, String> macroValues = new HashMap<>();
        macroValues.put("USER_ID", "12345");
        macroValues.put("PRICE", "100.50");
        macroValues.put("AD_ID", "67890");
        macroValues.put("TIMESTAMP", "1642459200");
        
        context = new MacroContext(macroValues);
    }

    @Test
    void testReplaceMacrosWithSingleMacro() {
        String template = "User ID is ${USER_ID}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("User ID is 12345", result);
    }

    @Test
    void testReplaceMacrosWithMultipleMacros() {
        String template = "Ad ${AD_ID} for user ${USER_ID} at price ${PRICE}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("Ad 67890 for user 12345 at price 100.50", result);
    }

    @Test
    void testReplaceMacrosWithNoMacros() {
        String template = "Just a plain text string";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("Just a plain text string", result);
    }

    @Test
    void testReplaceMacrosWithUnknownMacro() {
        String template = "Unknown macro: ${UNKNOWN_MACRO}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        // 未知的宏应该保留原样
        assertEquals("Unknown macro: ${UNKNOWN_MACRO}", result);
    }

    @Test
    void testReplaceMacrosWithMixedKnownAndUnknownMacros() {
        String template = "User: ${USER_ID}, Unknown: ${UNKNOWN}, Price: ${PRICE}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("User: 12345, Unknown: ${UNKNOWN}, Price: 100.50", result);
    }

    @Test
    void testReplaceMacrosWithNullTemplate() {
        String result = MacroUtils.replaceMacros(null, macroPattern, context);
        
        assertNull(result);
    }

    @Test
    void testReplaceMacrosWithEmptyTemplate() {
        String template = "";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("", result);
    }

    @Test
    void testReplaceMacrosWithTemplateOnlyContainingMacro() {
        String template = "${USER_ID}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("12345", result);
    }

    @Test
    void testReplaceMacrosWithRepeatingMacros() {
        String template = "${USER_ID}-${USER_ID}-${USER_ID}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("12345-12345-12345", result);
    }

    @Test
    void testReplaceMacrosWithEmptyContext() {
        Map<String, String> emptyMap = new HashMap<>();
        MacroContext emptyContext = new MacroContext(emptyMap);
        
        String template = "User: ${USER_ID}, Price: ${PRICE}";
        String result = MacroUtils.replaceMacros(template, macroPattern, emptyContext);
        
        assertEquals("User: ${USER_ID}, Price: ${PRICE}", result);
    }

    @Test
    void testReplaceMacrosWithNullContext() {
        MacroContext nullContext = new MacroContext(null);
        
        String template = "User: ${USER_ID}";
        String result = MacroUtils.replaceMacros(template, macroPattern, nullContext);
        
        assertEquals("User: ${USER_ID}", result);
    }

    @Test
    void testReplaceMacrosWithSpecialCharactersInMacroValue() {
        Map<String, String> specialValues = new HashMap<>();
        specialValues.put("SPECIAL", "$100.00 & 50% off");
        MacroContext specialContext = new MacroContext(specialValues);
        
        String template = "Offer: ${SPECIAL}";
        String result = MacroUtils.replaceMacros(template, macroPattern, specialContext);
        
        assertEquals("Offer: $100.00 & 50% off", result);
    }

    @Test
    void testReplaceMacrosWithBackslashesInMacroValue() {
        Map<String, String> specialValues = new HashMap<>();
        specialValues.put("PATH", "/home/user\\documents\\file.txt");
        MacroContext specialContext = new MacroContext(specialValues);
        
        String template = "File path: ${PATH}";
        String result = MacroUtils.replaceMacros(template, macroPattern, specialContext);
        
        assertEquals("File path: /home/user\\documents\\file.txt", result);
    }

    @Test
    void testReplaceMacrosWithEmptyMacroValue() {
        Map<String, String> emptyValueMap = new HashMap<>();
        emptyValueMap.put("EMPTY", "");
        MacroContext emptyValueContext = new MacroContext(emptyValueMap);
        
        String template = "Value: ${EMPTY}";
        String result = MacroUtils.replaceMacros(template, macroPattern, emptyValueContext);
        
        assertEquals("Value: ", result);
    }

    @Test
    void testReplaceMacrosWithMacroAtBeginning() {
        String template = "${USER_ID} is the user";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("12345 is the user", result);
    }

    @Test
    void testReplaceMacrosWithMacroAtEnd() {
        String template = "The price is ${PRICE}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("The price is 100.50", result);
    }

    @Test
    void testReplaceMacrosWithMacroAtBothEnds() {
        String template = "${USER_ID} and ${PRICE}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("12345 and 100.50", result);
    }

    @Test
    void testReplaceMacrosWithConsecutiveMacros() {
        String template = "${USER_ID}${AD_ID}${PRICE}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("1234567890100.50", result);
    }

    @Test
    void testReplaceMacrosWithNumericMacroNames() {
        Map<String, String> numericValues = new HashMap<>();
        numericValues.put("123", "numeric_value");
        numericValues.put("test123", "mixed_value");
        MacroContext numericContext = new MacroContext(numericValues);
        
        String template = "${test123} and ${123}";
        String result = MacroUtils.replaceMacros(template, macroPattern, numericContext);
        
        assertEquals("mixed_value and numeric_value", result);
    }

    @Test
    void testReplaceMacrosWithUnderscoreInMacroName() {
        Map<String, String> underscoreValues = new HashMap<>();
        underscoreValues.put("USER_NAME_FIRST", "John");
        underscoreValues.put("USER_NAME_LAST", "Doe");
        MacroContext underscoreContext = new MacroContext(underscoreValues);
        
        String template = "${USER_NAME_FIRST} ${USER_NAME_LAST}";
        String result = MacroUtils.replaceMacros(template, macroPattern, underscoreContext);
        
        assertEquals("John Doe", result);
    }

    @Test
    void testReplaceMacrosWithCaseSensitiveMacroNames() {
        Map<String, String> caseValues = new HashMap<>();
        caseValues.put("user_id", "lowercase");
        caseValues.put("USER_ID", "uppercase");
        caseValues.put("User_Id", "mixedcase");
        MacroContext caseContext = new MacroContext(caseValues);
        
        String template = "${user_id}-${USER_ID}-${User_Id}";
        String result = MacroUtils.replaceMacros(template, macroPattern, caseContext);
        
        assertEquals("lowercase-uppercase-mixedcase", result);
    }

    @Test
    void testReplaceMacrosWithWhitespaceAroundMacro() {
        String template = " ${USER_ID} ${PRICE} ${AD_ID} ";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals(" 12345 100.50 67890 ", result);
    }

    @Test
    void testReplaceMacrosWithNewlinesInTemplate() {
        String template = "Line 1: ${USER_ID}\nLine 2: ${PRICE}\nLine 3: ${AD_ID}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("Line 1: 12345\nLine 2: 100.50\nLine 3: 67890", result);
    }

    @Test
    void testReplaceMacrosWithTabsInTemplate() {
        String template = "ID:\t${USER_ID}\tPrice:\t${PRICE}";
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals("ID:\t12345\tPrice:\t100.50", result);
    }

    @Test
    void testReplaceMacrosWithMacroValueContainingDollarSign() {
        Map<String, String> dollarValues = new HashMap<>();
        dollarValues.put("AMOUNT", "$500");
        MacroContext dollarContext = new MacroContext(dollarValues);
        
        String template = "Amount: ${AMOUNT}";
        String result = MacroUtils.replaceMacros(template, macroPattern, dollarContext);
        
        assertEquals("Amount: $500", result);
    }

    @Test
    void testReplaceMacrosWithMacroValueContainingBraces() {
        Map<String, String> braceValues = new HashMap<>();
        braceValues.put("EXPRESSION", "{x + y}");
        MacroContext braceContext = new MacroContext(braceValues);
        
        String template = "Formula: ${EXPRESSION}";
        String result = MacroUtils.replaceMacros(template, macroPattern, braceContext);
        
        assertEquals("Formula: {x + y}", result);
    }

    @Test
    void testReplaceMacrosWithUnicodeMacroValues() {
        Map<String, String> unicodeValues = new HashMap<>();
        unicodeValues.put("CHINESE", "你好世界");
        unicodeValues.put("EMOJI", "🎉✨");
        MacroContext unicodeContext = new MacroContext(unicodeValues);
        
        String template = "${CHINESE} ${EMOJI}";
        String result = MacroUtils.replaceMacros(template, macroPattern, unicodeContext);
        
        assertEquals("你好世界 🎉✨", result);
    }

    @Test
    void testReplaceMacrosWithVeryLongTemplate() {
        StringBuilder longTemplate = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longTemplate.append("User ${USER_ID} with price ${PRICE}. ");
        }
        String template = longTemplate.toString();
        
        String result = MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertTrue(result.contains("User 12345 with price 100.50."));
        assertTrue(result.length() > 100);
    }

    @Test
    void testReplaceMacrosWithVeryLongMacroValue() {
        StringBuilder longValue = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longValue.append("x");
        }
        //longValue有 1000个字符
        Map<String, String> longValueMap = new HashMap<>();
        longValueMap.put("LONG", longValue.toString());
        MacroContext longValueContext = new MacroContext(longValueMap);
        
        String template = "Long value: ${LONG}";
        String result = MacroUtils.replaceMacros(template, macroPattern, longValueContext);
        
        assertEquals(1012, result.length()); // "Long value: " + 1000 chars
        assertTrue(result.endsWith("xxx"));
    }

    @Test
    void testReplaceMacrosWithMultipleSameMacroNamesDifferentPatterns() {
        // 测试同一个宏在不同模式下的替换
        Pattern simplePattern = Pattern.compile("\\$(\\w+)"); // $USER_ID
        Map<String, String> values = new HashMap<>();
        values.put("USER_ID", "123");
        MacroContext simpleContext = new MacroContext(values);
        
        String template = "$USER_ID";
        String result = MacroUtils.replaceMacros(template, simplePattern, simpleContext);
        
        assertEquals("123", result);
    }

    @Test
    void testReplaceMacrosDoesNotModifyOriginalTemplate() {
        String template = "${USER_ID} ${PRICE}";
        String originalTemplate = template;
        
        MacroUtils.replaceMacros(template, macroPattern, context);
        
        assertEquals(originalTemplate, template);
    }

    @Test
    void testReplaceMacrosWithEscapedBracesInPattern() {
        // 测试包含转义字符的模式
        Pattern escapedPattern = Pattern.compile("\\%\\{(\\w+)\\}"); // %{macro}
        Map<String, String> escapedValues = new HashMap<>();
        escapedValues.put("USER", "test");
        MacroContext escapedContext = new MacroContext(escapedValues);
        
        String template = "User: %{USER}";
        String result = MacroUtils.replaceMacros(template, escapedPattern, escapedContext);
        
        assertEquals("User: test", result);
    }

    @Test
    void testReplaceMacrosWithMacroValueContainingReplacementString() {
        // 测试替换值中包含替换模式相关字符
        Map<String, String> trickyValues = new HashMap<>();
        trickyValues.put("REPL", "$1");
        MacroContext trickyContext = new MacroContext(trickyValues);
        
        String template = "Value: ${REPL}";
        String result = MacroUtils.replaceMacros(template, macroPattern, trickyContext);
        
        assertEquals("Value: $1", result);
    }
}
