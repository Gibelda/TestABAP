import javax.xml.stream.*;
import java.io.IOException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        String name = "Гонконгский доллар";
        String uri = "http://www.cbr.ru/scripts/XML_daily.asp";
        try {
            URL url = new URL(uri);
            double value = valCurs(url, name);
            System.out.println("1 " + name + " = " + value + " Российский рубль");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double valCurs(URL url, String name) throws IOException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(url.openStream());
        try {
            int event = reader.getEventType();
            boolean nominalFlag = false, nameFlag = false, valueFlag = false;
            String curName = "";
            int nominal = 1;
            while (true) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        nameFlag = reader.getName().toString().equals("Name");
                        valueFlag = reader.getName().toString().equals("Value");
                        nominalFlag = reader.getName().toString().equals("Nominal");
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (reader.isWhiteSpace()) break;
                        curName = nameFlag ? reader.getText() : curName;
                        nominal = nominalFlag ? Integer.parseInt(reader.getText()) : nominal;
                        if (valueFlag && curName.equals(name))
                            return Double.parseDouble(reader.getText().replaceAll(",",".")) / nominal;
                        break;
                }
                if (!reader.hasNext())
                    break;
                event = reader.next();
            }
        } finally {
            reader.close();
        }
        return 0;
    }
}
