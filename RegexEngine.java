import java.util.*;

/**
 * edges between states of FSA
 */
class Edge {
    private int from;
    private int to;
    private char ch;

    public Edge(int from, int to, char ch) {
        this.from = from;
        this.to = to;
        this.ch = ch;
    }


    public int getFrom() {
        return from;
    }


    public int getTo() {
        return to;
    }


    public char getCh() {
        return ch;
    }


    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", ch=" + ch +
                '}';
    }

}

class Pair {
    public int to;
    public char ch;

    public Pair(int to, char ch) {
        this.to = to;
        this.ch = ch;
    }

}

class MyHashSet extends HashSet<Integer> {
    public int state;

    @Override
    public boolean equals(Object obj) {
        boolean flag = true;

        if (this.size() != ((MyHashSet)obj).size() )
            flag = false;

        Iterator<Integer> it = ((MyHashSet)obj).iterator();
        while (it.hasNext()) {
            if (!this.contains(it.next()))
                flag = false;
        }

        return flag;
    }
}

class eNFA {
    private String regularExpression;
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private boolean finalState[] = new boolean[100];
    private Vector<Pair>[] table = new Vector[100];
    private int stateCounter = 0;
    private Set<Character> symbols = new TreeSet<Character>();
    private Map<Integer, Integer> endState = new TreeMap<Integer, Integer>();

    public eNFA(String regularExpression) {
        super();
        this.regularExpression = regularExpression;
        setSymbols();
        symbols.add('$');

        generate(0, 0, this.regularExpression.length() - 1, false);

        setTable();

        setFinalState();

    }

    private void setSymbols() {
        for(int i = 0; i < regularExpression.length(); i++) {
            if(Character.isDigit(regularExpression.charAt(i))
                    || Character.isLetter(regularExpression.charAt(i))
                    || Character.isSpaceChar(regularExpression.charAt(i)))
                symbols.add(regularExpression.charAt(i));
        }
    }

    private void setFinalState() {
        /*
         there are 2 types of final state
         1. state with no edge out
         2. state only contains edge way to itself
         */
        for (int i = 0; i <= stateCounter; i++) {

            // type 1
            if (table[i] == null) {
                finalState[i] = true;
                continue;
            }

            // type 2
            boolean flag = true;
            for (int j = 0; j < table[i].size(); j++)
                if (table[i].elementAt(j).to != i)
                    flag = false;

            finalState[i] = flag;
        }
    }

    private void setTable() {
        for (Edge e: edges) {
            if (table[e.getFrom()] == null)
                table[e.getFrom()] = new Vector<Pair>();
            table[e.getFrom()].add(new Pair(e.getTo(), e.getCh()));
        }
    }

    public Set<Character> getSymbols() {
        return symbols;
    }

    public Vector<Pair>[] getTable() {
        return table;
    }

    public boolean[] getFinalState() {
        return finalState;
    }

    public void transition_table(boolean verbose) {

        if(verbose) {

            // first line
            System.out.print("    ");
            for(Character s:symbols) {
                System.out.print("       " + s);
            }
            System.out.println();

            // rest line
            for(int i = 0; i <= stateCounter; i++) {

                if (i == 0) System.out.print(">");
                else if (finalState[i]) System.out.print("*");
                System.out.print("q" + i);


                String str = "";
                String subStr = "";
                if(table[i] != null) {
                    for(Character s:symbols) {

                        boolean flag = false;
                        subStr = "";
                        for(int j = 0; j < table[i].size(); j++) {
                            if(s.equals(table[i].elementAt(j).ch )) {
                                if(flag) {
                                    subStr = subStr + ",";
                                }

                                subStr = subStr + "q" + table[i].elementAt(j).to;
                                flag = true;
                            }

                        }
                        if(!flag) {
                            subStr = "";
                        }

                        while(subStr.length() < 8) subStr = " " + subStr;

                        str = str + subStr;
                    }
                }
                if (!finalState[i]) str = " " + str;
                if(i != 0) str = " " + str;
                System.out.println(str);
            }
        }
    }

