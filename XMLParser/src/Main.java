

public class Main {

    public static void main(String[] args) {
        if(args.length != 3){
            System.out.println("should provide names of xsd, xml, and output files");
            System.exit(0);
        }
        System.out.println("xsd file name= " + args[0]);
        System.out.println("xml file name= " + args[1]);
        System.out.println("output file name= " + args[2]);
        XMLParser parser = new XMLParser();
        parser.startParsing(args[0], args[1], args[2]);
    }
}
