/**
 * Created by Rajan on 4/16/2017.
 */
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLParser {

    public XMLParser(){}

    /*Name: startParsing
    * Parameters: String xsdName - xsd file name with extension
    *       String xmlName - xml file name with extension
    *       String outputFileName - file name to save the output queries in
    * Functions:
    *       calls validateXMLSchema to check validity of xml file based on given xsd file
    *       reports the final status whether operation succeeded or failed*/
    protected void startParsing(String xsdName, String xmlName, String outputFileName){
        boolean isValid = this.inputXSD(xsdName, xmlName, outputFileName);
        if(isValid){
            System.out.println("xml data is parsed successfully!");
        }else {
            System.out.println("Program failed to parse xml data!");
        }
    }//end of startParsing

    /*Name: inputXSD
    * Parameters: String xsdName contains the xsd file name,
    *               String xmlName contains the xml file name,
    *               String outputFileName contains the output file name
    * Function: it calls diffrent functions i.e. readReturnFileContents, parseXSD,
    *                           getTableFieldsAttribs, etc */
    private boolean inputXSD(String xsdName, String xmlName, String outputFileName) {
        ArrayList<String> wholeFile = this.readReturnFileContents(xsdName);
        ArrayList<Integer> match = new ArrayList<Integer>();
        ArrayList<Map> fieldsAttribs = new ArrayList<>();
        match.add(1);
        for(String s:wholeFile){
            if(s.trim().length() != 0){
                match = this.parseXSD(s.trim(), match);
                if(match.get(match.size()-1)==8)
                    return false;
                if(match.size()>2){
                    if(match.get(match.size()-1) == 4){
                        Map tMap = this.getTableFieldsAttribs(s);
                        boolean flag = true;
                        for (Map cMap: fieldsAttribs){
                            if(cMap.get("name").toString().equals(tMap.get("name").toString())){
                                flag = false;
                                System.out.println("***Error- "+ tMap + " \n this element is ignored due to duplicate name attribute in xsd file");
                            }
                        }
                        if(flag)
                            fieldsAttribs.add(tMap);
                    }
                }
            }

        }
        return this.inputXML(xmlName, fieldsAttribs, outputFileName);
    }//end of inputXSD

    /*Name: parseXSD
    * Parameters: String s contains the whole xsd file,
    *               ArrayList<Integer> match contains numbers that represent a predefined syntax of
    *                  xsd file for example, 1 is for xml version tag, 2 for open schema tag, etc
    * Function: from the given string s, it checks the structure of xsd file using multiple regex
    *           strings, and reports the error if any*/
    private ArrayList<Integer> parseXSD(String s, ArrayList<Integer> match){
        String xmlversion = "((\\<\\?)\\s*(xml)\\s+(version)\\s*(=)\\s*(\".*?\")\\s*(\\?\\>))";
        String attribDefault = "((attributeFormDefault)\\s*(=)\\s*(\"qualified\"))";
        String elementDefault = "((elementFormDefault)\\s*(=)\\s*(\"qualified\"))";
        String schema1 = "^(\\<(schema)\\s+"+ attribDefault + "\\s+" + elementDefault + "\\s*(\\>))$";
        String schema2 = "^(\\<(schema)\\s+"+ elementDefault + "\\s+" + attribDefault + "\\s*(\\>))$";
        String tableName = "^((<xsd:complexType)\\s+(name)\\s*(=)\\s*(\".+\")\\s*(\\>))$";
        String name = "(name(\\s*)(=)\\4(\\\"\\w+?\\\")\\2)";
        String type = "(type\\4\\5\\4(\\\"(xsd:)(string|int(eger)?|(date)|(decimal))\\\")\\2)";
        String date = "(\\2fraction\\4\\5\\4(\\\"\\d+\\\")\\4)";
        String fraction = "(\\2date\\4\\5\\4(\\\"mm\\/dd\\/(yy)?yy\\\"))";
        String maxo = "(maxOccurs\\4\\5(\\\"\\d+\\\")\\2)";
        String mino = "(minOccurs\\4\\5\\4(\\\"\\d+\\\")\\4)";
        String field3 = "^(<xsd:\\w+?(\\s+)" + name + type + maxo + mino + "\\4" + fraction + "?\\s*" + date+ "?\\s*(\\/>))$";
        String field4 = "^(<xsd:\\w+?(\\s+)" + type + name + maxo + mino + "\\4" + fraction + "?\\s*" + date+ "?\\s*(\\/>))$";
        String field1 = "^(<xsd:\\w+?(\\s+)name(\\s*)(=)\\3(\"\\w+?\")\\2(type\\3\\4\\3(\"(xsd:)(string|int(eger)?|(date)|(decimal))\")\\2)(maxOccurs\\3\\4(\"\\d+\")\\2)(minOccurs\\3\\4\\3(\"\\d+\")\\3)\\3(\\2fraction\\3\\4\\3(\"\\d+\")\\3)?\\s*(\\2date\\3\\4\\3(\"mm\\/dd\\/(yy)?yy\"))?\\s*(\\/>))$";
        String field2 = "^(<xsd:\\w+?(\\s+)name(\\s*)(=)\\3(\"\\w+?\")\\2(type\\3\\4\\3(\"(xsd:)(string|int(eger)?|(date)|(decimal))\")\\2)(minOccurs\\3\\4\\3(\"\\d+\")\\2)(maxOccurs\\3\\4(\"\\d+\")\\3)\\3(\\2fraction\\3\\4\\3(\"\\d+\")\\3)?\\s*(\\2date\\3\\4\\3(\"mm\\/dd\\/(yy)?yy\"))?\\s*(\\/>))$";
        String closeTable = "^(<\\/xsd:complexType>)$";
        String closeSchema = "^(<\\/schema>)$";
        String error = "***Error- ";
        int last = match.size()-1;
        if (match.get(last) == 1){
//            Matcher m = Pattern.compile(xmlversion).matcher(s);
//            boolean b = m.find();
            if(s.matches(xmlversion)){
                match.add(2);
            }else {
                System.out.println(error + "xml version tag should be on the very top of xsd file.");
                match.add(8);
            }
        } else if(match.get(last)==2){
            Matcher m = Pattern.compile(schema1).matcher(s);
            boolean b = m.find();
            if(b){
                match.add(3);
            }else {
                Matcher m2 = Pattern.compile(schema2).matcher(s);
                boolean b2 = m2.find();
                if(b2) {
                    match.add(3);
                }else {
                    System.out.println(error + "no schema tag provided in xsd file.");
                    match.add(8);
                }
            }
        } else if(match.get(last)==3){
            Matcher m = Pattern.compile(tableName).matcher(s);
            boolean b = m.find();
            if(b){
                match.add(4);
            }else {
                System.out.println(error + "no complexType provided in xsd file");
                match.add(8);
            }
        } else if(match.get(last) == 4){
            Matcher m = Pattern.compile(field1).matcher(s);
            boolean b = m.find();
            if(b){
                if(XSDErrorChecks(m.group())){
                    match.add(8);
                }else {
                    match.add(4);
                }
            }else {
                Matcher m2 = Pattern.compile(field2).matcher(s);
                boolean b2 = m2.find();
                if(b2) {
                    if(XSDErrorChecks(m2.group()))
                        match.add(8);
                    else {match.add(4);}
                }else {
                    Matcher m3 = Pattern.compile(closeTable).matcher(s);
                    boolean b3 = m3.find();
                    if(b3){
                        match.add(5);
                        match.add(6);
                    }else {
                        if(s.matches("(.*)(xsd:element)(.*)")){
                            System.out.println(error + "xsd:element syntax");
                        }else if(match.get(last)==4 && match.get(last-1)==4){
                            System.out.println(error + "complexType tag is not closed properly");
                        }else {
                            System.out.println(error + "no elements are provided under complexType in xsd file");
                        }
                        match.add(8);
                    }
                }
            }
        } else if(match.get(last) == 6){
            Matcher m = Pattern.compile(closeSchema).matcher(s);
            boolean b = m.find();
            if(b){
                match.add(7);
            }else {
                System.out.println(error + "schema tag not closed in xsd file"); match.add(8);
            }
        } else if(match.get(last) == 7){
            System.out.println(error + s + " string is found after closed schema tag. \n Program ignores everything after closed schema tag.");
            match.add(8);
        }
        return match;
    }//end of parseXSD

    /*Name: getTableFieldsAttribs
    * Parameters: String s
    * Function: it returns a dictionary of attributes and their value*/
    private HashMap getTableFieldsAttribs(String s){
        HashMap<String, String> map = new HashMap<String, String>();
        String name = "(.*)(?<=name)(=\\\")(\\w+)(.*)"; // 3rd group
        String type = "(.*)(?<=type)(=\\\")(\\w+:)(\\w+)(.*)"; // 4th group
        String maxO = "(.*)(?<=maxOccurs)(=\\\")(\\w+)(.*)"; //3rd group
        String minO = "(.*)(?<=minOccurs)(=\\\")(\\w+)(.*)"; //3rd group
        String fraction = "(.*)(?<=fraction)(=\\\")(\\w+)(.*)"; //3rd group
        String date = "(.*)(?<=date)(=\\\")(.*)(\\\")(.*)"; //3rd group
        Matcher m = Pattern.compile(name).matcher(s);
        Matcher m1 = Pattern.compile(type).matcher(s);
        Matcher m2 = Pattern.compile(maxO).matcher(s);
        Matcher m3 = Pattern.compile(minO).matcher(s);
        Matcher m4 = Pattern.compile(fraction).matcher(s);
        Matcher m5 = Pattern.compile(date).matcher(s);
        if(m.find()){
            map.put("name",m.group(3));
        }
        if(m1.find()){
            map.put("type",m1.group(4));
        }
        if(m2.find()){
            map.put("maxOccurs",m2.group(3));
        }
        if(m3.find()){
            map.put("minOccurs",m3.group(3));
        }
        if(m4.find()){
            map.put("fraction",m4.group(3));
        }
        if(m5.find()){
            map.put("date",m5.group(3));
        }
        return map;
    }//end of getTableFieldsAttribs

    /*Name: readReturnFileContents
    * Parameters: String fileName is a file name
    * Function: it scans the whole file and returns the content as a string*/
    private ArrayList<String> readReturnFileContents(String fileName){
        String startingDir = System.getProperty("user.dir");
        BufferedReader reader = null;
        String line = "";
        ArrayList<String> wholeFile = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(startingDir + "/" + fileName));
            while ((line = reader.readLine()) != null) {
                wholeFile.add(line);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(1);
        } catch (NullPointerException npe) {
            System.out.println(npe.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        return wholeFile;
    }//end of readReturnFileContents

    /*Name: inputXML
    * Parameters: String xmlName is the xml file name,
    *               ArrayList<Map> fieldsAttribs holds dictionary of attribute name and value for every field,
    *               String outputFileName is the file name that sql commands will be written into
    * Function: it scans the file, stores it in a String, calls parseXML and validateXML methods*/
    private boolean inputXML(String xmlName, ArrayList<Map> fieldsAttribs, String outputFileName){
        ArrayList<String> wholeFile = this.readReturnFileContents(xmlName);
        String first = wholeFile.get(0);
        String xmlversion = "((\\<\\?)\\s*(xml)\\s*(version)\\s*(=)\\s*(\\\".*?\\\")\\s*(\\?\\>))";
        if(first.matches(xmlversion)){
            wholeFile.remove(0); //remove xml version line from wholeFile
        }
        String t = "";
        for(String s : wholeFile){
            t+=s + "\n";
        }
        String closeTag = "(\\/\\w+>)";
        Matcher m = Pattern.compile(closeTag).matcher(t);
        while(m.find()){
            String search = m.group();
            String replacement = search + "\n";
            t= t.replace(search, replacement);
        }
        t = t.replaceAll("\\n(\\n)+", "\n");
        String openTagsSideBySide = "(<\\w+>)\\s*(<\\w+>)";
        Matcher m2 = Pattern.compile(openTagsSideBySide).matcher(t);
        while(m2.find()){
            String search = m2.group(2);
            String replacement = "\n" + search;
            t= t.replace(search, replacement);
        }
        t = t.replaceAll("\\n(\\n)+", "\n");
        if(this.parseXML(t, fieldsAttribs)){
            if(this.validateXML(t, fieldsAttribs, outputFileName))
            {
                return true;
            }
        }
        return false;
    }//end of inputXML

    /*Name: XSDErrorChecks
    * Parameters: String group contains a string of xsd:element tag and its attribute
    * Function: it returns true when any of these errors is found*/
    private boolean XSDErrorChecks(String group) {
        boolean xsdString = group.contains("xsd:string");
        boolean xsdDate = group.contains("xsd:date");
        boolean xsdInt = (group.contains("xsd:int") || group.contains("xsd:integer"));
        boolean xsdDecimal = group.contains("xsd:decimal");
        boolean maxOccur = group.matches("(.*)(maxOccurs)\\s*(=)\\s*(\"[0]*[1-9][0-9]*\")(.*)");
        boolean minOccur = group.matches("(.*)(minOccurs)\\s*(=)\\s*(\"[0]*[1-9][0-9]*\")(.*)");
        String error = "***Error- ";
        if((xsdString || xsdInt || xsdDecimal) && group.matches("(date)\\s*(=)")){
            System.out.println(error + group + "\nthis element type can not have date attribute");
            return true;
        }
        if ((xsdString || xsdDate || xsdInt) && group.contains("fraction")){
            System.out.println(error + group + "\nthis element type can not have fraction attribute");
            return true;
        }
        if(xsdDecimal && !group.contains("fraction")){
            System.out.println(error + group + "\nthis element type requires fraction attribute");
            return true;
        }
        if(xsdDecimal && !group.matches("(.*)(fraction)\\s*(=)\\s*(\"[0]*[1-9]\")(.*)")){
            System.out.println(error + group + "\nthis element type requires the value of fraction attribute " +
                    "to be more than 0");
            return true;
        }
        if(xsdDate && !group.matches("(.*)(date)\\s*(=)(.*)")){
            System.out.println(error + group + "\nthis element type requires date attribute");
            return true;
        }
        if(xsdDate && !group.matches("(.*)(date)\\s*(=)\\s*(\"(mm)\\/(dd)\\/(yy)?(yy)\")(.*)")){
            System.out.println(error + group + "\nthis element type requires date attribute to have value of " +
                    "either mm/dd/yy or mm/dd/yyyy");
        }
        if((xsdString || xsdInt || xsdDecimal ) && !maxOccur){
            System.out.println(error + group + "\nthis element requires maxOccurs attribute, and the value should be " +
                    "more than 0");
            return true;
        }
        if((xsdString || xsdInt || xsdDecimal ) && !minOccur){
            System.out.println(error + group + "\nthis element requires minOccurs attribute, and the value should be " +
                    "more than 0");
            return true;
        }
        if(xsdDate && !group.matches("(.*)(minOccurs)\\s*(=)\\s*(\"[1]\")(.*)")
                && !group.matches("(.*)(maxOccurs)\\s*(=)\\s*(\"[1]\")(.*)")){
            System.out.println(error + group + "\nthis element requires minOccurs and maxOccurs attributes, " +
                    "and their values should be exactly 1");
            return true;
        }
        return false;
    }//end of XSDErrorChecks

    /*Name: parseXML
    * Parameters: String tagList contains the whole xml file except the line that contains xml version tag
    * Function: it checks for and report syntax errors related missing tags, not closed tags, etc.
    *           If no error is found here, it returns true, or else it returns false*/
    private boolean parseXML(String tagList, ArrayList<Map> fieldAttribs)
    {
        String duplicateTags = "(<\\/(\\w+)>)(\\s*<(\\w+)>(\\w|\\/|\\s)+?<\\/\\4>\\s*)*?((<\\2>)\\w+(<\\/\\2>))";
        String openTagsRegex = "(<(\\w+)>)";
        String closeTagsRegex = "(<\\/(\\w+)?>)";
        Matcher m = Pattern.compile(duplicateTags).matcher(tagList);
        Matcher m1 = Pattern.compile(openTagsRegex).matcher(tagList);
        Matcher m2 = Pattern.compile(closeTagsRegex).matcher(tagList);
        ArrayList<String> openTags = new ArrayList<String>();
        ArrayList<String> closeTags = new ArrayList<String>();
        String error = "***Error- ";
        if(m.find()){
            String duplicate = m.group(6);
            System.out.println(error + "there is a duplicate tag : "+ duplicate);
            return false;
        }
        while(m1.find()){
            openTags.add(m1.group(2));
        }
        while(m2.find()){
            closeTags.add(m2.group(2));
        }
        ArrayList<String> stack = new ArrayList<String>();
        if(openTags.size() != closeTags.size()) {
            System.out.println(error + "Some opening and closing tags dont match in xml file.");
            return false;
        }
        int count = 1;
        stack.add(openTags.get(0));
        for(int i=1; i<openTags.size(); i++){
            stack.add(openTags.get(i)); count++;
            int last = stack.size()-1;
            if(i!=0 && closeTags.get(i-1).equals(stack.get(last))){
                stack.remove(last);
                count--;
            }
        }
        if(count==1 && stack.size()==1 && closeTags.get(closeTags.size()-1).equals(stack.get(0))){
            stack.remove(0);
            count--;
        }else {
            System.out.println(error + "Some opening and closing tags dont match in xml file.");
            return false;
        }
        if(stack.size()!=0){
            System.out.println(error + "Some opening and closing tags dont match in xml file.");
            return false;
        }
        Set<String> openSet = new HashSet<String>();
        Set<String> closeSet = new HashSet<String>();
        for(String s: openTags){
            openSet.add(s);
        }
        for (String s: closeTags){
            closeSet.add(s);
        }
        if((!openSet.containsAll(closeSet)) && (!closeSet.containsAll(openSet)) && (openSet.size() > openTags.size())
                && (closeSet.size() > closeTags.size())){
            System.out.println(error + "Some opening and closing tags dont match in xml file.");
            return false;
        }
        return true;
    }//end of parseXML


    /*Name: validateXML
    * Parameters: String xml holds the whole xml file as a string,
    *             ArrayList<Map> fieldsAttribs holds dictionary of attribute name and value for every field,
    *             String outputFileName is the file name that sql commands will be written into
    * Function: looks for semantic errors in the already scanned xml file to validate it against xsd file
    *           for example, undefined tags, missing tags, value not valid, etc
    *           if no error is there, it calls function printSQLCommands*/
    private boolean validateXML(String xml, ArrayList<Map> fieldsAttribs, String outputFileName) {
        String acceptedChars = "\\/\\-\\_\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\+\\=\\{\\[\\}\\]\\;\\:\\?\\.\\,";
        String pat = "(<(\\w+)?>([a-zA-Z0-9" + acceptedChars + " ]+?)<\\/(\\2)>)";
        String error = "***Error- ";
        ArrayList<Map> fieldValues = new ArrayList<Map>();
        Matcher m = Pattern.compile(pat).matcher(xml);
        Map<String, String> tempMap = new HashMap<String, String>();
        while(m.find()){
            if(tempMap.containsKey(m.group(2))){
                fieldValues.add(tempMap);
                tempMap = new HashMap<String, String>();
            }
            tempMap.put(m.group(2), m.group(3));
        }
        if(tempMap!=null){
            fieldValues.add(tempMap);
        }
        String tableName = fieldsAttribs.get(0).get("name").toString();
        fieldsAttribs.remove(0);
        int i = 0;
        ArrayList<Object> fieldsXSD = new ArrayList<Object>();
        for(Map attribs : fieldsAttribs){
            Object field = attribs.get("name");
            fieldsXSD.add(field);
        }
        for(Map record : fieldValues){
            i++;
            Set fieldsXML = record.keySet();
            if(fieldsXML.size() != fieldsXSD.size()){
                int count =0;
                if((count = Math.abs(fieldsXML.size() - fieldsXSD.size())) == 1)
                    System.out.println("In record #" + i + ", there is an undefined tags in xml file, and is ignored.");
                else
                    System.out.println("In record #" + i + ", there are "+count+" undefined tags in xml file, and are ignored.");
            }
        }
        for (Map record : fieldValues){
            for (Map attrib : fieldsAttribs){
                String column = attrib.get("name").toString();
                String type = attrib.get("type").toString();
                String max = attrib.get("maxOccurs").toString();
                String min = attrib.get("minOccurs").toString();
                String fraction = "";
                String date = "";
                if(attrib.containsKey("date")){
                    date = attrib.get("date").toString();
                }
                if(attrib.containsKey("fraction")){
                    fraction = attrib.get("fraction").toString();
                }
                String columnValue = "";
                if(record.containsKey(column))
                    columnValue = record.get(column).toString();
                else {
                    System.out.println(error + " program could not find "+ column + " in xml file.");
                    return false;
                }
                if(type.equals("string") || type.equals("integer") || type.equals("int")){
                    if(columnValue.length() > Integer.parseInt(max)){
                        System.out.println(error + column + " " + columnValue + " length is more than the limit "
                                + Integer.parseInt(max));
                        return false;
                    }
                    if(columnValue.length() < Integer.parseInt(min)){
                        System.out.println(error + column + " " + columnValue + " length is less than the limit "
                                + Integer.parseInt(min));
                        return false;
                    }
                }
                if(type.equals("decimal")){
                    String[] part = columnValue.split("\\.");
                    if(part.length > 2){
                        System.out.println(error + column + " " + columnValue + " is not a valid decimal ");
                        return false;
                    }
                    if(part[0].length() > Integer.parseInt(max)){
                        System.out.println(error + column + " " + columnValue + " length is more than the limit "
                                + Integer.parseInt(max));
                        return false;
                    }
                    if(part[0].length() < Integer.parseInt(min)){
                        System.out.println(error + column + " " + columnValue + " length is less than the limit "
                                + Integer.parseInt(min));
                        return false;
                    }
                    if(this.isNotNumeric(part[0])){
                        System.out.println(error + column + " " + columnValue + " not a valid decimal");
                        return false;
                    }
                    if(part.length == 2){
                        if(part[1].length() != Integer.parseInt(fraction)){
                            System.out.println(error + column + " " + columnValue + " fraction part should be "
                                    + Integer.parseInt(fraction) + " digit long");
                            return false;
                        }
                        if(this.isNotNumeric(part[1])){
                            System.out.println(error + column + " " + columnValue + " not a valid decimal");
                            return false;
                        }
                    }
                }
                if(type.equals("date")){
                    String[] ValuePart = columnValue.split("\\/");
                    String[] xsdPart = date.split("\\/");
                    if(xsdPart.length != 3){
                        System.out.println(error + "date should be in format either mm/dd/yy or mm/dd/yyyy " +
                                "in xsd file");
                        return false;
                    }
                    if((ValuePart.length != 3) || (ValuePart[0].length() != 2) || (ValuePart[1].length() != 2)){
                        System.out.println(error + column + " " + columnValue + " should be in format either " +
                                "mm/dd/yy or mm/dd/yyyy");
                        return false;
                    }
                    if(ValuePart[2].length() > xsdPart[2].length()){
                        System.out.println(error + column + " " + columnValue + " should be in " + date + " format");
                        return false;
                    }
                    if(this.isNotNumeric(ValuePart[0]) || this.isNotNumeric(ValuePart[1]) || this.isNotNumeric(ValuePart[2])){
                        System.out.println(error + column + " " + columnValue + " mm, dd, & [yy]yy values must " +
                                "be integers");
                        return false;
                    }
                }
            }
        }
        this.printSQLCommands(fieldValues, fieldsAttribs, tableName, outputFileName);
        return true;
    }//end of validateXML

    /*Name: isNotNumeric
    * Parameters: String s is the string that the function will run regex on
    * Function: it will run the regex on the given string and returns true or false based on the match
    *           if any match found, it will return true, or else it will return false*/
    private boolean isNotNumeric(String s) {
        return s.matches("(.*)\\D(.*)");
    }//end of isNotNumeric

    /*Name: printSQLCommands
    * Parameters: ArrayList<Map> fieldValues is dictionary of field names and their values as key and value respectively
    *           , ArrayList<Map> fieldAttribs is dictionary of attributes and their values as key and value respectively
    *           , String tableName is the table name for the queries
    *           , String fileName is the file name for those queries to write in
    * Function: using fieldValues and fieldAttribs, it creates sql INSERT INTO queries.
    *           If no error, it finds the file, or create one, and writes, or appends, those queries in it.*/
    private void printSQLCommands(ArrayList<Map> fieldValues, ArrayList<Map> fieldAttribs, String tableName,
                                  String fileName){
        ArrayList<String> commands = new ArrayList<String>();
        for(Map record : fieldValues){
            String command = "INSERT INTO " + tableName + " ( ";
            for(Map attrib : fieldAttribs){
                command += attrib.get("name").toString() + ", ";
            }
            command = command.substring(0,command.length()-2);
            command += " ) VALUES ( ";
            for(Map attrib : fieldAttribs){
                String column = attrib.get("name").toString();
                String value = record.get(column).toString();
                if(attrib.get("type").equals("string")){
                    command += "\"" + value + "\", ";
                }else {
                    command += value + ", ";
                }
            }
            command = command.substring(0,command.length()-2);
            command += " );";
            commands.add(command);
        }
        String currentDatabasePath = this.findCurrentDBPath();
        File file = new File(currentDatabasePath + "/" + fileName);
        try {
            if(!file.exists()) {
                boolean flag = file.createNewFile();
                if(flag) { System.out.println(fileName + " created!"); }
                else { System.out.println("***Error- " + fileName + " can not be created!"); }
            }
            PrintWriter tableWriter = new PrintWriter(new FileWriter(file, true));
            for (String query : commands) {
                tableWriter.append(query).append("\n");
            }
            tableWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end of printSQLCommands

    /*Name: findCurrentDBPath
    * Parameters: none
    * Functions: it returns a path for the currently opened database as a string
    *           : for example ~/src/DATABASE_NAME
    *           : it will always return the database name as upper case*/
    private String findCurrentDBPath(){
        String startingDir = System.getProperty("user.dir");	//get starting directory
        File database = new File(startingDir + "/src/currentDatabase.txt");

        String currentDatabasePath= null;
        try {
            if (database.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(database));
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    currentDatabasePath = startingDir + "/src/" + currentLine.trim().toUpperCase();
                }
                reader.close();
            }
            else{
                System.out.println("***Error- currentDatabase.txt file doesn't exits.");
            }
        }
        catch(Exception ex){
            System.out.println(ex);
        }
        return currentDatabasePath ;
    }//end of findCurrentDBPath
}

