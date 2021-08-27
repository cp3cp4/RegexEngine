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

class eNFA {

    private String regularExpression;
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private Set<Character> symbols = new TreeSet<Character>();
    private int stateCounter = 0;
    private Map<Integer, Integer> endState = new TreeMap<Integer, Integer>();

    public eNFA(String regularExpression) {
        super();
        this.regularExpression = regularExpression;
        setSymbols();

        generate(0, 0, this.regularExpression.length() - 1, false);

        //checkFinalState();

        for (Edge e : edges)
            System.out.println(e);
    }

    private void setSymbols() {
        for(int i = 0; i < regularExpression.length(); i++) {
            if(Character.isDigit(regularExpression.charAt(i))
                    || Character.isLetter(regularExpression.charAt(i))
                    || Character.isSpaceChar(regularExpression.charAt(i)))
                symbols.add(regularExpression.charAt(i));
        }
    }

    private void checkFinalState() {
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

                    edges.add(new Edge(localPre, ++stateCounter, 'ℰ'));
                    localPre = stateCounter;
                    preBracket = stateCounter;

                    if(regularExpression.charAt(rightBracket + 1) == '*') {
                        edges.add(new Edge(localPre, ++stateCounter, 'ℰ'));
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
                        edges.add(new Edge(localPre, ++stateCounter, 'ℰ'));
                    if(regularExpression.charAt(i + 1) == '+')
                        edges.add(new Edge(localPre, ++stateCounter, regularExpression.charAt(i)));

                    localPre = stateCounter;
                    edges.add(new Edge(localPre, localPre, regularExpression.charAt(i)));

                    if (i + 1 == right && isClosure)
                        edges.add(new Edge(localPre, preState, 'ℰ'));
                    //not Closure
                    else {
                        if (endState.containsKey(preState))
                            edges.add(new Edge(localPre, endState.get(preState), 'ℰ'));
                        else {
                            edges.add(new Edge(localPre, ++stateCounter, 'ℰ'));
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

    public void transition_table(boolean verbose) {
        if(verbose) {
            System.out.println("Transition Table of the Regex.");
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