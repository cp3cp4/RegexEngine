import java.util.*;


/**
 * edges between states of FSA
 */
class Edge {
    private int from;
    private int to;
    private char ch;

    public Edge() {
    }


    public Edge(int from, int to, char ch) {
        this.from = from;
        this.to = to;
        this.ch = ch;
    }


    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", ch=" + ch +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from == edge.from && to == edge.to && ch == edge.ch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, ch);
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

class eNFA {
    public static final int MAX = 100;
    private String regularExpression;
    private boolean finalState[] = new boolean[MAX];
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private Set<Character> symbols = new TreeSet<Character>();
    private Vector<Pair>[] table = new Vector[MAX];
    private int stateCounter = 0;
    private Map<Integer, Integer> endState = new TreeMap<Integer, Integer>();

    public eNFA(String regularExpression) {
        super();
        this.regularExpression = regularExpression;
        setSymbols();
        symbols.add('$');

        generate(0, 0, this.regularExpression.length() - 1, false);

        settable();

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

    public void showSymbols() {
        System.out.print("Symbols : ");
        for(Character s:symbols) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    public Set<Character> getSymbols() {
        return symbols;
    }

    public void showStateCounter() {
        System.out.println("StateCounter : " + stateCounter);
    }

    private void setFinalState() {
        /*
         there are 2 types of final state
         1. state with no edge out
         2. state only contains edge way to itself
         */
        for (int i = 0; i <= stateCounter; i++) {
            int count = 0;

            // type 1
            if (table[i] == null) {
                finalState[i] = true;
				continue;
            }

            // type 2
            for (int j = 0; j < table[i].size(); j++)
                if (table[i].elementAt(j).to != i)
                    count++;
            if (count == 0)
                finalState[i] = true;
        }
    }

    public void showEdges() {
        for (Edge e : edges)
            System.out.println(e);
    }



    public boolean[] getFinalState() {
        return finalState;
    }

    /**
     * @param preState previous state
     * @param left index of left side of sub regular expression
     * @param right index of right side of sub regular expression
     * @param isClosure sub regular expression is closure or not
     * @return
     */
    private boolean generate(int preState, int left, int right, boolean isClosure) {

        if (left < 0 || right >= regularExpression.length()) {
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
                    if (!generate(preBracket, leftBracket + 1, rightBracket - 1, true)) return false;

                }
                // (regex)
                else {
                    preBracket = localPre;
                    if (generate(preBracket, leftBracket + 1, rightBracket - 1, false) == false)
                        return false;
                    i = rightBracket;
                }
            }
            // single symbol(character)
            else {
                if (regularExpression.charAt(i) == ')') continue;

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
                            edges.add(new Edge(localPre, ++stateCounter, '$'));
                            if (i == right) endState.put(preState, stateCounter);
                        }
                    }
                    localPre = stateCounter;
                    i++;// next character of * or +
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
                            edges.add(new Edge(localPre, ++stateCounter, regularExpression.charAt(i)));
                            if (i == right) endState.put(preState, stateCounter);
                        }
                    }
                    localPre = stateCounter;
                }
            }
        }

        return true;
    }

    private void settable() {
        for (Edge e: edges) {
            if (table[e.getFrom()] == null)
                table[e.getFrom()] = new Vector<Pair>();
            table[e.getFrom()].add(new Pair(e.getTo(), e.getCh()));
        }
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

}

class DFA {

    public DFA(eNFA enfa) {
        System.out.println("ready");
    }

    public boolean check(String input) {
        if("a".equals(input))
            return true;
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

        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(dfa.check(input));
        }

    }
}
