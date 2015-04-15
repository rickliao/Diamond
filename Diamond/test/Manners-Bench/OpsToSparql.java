import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpsToSparql {

	private LinkedList<Triple> deletes;
	private LinkedList<Triple> inserts;
	private LinkedList<Triple> where;
	private HashMap<String, String> binds;
	private HashMap<String, List<Triple>> negations;
	private ArrayList<String> subjMap;
	private int subjectId, varId;
	
	public OpsToSparql() {
		deletes = new LinkedList<Triple>();
		inserts = new LinkedList<Triple>();
		where = new LinkedList<Triple>();
		binds = new HashMap<String, String>();
		negations = new HashMap<String, List<Triple>>();
		subjMap = new ArrayList<String>();
		subjectId = 0; varId = 0;
	}
	
	public String getNextSubject() {
		if(subjectId == 0) {
			where.add(new Triple("ex:next_id", "ex:value", "?nid"));
			deletes.add(new Triple("ex:next_id", "ex:value", "?nid"));
			inserts.add(new Triple("ex:next_id", "ex:value", "?nid2"));
		}
		String id = "?_id" + subjectId;
		binds.put(id, "\"id\" + (?nid + " + subjectId + ")");
		binds.put("?nid2", "?nid+" + (++subjectId));
		return id;
	}
	
	public String getNextVar() {
		return "?_x" + varId++;
	}
	
	public Triple find(String subject, String predicate, List<Triple> list) {
		for(Triple t : list)
			if(t.subject.equals(subject) && t.predicate.equals(predicate))
				return t;
		return null;
	}
	
	private class Triple {
		
		protected String subject;
		protected String predicate;
		protected String object;
		
		public Triple(String s, String p, String o) {
			this.subject = s;
			this.predicate = p;
			this.object = o;
		}
		
		public String toString() {
			return subject + " " + predicate + " " + object + " .";
		}
	}
	
	public String[] parseArgList(String rule) {
		Pattern p = Pattern.compile("\\(compute ([^\\)]*)\\)");
		Matcher m = p.matcher(rule);
		List<String> computes = new LinkedList<String>();
		while (m.find()) {
		    String x = m.group(1).replaceAll("\\<([^\\>]*)\\>", "?$1");
		    computes.add(x.replaceAll(" ", ""));
		} for(String expr : computes) {
			String var = getNextVar();
			rule = rule.replaceFirst("\\(compute ([^\\)]*)\\)", var);
			binds.put(var, expr);
		}
	    
		String delim = " +\\(|\\(| +\\^| +\\)|\\)| +";
		String[] tok = rule.split(delim);
		int len = tok.length;
		String[] argVect = new String[len - 1];
		for(int i = 1; i < len; ++i)
			argVect[i-1] = tok[i];
		return argVect;
	}
	
	public String parseKey(String key) {
		return "ex:" + key;
	}
	
	public String parseValue(String value) {
		if(value.charAt(0) == '?') {
			return value;
		} if(value.charAt(0) == '<') {
			value = "?" + value.substring(1, value.length()-1);
		} else try {
			Integer.parseInt(value);
		} catch(Exception e1) {
			try {
				Double.parseDouble(value);
			} catch(Exception e2) {
				value = "\"" + value + "\"";
			}
		} return value;
	}
	
	public void parseMake(String rule) {
		if(rule == null) return;
		String[] argVect = parseArgList(rule);
		int len = argVect.length;
		if(len < 4) return;
		
		String subject = getNextSubject();
		inserts.add(new Triple(subject, "ex:type", parseKey(argVect[1])));
		for(int i = 2; i < len; i+=2) {
		  inserts.add(new Triple(subject, parseKey(argVect[i]), parseValue(argVect[i+1])));
		}
	}
	
	public void parseExists(String rule) {
		if(rule == null) return;
		String[] argVect = parseArgList(rule);
		int len = argVect.length;
		if(len < 3) return;
		
		String subject = (len == 3 ? parseKey(argVect[1]) : getNextVar());
		subjMap.add(subject);
		if(len == 3) { // variable
			where.add(new Triple(subject, "ex:value", parseValue(argVect[2])));
		} else { // structure
			where.add(new Triple(subject, "ex:type", parseKey(argVect[1])));
			for(int i = 2; i < len; i+=2) {
				where.add(new Triple(subject, parseKey(argVect[i]), parseValue(argVect[i+1])));
			}
		}
	}
	
	public void parseNotExists(String rule) {
		if(rule == null) return;
		String[] argVect = parseArgList(rule);
		int len = argVect.length;
		if(len < 3) return;
		
		String subject = (len == 3 ? parseKey(argVect[1]) : getNextVar());
		LinkedList<Triple> optional = new LinkedList<Triple>();
		negations.put(subject, optional);
		if(len == 3) { // variable
			optional.add(new Triple(subject, "ex:value", parseValue(argVect[2])));
		} else { // structure
			optional.add(new Triple(subject, "ex:type", parseKey(argVect[1])));
			for(int i = 2; i < len; i+=2) {
				optional.add(new Triple(subject, parseKey(argVect[i]), parseValue(argVect[i+1])));
			}
		}
	}
	
	public void parseModify(String rule) {
		if(rule == null) return;
		String[] argVect = parseArgList(rule);
		int len = argVect.length;
		if(len < 3) return;
	
		int subjectId = Integer.parseInt(argVect[1]) - 1;
		String subject = subjMap.get(subjectId);
		if(len == 3) { // variable
			String predicate = "ex:value";
			String object = find(subject, predicate, where).object;
			inserts.add(new Triple(subject, predicate, parseValue(argVect[2])));
			deletes.add(new Triple(subject, predicate, object));
		} else { // structure
			for(int i = 2; i < len; i+=2) {
				String predicate = parseKey(argVect[i]);
				String object = find(subject, predicate, where).object;
				inserts.add(new Triple(subject, predicate, parseValue(argVect[i+1])));
				deletes.add(new Triple(subject, predicate, object));
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[DELETE {\n");
		for(Triple d : deletes) {
			sb.append("    ").append(d.toString()).append("\n");
		}
		sb.append("} INSERT {\n");
		for(Triple i : inserts) {
			sb.append("    ").append(i.toString()).append("\n");
		}
		sb.append("} WHERE {\n");
		for(Triple w : where) {
			sb.append("    ").append(w.toString()).append("\n");
		}
		for(Entry<String, List<Triple>> el : negations.entrySet()) {
			sb.append("    OPTIONAL {\n");
			List<Triple> optional = el.getValue();
			for(Triple o : optional) {
				sb.append("    ").append(o.toString()).append("\n");
			}
			sb.append("    } FILTER(!").append(el.getKey()).append(") .\n");
		}
		for(Entry<String, String> b : binds.entrySet()) {
			sb.append("    BIND(").append(b.getValue());
			sb.append(" AS ").append(b.getKey()).append(") .\n");
		}
		sb.append("}]");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		
		System.out.println("PREFIX ex: <http://www.example.org/>\n");
		OpsToSparql parser; 
		
		parser = new OpsToSparql();
		parser.parseExists("(exists context start)");
		parser.parseExists("(exists guest ^name <n>)");
		parser.parseExists("(exists count ^c <c>)");
		parser.parseMake("(make seating ^seat1 1 ^name1 <n> ^name2 <n> ^seat2 1 ^id <c> ^pid 0 ^path_done yes)");
		parser.parseMake("(make path ^id <c> ^name <n> ^seat 1)");
		parser.parseModify("(modify 3 ^c (compute <c> + 1))");
		parser.parseModify("(modify 1 assign_seats)");
		System.out.println(parser);
		System.out.println();
		
		parser = new OpsToSparql();
		parser.parseExists("(exists context assign_seats)");
		parser.parseExists("(exists seating ^seat1 <seat1> ^seat2 <seat2> ^name2 <n2> ^id <id> ^pid <pid> ^path_done yes)");
		parser.parseExists("(exists guest ^name <n2> ^sex <s1> ^hobby <h1>)");
		parser.parseExists("(exists guest ^name <g2> ^sex <s2> ^hobby <h1>)"); // Fix filter TODO
		parser.parseExists("(exists count ^c <c>)");
		parser.parseNotExists("(not_exists path ^id <id> ^name <g2>)");
		parser.parseNotExists("(not_exists chosen ^id <id> ^name <g2> ^hobby <h1>)");
		parser.parseMake("(make seating ^seat1 <seat2> ^name1 <n2> ^name2 <g2> ^seat2 (compute <seat2> + 1) ^id <c> ^pid <id> ^path_done no)");
		parser.parseMake("(make path ^id <c> ^name <g2> ^seat (compute <seat2> + 1))");
		parser.parseMake("(make chosen ^id <id> ^name <g2> ^hobby <h1>)");
		parser.parseModify("(modify 5 ^c (compute <c> + 1))");
		parser.parseModify("(modify 1 make_path))");
		System.out.println(parser);
		System.out.println();
		
		parser = new OpsToSparql();
		parser.parseExists("(exists context make_path)");
		parser.parseExists("(exists seating ^id <id> ^pid <pid> ^path_done no)");
		parser.parseExists("(exists path ^id <pid> ^name <n1> ^seat <s>)");
		parser.parseNotExists("(not_exists path ^id <id> ^name <n1>)");
		parser.parseMake("(make path ^id <id> ^name <n1> ^seat <s>)");
		System.out.println(parser);
		System.out.println();
		
		parser = new OpsToSparql();
		parser.parseExists("(exists context make_path)");
		parser.parseExists("(exists seating ^path_done no))");
		parser.parseModify("(modify 2 ^path_done yes)");
		parser.parseModify("(modify 1 check_done)");
		System.out.println(parser);
		System.out.println();
		
		parser = new OpsToSparql();
		parser.parseExists("(exists context check_done)");
		parser.parseExists("(exists last_seat <l_seat>))");
		parser.parseExists("(exists seating ^seat2 <l_seat>)");
		parser.parseModify("(modify 1 print_results)");
		System.out.println(parser);
		System.out.println();
		
		parser = new OpsToSparql();
		parser.parseExists("(exists context check_done)");
		parser.parseModify("(modify 1 assign_seats)");
		System.out.println(parser);
		System.out.println();
	}
}
