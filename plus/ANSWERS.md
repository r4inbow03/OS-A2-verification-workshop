# Homework plus: answers

Name(s): Shangda Jin 999011075

### 1. Invariants from an LLM

| method | LLM | rounds | mistakes | final invariants by |
| --- | --- | --- | --- | --- |
| count | GPT-6 | 1 | timeout under 120s' limit due to `\num_of` | Human |
| reverse | GPT-6 | 3 | forget to verify the array indices in `i<=k<=j` && missing the equation `i+j=a.length-1` | GPT-6 |
| isqrt | GPT-6 | 1 | - | GPT-6 |
| dutchFlag | GPT-6 | 2 | forget to declear that all indices are between 0 and 2 | GPT-6 |


Q: Why is it safe to use invariants written by an LLM here, even if the LLM is often wrong?

A: This is because all the answers from LLM will be checked by JML instead of been trusted blindly. If an invariant is incorrect or too weak, the verifier reports a failure, so only invariants that pass verification will be accepted. This ensure the correctness of final answer drafted by LLM.

### 2. The Zune bug

Q: `--unwind 31 --no-unwinding-assertions` says SUCCESSFUL. Does that mean `yearOf` is correct for every day it checked?

A: All candidates are tested. But for those days that may lead to infinite loops, they are not asked to guarantee they will finish within 31 times of loops. So in fact, the potential bug is checked, but the corresponding result ignored.

Q: What does JBMC report without `--no-unwinding-assertions`? Is K too small, or is something else wrong?

A: It report that there exist some days that will loop for more than 31 times. In fact, the loop will execute for infinite of times, so it's not because K is too small, but the code is incorrect.

Q: What is wrong with the loop? Which date froze the Zune?

A: when `yearOf` enter a leapyear with `days==366`, the `if(isLeapYear(year))` will always be triggered, but never do the operations in `if(days > 366)`. As a result, the while loop will never exit and the invariant `year`&`days` change no more. There are multiple date which froze the Zune, and the least one is 1827.

Q: With your fix, what is the smallest K that gives VERIFICATION SUCCESSFUL with unwinding assertions? What exactly has been proved?

A: The smallest K is `K==30` for JBMC to report `VERIFICATION SUCCESSFUL`, this proves that for every input days in the specified range, `yearOf` terminates within the unwinding bound and returns a year in the range `[1980, 2010)`.

Q: A colleague changes `days > 366` to `days >= 366`. Which harness notices, and why?

A: Only `yearOfIsRight` notice the bug after "fixing". Because the modified condition will assigns the last day of a leap year to the following year incorrectly, but `yearOfInRange` won't detect this error since the returned year is still within the allowed range, while `yearOfIsRight` detects it because the computed day of the returned year becomes 0.

Q (★): Why does `./esc` say OK on the original? What changes with `decreases`?

A: Because the loop_invariant contract only check the mathematics relationships between variables in loop, but not guarantee that the progress of the loop is advanced each turn. So JML pass the test since the mathematics relationships are still correct when bug occur. After adding with `decreases`, `days` is asked to decrease in each turn of loop, so JML will report the issue since `days` no longer been decreased when bug occur.

### 3. Range

Q: Why does `size()` not verify? Give concrete values.

A: The main problem is overflow. Since both `hi`&`lo` are int in java,so both `hi-lo` and `(hi-lo)+1` may occur overflow. Meanwhile, the postcondition `//@ ensures \result == (long) hi - lo + 1;` use `long`, but the value returned is `int`, so when integer result overflow, the value would be different under `int` and `long`.

Q: Compare JBMC's counterexample with your answer above.

A: JBMC give the counterexample of `lo=-1985200127` and `hi=506855424`, and the mathematical interval size is 2492055552, which exceeds `Integer.MAX_VALUE`. So the `int` computation in `size()` overflows and returns -1802911744 instead of the expected result. This confirms that the method is incorrect for some valid ranges satisfying the class invariant `lo <= hi`.

### 4. StackAr

Q: Which tool reports your bug, and how? Could a bug get past OpenJML but not JBMC, or the other way around?

A: We delete `top--;` and both JBMC and JML detect this bug. JML report the postcondition since `pop()` no longer satisfies `top==\old(top)-1`. JBMC find a counterexample by doing `pop()` after `push()`, the harness expects the stack size to decrease, but the actual size remains unchanged. 
Of course it is possible for a bug to pass one tool but not the other. OpenJML may miss a bug if the method specification is too weak or does not express the required behavior, but JBMC may still detect it if the harness contains assertions that expose the incorrect behavior. On the other hand, JBMC may miss a bug if the failing execution requires more loop unrolling or a longer operation sequence than the chosen bound, while OpenJML may detect it through a method contract that it can prove or disprove independently of a particular execution sequence.

### 5. SafeLock

Q: Smallest K that finds a bug? Which sequence of events? What is wrong in the code?

A: The smallest K is `K==5` to find a bug; The problem sequence is firstly enter wrong pin for 2 times, then `Close`, and enter wrong pin for one more time, and enter the correct pin at last. In this case, we entered wrong pin for 3 times continuly, but the lock does not blocked. This is because `failed = 0` in `Close()` method stay outside the `if (state == State.OPEN)`, so it can be triggered whatever the state it is, as long as we triggered the `Close()` event.

Q: Formula size and time for K = 8, 10, 12.

A: After fixing `SafeLock.close()` by removing the unconditional reset of `failed`, JBMC successfully verifies `neverOpensAfterTooManyWrongPins` for all three bounds.

| K | SAT variables | SAT clauses | Solving time |
|---|---:|---:|---:|
| 8 | 46,050 | 68,865 | 0.48 s |
| 10 | 56,941 | 87,971 | 0.92 s |
| 12 | 67,836 | 107,563 | 1.75 s |

As K increases, both the number of SAT variables and clauses increase because JBMC unrolls the event loop more times and considers longer event sequences. The solving time also increases, from 0.48 seconds at K = 8 to 1.75 seconds at K = 12.

Q: Does SUCCESSFUL with K = 12 mean the property holds for every sequence? Can unwinding assertions help?

A: Due to some unknown reason, JBMC stuck when K > 10, so only cases of K <= 10 are tested, and proved to be correct.


