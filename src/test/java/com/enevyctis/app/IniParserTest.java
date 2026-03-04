package com.enevyctis.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.FileSystems;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IniParserTest {

    private static String absolutePath;

    @BeforeAll
    public static void setUp(){
        absolutePath = FileSystems.getDefault().getPath("src/test/java/com/enevyctis/app/TestIniFiles").normalize().toAbsolutePath().toString();
    }

    @Test
    public void shouldParseSingleSection(){
        IniParser parser = new IniParser(absolutePath+"/section-only.ini");
        parser.parse();
        Set<String> sections = parser.getSections();
        assertArrayEquals(new String[]{"section name"}, sections.toArray());
    }

    @Test
    public void shouldIgnoreEmptyLines(){
        IniParser parser = new IniParser(absolutePath+"/section-only-with-empty-lines.ini");
        parser.parse();
        Set<String> sections = parser.getSections();
        assertArrayEquals(new String[]{"section name"}, sections.toArray());
    }

    @Test
    public void shouldParseSectionAndValuesAsString(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();
        Set<String> sections = parser.getSections();
        String debug = parser.getValue("section name", "Debug");
        String environmentready = parser.getValue("section name", "EnvironmentReady");
        String integerVariable = parser.getValue("section name", "IntegerVariable");
        String stringVariable = parser.getValue("section name", "StringVariable");
        assertArrayEquals(new String[]{"section name"}, sections.toArray());
        assertEquals("true", debug);
        assertEquals("False", environmentready);
        assertEquals("5", integerVariable);
        assertEquals("hello world", stringVariable);
    }

    @Test
    public void shouldParseEveryKey(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();

        Set<String> keys = parser.getKeysFromSection("section name");
        String[] expectedKeys = new String[]{"Debug","EnvironmentReady","IntegerVariable","StringVariable","whitespace key","same key name"};
        for(String key : expectedKeys){
            assertTrue(keys.contains(key.toLowerCase()));
        }
    }

    @Test
    public void shouldParseCorrectlyKeyValueWithSpace(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();

        String valueWithWhiteSpace = parser.getValue("section name", "whitespace key");
        assertEquals("whitespace value", valueWithWhiteSpace);
    }

    @Test
    public void shouldOverwriteSameKeyNames(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();

        String sameKeyName = parser.getValue("section name", "same key name");
        assertEquals("value 2", sameKeyName);
    }

    @Test
    public void shouldIgnoreComments(){
        IniParser parser = new IniParser(absolutePath+"/single-section-single-value-with-comments.ini");
        parser.parse();

        String value = parser.getValue("section name", "key");
        Set<String> sections = parser.getSections();
        Set<String> keys = parser.getKeysFromSection("section name");

        assertEquals("value", value); // the comment following the value should not be part of it.
        assertEquals(1, keys.size());
        assertEquals(1, sections.size());
        assertTrue(keys.contains("key"));
        assertTrue(sections.contains("section name"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfSectionOrKeyIsWrong(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();

        assertThrows(IllegalArgumentException.class, () -> parser.getValue("not existing section", "StringVariable"));
        assertThrows(IllegalArgumentException.class, () -> parser.getValue("section name", "not existing variable"));
    }

    @Test
    public void shouldReturnBooleanValues(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();

        try{
            boolean debug = parser.getValueAsBoolean("section name", "Debug");
            assertEquals(true, debug);
        } catch (Exception e){
            assertTrue(false);
        }
    }

    @Test
    public void shouldThrowAnExceptionIfNotABoolean(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();

        assertThrows(Exception.class, () -> parser.getValueAsBoolean("section name", "EnvironmentReady"));
    }

    @Test
    public void shouldReturnIntegerValues(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();

        int integerVariable = parser.getValueAsInt("section name", "IntegerVariable");
        assertEquals(5, integerVariable);
    }

    @Test
    public void shouldThrowAnExceptionIfNotAnInteger(){
        IniParser parser = new IniParser(absolutePath+"/single-section-multiple-values.ini");
        parser.parse();
        assertThrows(NumberFormatException.class, () -> parser.getValueAsInt("section name", "StringVariable"));
    }

    @Test
    public void shouldParseMultipleSectionsWithSameVariableName(){
        IniParser parser = new IniParser(absolutePath+"/multiple-sections-multiple-values.ini");
        parser.parse();

        Set<String> sections = parser.getSections();
        assertEquals(2, sections.size());
        assertEquals(true, sections.contains("first section"));
        assertEquals(true, sections.contains("second section"));
        assertEquals("key2", parser.getValue("first section", "key2"));
        assertEquals("key2sec2", parser.getValue("second section","key2"));
    }
}
