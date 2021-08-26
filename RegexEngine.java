import java.util.Scanner;

class eNFA {

    private String regularExpression;

    public eNFA(String regularExpression) {
        super();
        this.regularExpression = regularExpression;
        this.generate();
        System.out.println("ready");
    }

    private void generate() {

    }

    public void transition_table(boolean verbose) {
        if(verbose) {
            System.out.println("Transition Table of the Regex.");
        }
        System.out.println(

        );
    }

}

class DFA {

    public DFA(eNFA enfa) {

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

        if (args.length == 1 && "-v".equals(args[0])) verbose = true;
        else if (args.length > 1) {
            System.out.println("Wrong Parameters");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);

        String regularExpression = scanner.nextLine();

        eNFA efna = new eNFA(regularExpression);

        efna.transition_table(verbose);

        DFA dfa = new DFA(efna);

        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();
            System.out.println(dfa.check(input));
        }

    }
}
