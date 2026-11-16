import org.cprover.CProver;

// 6. A harness for SumX.sum with the contract of exercise 2.
//   ./bmc SumXCheck.check --unwind 10 --no-unwinding-assertions
public class SumXCheck {

    public static void check(int x) {
        
        CProver.assume(0 <= x && x <= 1000);

        int r = SumX.sum(x);

        assert r == x * (x + 1) / 2;
    }
}
