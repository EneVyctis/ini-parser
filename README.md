# INI-Parser


ini-parser is a file parser library in java. It is able to parse INI files, supporting most of what the file format has to offer. 

It is still a fairly simple implementation and **might not work on complexe files**.


## QuickStart 

The usage is quite straight forward.

First add this project as a dependency according to your preference.

Then, create an IniParser object providing the String of the absolute path of your ini file, and call the parse method of the IniParser class.

```java
IniParser parser = new IniParser("Some absolute path");
parser.parse();
```

then, use the getters to access the data

```java
public Set<String> getSections() // Returns a Set of every parsed sections.
public Set<String> getKeysFromSection(String section) throws IllegalArgumentException // Returns a Set of every keys of a specified section.
public String getValue(String section, String key) throws IllegalArgumentException // Returns the value corresponding to the specified key of the specified section
public int getValueAsInt(String section, String key) throws IllegalArgumentException, NumberFormatException // Attempts to return the value as an int
public boolean getValueAsBoolean(String section, String key) throws IllegalArgumentException, Exception // Attempts to return the value as a boolean
```
