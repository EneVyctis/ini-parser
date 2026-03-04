package com.enevyctis.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniParser {

    private final Path path;
    private Map<String, Map<String,String>> sections = new HashMap<>();

    public IniParser(String pathString){
        this.path = Paths.get(pathString);
    }

    public void parse(){
        
        String currentSection = "";

        Pattern commentPattern = Pattern.compile("[#;].*$");
        Pattern sectionPattern = Pattern.compile("\\[.+\\]");
        Pattern valuePattern = Pattern.compile(".+=.+");

        try(BufferedReader bufferedReader = Files.newBufferedReader(path)){
            String line = bufferedReader.readLine();

            while( line != null){
                
                if(line.isBlank()){
                    line = bufferedReader.readLine();
                    continue;
                }

                Matcher commentMatcher = commentPattern.matcher(line);

                if( commentMatcher.find()){
                    line = line.replace(commentMatcher.group(), "");
                }
                line.trim();

                Matcher sectionMatcher = sectionPattern.matcher(line);
                if( sectionMatcher.find()){
                    currentSection = sectionMatcher.group().replaceAll("[\\[\\]]", "").trim().toLowerCase();
                    sections.put(currentSection, new HashMap<>());
                    System.out.println("size equals"+sections.size());
                }

                Matcher valueMatcher = valuePattern.matcher(line);

                if( valueMatcher.find()){
                    String[] keyValue = line.split("=",2);
                    sections.get(currentSection).put(keyValue[0].trim().toLowerCase(), keyValue[1].trim());
                }

                line = bufferedReader.readLine();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getValue(String section, String key) throws IllegalArgumentException{

        section = section.toLowerCase();
        key = key.toLowerCase();

        if(!sections.containsKey(section)){
            throw new IllegalArgumentException("Section " +section +" not found.");
        }
        if(!sections.get(section).containsKey(key)){
            throw new IllegalArgumentException("Key " +key+ " not found");
        }
        return sections.get(section).get(key);
    }

    public Set<String> getSections(){
        return sections.keySet();
    }

    public Set<String> getKeysFromSection(String section) throws IllegalArgumentException{

        section = section.toLowerCase();

        if(!sections.containsKey(section)){
            throw new IllegalArgumentException("Section " + section + " not found");
        }
        return sections.get(section).keySet();
    }

    public int getValueAsInt(String section, String key) throws IllegalArgumentException, NumberFormatException{
        key = key.toLowerCase();
        String value = getValue(section, key);

        return Integer.parseInt(value);
    }

    public boolean getValueAsBoolean(String section, String key) throws IllegalArgumentException, Exception {
        key = key.toLowerCase();
        String value = getValue(section, key);

        Set<String> allowedTruthyBooleans = Set.of("true","yes","on","1");
        Set<String> allowedFalsyBooleans = Set.of("false","no","0","off");
        if(allowedTruthyBooleans.contains(value)){
            return true;
        }
        if(allowedFalsyBooleans.contains(value)){
            return false;
        }
        else{
            throw new Exception("Variable "+ value+ " is not a boolean");
        }
    }
}