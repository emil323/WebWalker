package scripts.Parser;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Properties {

    private HashMap<String, Object> properties = new HashMap<>();


    public Properties(NamedNodeMap nodeMap) {
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Node node = nodeMap.item(i);
            properties.put(node.getNodeName(), node.getNodeValue());
        }
    }

    public boolean has(String key) {
        return this.properties.containsKey(key);
    }

    public Object get(String key) {
        if (properties.containsKey(key)) {
            return properties.get(key);
        }
        throw new NullPointerException("Obstacle property not found:'" + key + "' " + this.toString());
    }

    public String getString(String key) {
        return (String) this.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(this.getString(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(this.getString(key));
    }


    public int[] getInts(String key) {
        String text = this.getString(key);
        String [] split = text.split(Pattern.quote("|"));
        int [] ints = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            ints[i] = Integer.parseInt(split[i]);
        }
        return ints;
    }

    public String[] getStrings(String key) {
        String text = this.getString(key);
        return text.split(Pattern.quote("|"));
    }

    public int[] getBounds(String key) {
        String text = this.getString(key);
        text = text.replaceAll("\\s", ""); //Remove whitespace
        String[] split = text.split(","); //Split string by comma
        int len = split.length;
        int[] bounds = new int[len];
        for (int i = 0; i < len; i++) {
            bounds[i] = Integer.parseInt(split[i]);
        }
        return bounds;
    }

    @Override
    public String toString() {
        return "Properties{" +
                "properties=" + properties +
                '}';
    }
}
