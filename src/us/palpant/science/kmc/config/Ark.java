package us.palpant.science.kmc.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class Ark implements Iterable<Entry<String,Object>> {
  
  private static final Logger log = Logger.getLogger(Ark.class);
  
  private Map<String,Object> data;

  public Ark(Map<String,Object> data) {
    this.data = data;
  }
  
  public Ark() {
    this(new TreeMap<String,Object>());
  }
  
  public static Ark load(Path p) throws IOException {
    Ark ark = new Ark();
    
    try (BufferedReader reader = Files.newBufferedReader(p, Charset.defaultCharset())) {
      String line = null;
      int lineNum = 0;
      String rootNode = "";
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        lineNum++;
        if (line.startsWith("#") || line.length() == 0) {
          // Skip comment and empty lines
        } else if (line.startsWith("}")) {
          log.debug("Closing section "+rootNode);
          int lastNode = rootNode.lastIndexOf('.', rootNode.length()-2);
          if (lastNode > 0) {
            rootNode = rootNode.substring(0, lastNode+1);
          } else {
            rootNode = "";
          }
        } else {  
          String[] keypair = line.split("=");
          if (keypair.length != 2) {
            throw new ArkException("Cannot parse Ark line "+lineNum+": "+line);
          }
          String key = keypair[0].trim();
          String value = keypair[1].trim();
          if (value.startsWith("{")) {
            rootNode += key+".";
          } else if (value.startsWith("[")) {
            value = value.substring(1, value.length()-1).trim();
            String[] array = value.split(" ");
            ark.set(rootNode+key, array);
          } else {
            ark.set(rootNode+key, value);
          }
        }
      }
    }
    
    return ark;
  }
  
  public static Ark fromArgv(String[] args) throws IOException {
    Ark ark = new Ark();
    boolean isInclude = false, isCfg = false;
    for (String s : args) {
      switch(s) {
      case "--include":
        isInclude = true;
        isCfg = false;
        break;
      case "--cfg":
        isCfg = true;
        isInclude = false;
        break;
      default:
        if (isInclude) {
          log.debug("Including file: "+s);
          ark.update(Ark.load(Paths.get(s)));
        } else if (isCfg) {
          log.debug("Setting config: "+s);
          String[] entry = s.split("=");
          if (entry.length != 2) {
            throw new ArkException("Cannot parse --cfg KEY=VALUE: "+s);
          }
          ark.set(entry[0], entry[1]);
        } else {
          throw new ArkException("Cannot parse args. Is "+s+" an --include or a --cfg?");
        }
      }
    }
    
    return ark;
  }
  
  public boolean has(String key) {
    return data.containsKey(key);
  }
  
  public Object get(String key) {
    log.debug("Getting "+key);
    Object ret = this;
    for (String k : key.split("\\.")) {
      if (!(ret instanceof Ark) || !((Ark)ret).has(k)) {
        throw new ArkException("Ark does not contain key "+key);
      }
      ret = ((Ark)ret).data.get(k);
    }
    
    return ret;
  }
  
  public void set(String key, Object value) {
    log.debug("Setting "+key+" = "+value);
    Ark ark = this;
    String[] keys = key.split("\\.");
    for (int i = 0; i < keys.length-1; i++) {
      if (!ark.has(keys[i]) || !(ark.get(keys[i]) instanceof Ark)) {
        ark.data.put(keys[i], new Ark());
      }
      ark = (Ark) ark.data.get(keys[i]);
    }
    
    ark.data.put(keys[keys.length-1], value);
  }
  
  public void update(Ark ark) {
    data.putAll(ark.data);
  }
  
  @Override
  public String toString() {
    return toString(0);
  }
  
  private String toString(int depth) {
    StringBuilder sb = new StringBuilder();
    StringBuilder indentation = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      indentation.append('\t');
    }
    for (Entry<String,Object> entry : data.entrySet()) {
      sb.append(indentation);
      sb.append(entry.getKey()).append(" = ");
      if (entry.getValue() instanceof Ark) {
        sb.append("{\n").append(((Ark)entry.getValue()).toString(depth+1)).append(indentation).append("}");
      } else if (entry.getValue() instanceof String[]) {
        sb.append("[ ").append(StringUtils.join((String[])entry.getValue(), ' ')).append(" ]");
      } else {
        sb.append(entry.getValue());
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  @Override
  public Iterator<Entry<String, Object>> iterator() {
    return data.entrySet().iterator();
  }
  
}
