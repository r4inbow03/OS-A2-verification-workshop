// 1. Write the contract of abs and verify it.
//   ./esc hw/Absolute.java
public class Absolute {

    // Returns the absolute value of n.
    /*@
    @ requires n > -2147483648;
    @ ensures \result >= 0;
    @ ensures \result == n || \result == -n;
    @*/
    // TODO (1): add requires/ensures clauses.
    public static int abs(int n) {
        if (n < 0) {
            return -n;
        }
        return n;
    }
}