    /**
     * @param preState previous state
     * @param left index of left side of sub regular expression
     * @param right index of right side of sub regular expression
     * @param isClosure sub regular expression is closure or not
     * @return generate succeeded or failed
     */
    private boolean generate(int preState, int left, int right, boolean isClosure) {

        if (left < 0) {
            System.out.println("[ERROR] : Wrong Regular Expression");
            System.exit(1);
        } else if (right >= regularExpression.length()) {
            System.out.println("[ERROR] : Wrong Regular Expression");
            System.exit(1);
        }

        int localPre = preState;

        // '|' inside brackets or not
        int insideBracket = 0;

        // handle alternation operator |
        for(int i = left; i <= right; i++) {
            if (regularExpression.charAt(i) == '(') insideBracket++;
            else if (regularExpression.charAt(i) == ')') insideBracket--;
            // regular expression is like "(sub_left)|(sub_right)"
            else if (regularExpression.charAt(i) == '|' && 0 == insideBracket) {
                if (generate(preState, left, i - 1, isClosure)
                    && generate(preState, i + 1, right, isClosure))
                    return true;
                else
                    return false;
            }
        }


        for(int i = left; i <= right; i++) {
            // sub regular expression inside brackets
            if (regularExpression.charAt(i) == '(') {
                int leftBracket = i;
                int rightBracket = -1;
                for (int j = i + 1; j <= right; j++) {
                    if (regularExpression.charAt(j) == ')') {
                        rightBracket = j;
                        break;
                    }
                }

                if (rightBracket == -1) {
                    System.out.println("[ERROR] : Wrong Regular Expression");
                    System.exit(1);
                }

                int preBracket = -1;
                // (regex)* or (regex)+
                if (rightBracket + 1 <= right &&
                        (regularExpression.charAt(rightBracket + 1) == '*'
                                || regularExpression.charAt(rightBracket + 1) == '+')) {
                    i = rightBracket + 1;

                    edges.add(new Edge(localPre, ++stateCounter, '$'));
                    localPre = stateCounter;
                    preBracket = stateCounter;

                    if(regularExpression.charAt(rightBracket + 1) == '*') {
                        edges.add(new Edge(localPre, ++stateCounter, '$'));
                        localPre = stateCounter;
                    }

                    // handle regex inside ()*
                    boolean flag = generate(preBracket, leftBracket + 1, rightBracket - 1, true);
                    if (flag == false) return false;

                }
                // (regex)
                else {
                    preBracket = localPre;
                    boolean flag = generate(preBracket, leftBracket + 1, rightBracket - 1, false);
                    if (flag == false) return false;
                    i = rightBracket;
                }
            }
            // single symbol(character)
            else {
                char head = regularExpression.charAt(i);
                if (head == ')' ) continue;

                if (i + 1 <= right &&
                        (regularExpression.charAt(i + 1) == '*'
                        || regularExpression.charAt(i + 1) == '+')) {

                    if(regularExpression.charAt(i + 1) == '*')
                        edges.add(new Edge(localPre, ++stateCounter, '$'));
                    if(regularExpression.charAt(i + 1) == '+')
                        edges.add(new Edge(localPre, ++stateCounter, regularExpression.charAt(i)));

                    localPre = stateCounter;
                    edges.add(new Edge(localPre, localPre, regularExpression.charAt(i)));

                    if (i + 1 == right && isClosure)
                        edges.add(new Edge(localPre, preState, '$'));
                    //not Closure
                    else {
                        if (endState.containsKey(preState))
                            edges.add(new Edge(localPre, endState.get(preState), '$'));
                        else {
                            stateCounter = stateCounter + 1;
                            if (right == i) endState.put(preState, stateCounter);
                            Edge e = new Edge(localPre, stateCounter, '$');
                            edges.add(e);
                        }
                    }
                    localPre = stateCounter;
                    // next character of * or +
                    i++;
                }
                else {
                    if (i == right && isClosure) {
                        edges.add(new Edge(localPre, preState, regularExpression.charAt(i)));
                    }
                    //not Closure
                    else {
                        if (endState.containsKey(preState))
                            edges.add(new Edge(localPre, endState.get(preState), regularExpression.charAt(i)));
                        else {
                            stateCounter = stateCounter + 1;
                            if (right == i) endState.put(preState, stateCounter);
                            Edge e = new Edge(localPre, stateCounter, regularExpression.charAt(i));
                            edges.add(e);
                        }
                    }
                    localPre = stateCounter;
                }
            }
        }

        return true;
    }

