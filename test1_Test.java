import static org.junit.Assert.assertEquals;
import org.junit.Test;

/*
For each regular expression, test with 5 satisfied inputs (t[]) and 5 unsatisfied inputs (f[])
 */
public class test1_Test {

    @Test
    public void test1_1() {
        DFA dfa = new DFA(new eNFA("(ab)*|c+"));
        String t[] = {"", "ab", "abab", "c", "cccc"};
        String f[] = {"a", "aa", "b", "ac", "bc"};
        for(int i = 0; i < 5; i++) {
            assertEquals(true, dfa.check(0, t[i] + "#"));
            assertEquals(false, dfa.check(0, f[i] + "#"));
        }
    }

    @Test
    public void test1_2() {
        DFA dfa = new DFA(new eNFA("(Zs)|(wa)*|(I|Q)"));
        String t[] = {"", "Zs", "wa", "wawa", "Q"};
        String f[] = {"ZsI", "waw", "IQ", "QI", "i"};
        for(int i = 0; i < 5; i++) {
            assertEquals(true, dfa.check(0, t[i] + "#"));
            assertEquals(false, dfa.check(0, f[i] + "#"));
        }

    }

    @Test
    public void test1_3() {
        DFA dfa = new DFA(new eNFA("(0|1|6)|(2|3)|(4|5)"));
        String t[] = {"0", "1", "2", "3", "4"};
        String f[] = {"", "7", "00", "a", "A"};
        for(int i = 0; i < 5; i++) {
            assertEquals(true, dfa.check(0, t[i] + "#"));
            assertEquals(false, dfa.check(0, f[i] + "#"));
        }

    }

    @Test
    public void test1_4() {
        DFA dfa = new DFA(new eNFA("Z+(A|a)*SOS"));
        String t[] = {"ZSOS", "ZZZZZZSOS", "ZAAASOS", "ZaaSOS", "ZaSOS"};
        String f[] = {"SOS", "AaSOS", "ZSOSSOS", "ZAaOS", "ZZZZZZ"};
        for(int i = 0; i < 5; i++) {
            assertEquals(true, dfa.check(0, t[i] + "#"));
            assertEquals(false, dfa.check(0, f[i] + "#"));
        }

    }

    @Test
    public void test1_5() {
        DFA dfa = new DFA(new eNFA("a*(b c*)*d*"));
        String t[] = {"", "b ", "b c", "ab cb d", "aaaab cb cdddd"};
        String f[] = {" ", "cb", "bcd", "acbd", "abcb"};
        for(int i = 0; i < 5; i++) {
            assertEquals(true, dfa.check(0, t[i] + "#"));
            assertEquals(false, dfa.check(0, f[i] + "#"));
        }

    }
}
