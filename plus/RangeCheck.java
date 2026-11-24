import org.cprover.CProver;

// 3. A harness for the record Range.
//   ./bmc RangeCheck.size --unwind 1
public class RangeCheck {

    public static void size(int lo, int hi) {
        CProver.assume(lo <= hi);
        CProver.assume((long)hi - lo + 1 <= Integer.MAX_VALUE);
        
        Range r = new Range(lo, hi);

        int result = r.size();

        assert result == (long)hi - lo + 1;
    }
}