    // used in debug
    public void showSymbols() {
        System.out.print("Symbols : ");
        for(Character s:symbols) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    // used in debug
    public void showStateCounter() {
        System.out.println("showStateCounter : " + stateCounter);
    }

    // used in debug
    public void showEdges() {
        for (Edge e : edges)
            System.out.println(e);
    }

}

class DFA {

    private Set<MyHashSet> set = new HashSet<MyHashSet>();
    private MyHashSet subSet = null;
    private boolean finalState[] = new boolean[100];
    private Set<Character> symbols = new TreeSet<Character>();
    private Vector<Pair>[] table = new Vector[100];
    private Set<Edge> edgeSet = new HashSet<Edge>(); // visited or not
    private Set<Edge> edges = new HashSet<Edge>();
    private boolean[] dfaFinalState = new boolean[100];
    private Queue<MyHashSet> q = new LinkedList<MyHashSet>();
    private int stateCounter = 0;

    public DFA(eNFA enfa) {
        finalState = enfa.getFinalState();
        symbols = enfa.getSymbols();
        table = enfa.getTable();
        symbols.remove('$');

        generate();

    }

    private void generate() {
        init();
        while (q.peek() != null) {
            MyHashSet myset = q.peek();
            q.remove();
            for (Character ch : symbols) {
                subSet = new MyHashSet();

                Iterator it = myset.iterator();
                while (it.hasNext()) {
                    edgeSet.clear();
                    dfs((Integer) it.next(), ch);
                }
                if (!subSet.isEmpty()) {
                    if (set.contains(subSet)) {

                        for (MyHashSet s : set) {
                            if(subSet.equals(s)) {
                                subSet = s;
                                break;
                            }
                        }
                    } else {
                        q.add(subSet);
                        set.add(subSet);
                        subSet.state = stateCounter++;
                        isFinalState(subSet, stateCounter - 1);
                    }
                    edges.add(new Edge(myset.state, subSet.state, ch));
                }
            }
        }
    }

    private void init() {
        edges.clear();
        subSet = new MyHashSet();
        subSet.add(0);
        subSet.state = stateCounter++;
        dfs(0, '$');
        isFinalState(subSet, 0);
        set.add(subSet);
        q.add(subSet);
    }

    private void dfs(int from, char ch) {
        if (table[from] == null)
            return;

        for (int i = 0; i < table[from].size(); i++) {
            Edge edge = new Edge(from, table[from].elementAt(i).to, table[from].elementAt(i).ch);
            if (!edgeSet.contains(edge) && table[from].elementAt(i).ch == ch) {
                edgeSet.add(edge);
                subSet.add(table[from].elementAt(i).to);
                dfs(table[from].elementAt(i).to, '$');
            }
        }
    }

    public void isFinalState(Set<Integer> subSet, int s) {
        for(Integer i : subSet) {
            if (finalState[i] == true) dfaFinalState[s] = true;
        }
    }

    public boolean check(int state, String input) {

        char ch = input.charAt(0);

        if (dfaFinalState[state] && ch == '#') return true;

        for (Edge e : edges) {
            if(e.getFrom() == state && e.getCh() == ch) {
                return check(e.getTo(), input.substring(1));
            }
        }

        return false;
    }
}

public class RegexEngine {
    public static void main(String[] args) {

        boolean verbose = false;

        if(args.length == 1 && "-v".equals(args[0])) verbose = true;
        else if(args.length > 1) {
            System.out.println("Wrong Parameters");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        String regularExpression = scanner.nextLine();

        eNFA enfa = new eNFA(regularExpression);

        enfa.transition_table(verbose);

        DFA dfa = new DFA(enfa);

        System.out.println("ready");

        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();
            // use # as end symbol
            System.out.println(dfa.check(0, input + "#"));
        }

    }
}