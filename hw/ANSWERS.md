# Homework: answers

Name(s): Jin Shangda 999011075

## OpenJML

### 1. Absolute

Q: What does OpenJML complain about before you write a contract, and for which input?

A: OpenJML point out that there may exist an integer overflow for some candidates in method 'abs'.

Q: Which `requires` did you need, and why? Could you avoid it by changing the `ensures`?

A: Since integers in java are actually stay in the interval [-2^31,2^31-1] where 2^31=2147483648, so we require n to be greater than -2147483648.
If we only hope to modify the `ensures`, we can write \result == n || \result == -n, but in this case the function will not output the absolute value in mathematics in all situations.

### 2. SumX

Q: Which proof obligations fail? Is the program wrong?

A: The program is correct, but OpenJML failed to prove the postcondition s=x(x+1)/2, since the loop invariant only declear the interval of i, but not the relationships in mathematics between s and i.

Q: Largest N for which `x <= N` verifies? What happens with N + 1, and why?

A: The returned value s should be an integer in java, means s <= 2^32-1 = 2147483647. So the leargest N = i should be 46340, gives that s = 46340*46341 = 2147441940 < 2147483647. 
For N = 46341, OpenJML may fail to verify the equation since 46341x46342 may directly lead to overflow before divided by 2.

### 3. Max

Q: Is the remaining failure caused by your invariants or by the code? (Your hypothesis.)

A: The code itself is incorrect, since the loop condition `i < a.length - 1` will only check the first `lenght-1` elements in the array, and skip the last one. So if the last element is coincidentally the greatest number in the array, the program will give the wrong answer. As a result, the postcondition is failed.


### 4. BinarySearch

Q: What does OpenJML report, in which method, and for which concrete call? How large must the array be?

A: OpenJML report the overflow issue in `midpoint`, since it will first count the sum of `lo + hi`, and this step may lead to overflow(e.g. lo == 1500000000 & hi = 2000000000). Meanwhile, even OpenJML accept the overflow, the sum of two number may also cause an overflow, like the sum of two positive integer may equal to an negative number, so the postcondition `lo <= result <= hi` is not guaraenteed.

Q: What happens without `/*@ pure @*/` on `midpoint`, and why?

A: Without `/*@ pure @*/`, OpenJML will assume that the program state will change after the function call. This may makes the loop invariant invalid and prevent the verification of  `search`. This is because declearing `midpoint` to be `pure` make sure that it does not modify the state of the program.

## JBMC

### 5. Max, again

Q: What input did JBMC find, and what goes wrong? Was your hypothesis of exercise 3 right?

A: JBMC set the input as {3, 32770}, and shows that the program will return 3 as the wrong answer. The previour hypothesis is correct.

Q: Smallest `--unwind` and `--max-nondet-array-length` that find it? Relation to the small scope hypothesis?

A: `--unwind 2` will still gives the counterexample, while `--unwind 1` will report `VERIFICATION SUCCESSFUL`; Meanwhile, `--max-nondet-array-length` also requires at least 2 elements, where `--unwind 2 --no-unwinding-assertions --max-nondet-array-length 2` will declear `VERIFICATION SUCCESSFUL`. So a small input of length 2 is sufficient to expose the bug.

Q: Why does `--unwind 5` with unwinding assertions fail on the fixed version? Smallest K with `--max-nondet-array-length 4`, and why not one less? What has been proved?

A: After fixing, `--unwind 5` without unwinding assertions will return `VERIFICATION FAILED` since in this case only first 6 elements in the array will be compared; Within `--max-nondet-array-length 4`, the smallest `--unwind` requirement will be 5. This is because MaxCheck will execute the loop for 4 times to check all the candidates, and one more time finish the loop.

### 6. SumX

Q: Which values of `x` were checked with `--unwind 10 --no-unwinding-assertions`? Why does it fail with unwinding assertions? Which K is a proof? Sizes and times for K = 10, 100 and that K.

A: In case of `--unwind 10 --no-unwinding-assertions`, only x in {1,2,...,10} are checked, so without `--no-unwinding-assertions` the check will fail since 0 <= x <= 1000 in `SumXCheck`. As a result, `K = 1001` allows the complete proof, but actually any integer greater than 1001 can also been set, but with more time costs.
| K (`--unwind`) | Verification Result | Properties Hold | Properties Fail | SAT Variables | SAT Clauses | Time |
|---:|---|---:|---:|---:|---:|---:|
| 10 | FAILED (Unwinding Assertion) | 38 | 1 | 4,638 | 13,644 | 0.01s |
| 100 | FAILED (Unwinding Assertion) | 38 | 1 | 9,587 | 30,260 | 0.05s |
| 1001 | VERIFICATION SUCCESSFUL | 39 | 0 | 66,870 | 666,136 | 3.98s |

Q: What did OpenJML need from you, and what did JBMC need?

A: OpenJML need tester to provide loop invariant and decreases clause, while JBMC require a harness and a unwinding bound that is great enough to check all the cases.

### 7. BinarySearch

Q: Why does JBMC miss the bug with the `search` harness? Could a larger bound help?

A: The `CProver.assume(a != null && a.length <= 5);` limited the length of array, and since `lo` & `hi` are array index, their values are also limited by the array length, so `lo + hi` can't overflow. As a result, simply increase `--unwind` of `--max-nondet-array-length` won't solve this issue, at least not practical since the array need to be extremely large, nearly 1 billion in length.

Q: What did the `midpoint` harness find?

A: The `midpoint` harness gives `VERIFICATION SUCCESSFUL` by using `return lo + (hi - lo) / 2;`, but after restore the original contract, it will report an overflow issue in case of both `lo` and `hi` are great enough.

## Comparison

### 8. Comparison

| program | OpenJML: result, what you wrote | JBMC: result, bound used |
| --- | --- | --- |
| SumX | Initially failed since the loop invariant was insufficient. After adding `0 <= i && i <= x and s == i * (i + 1) / 2` all proof obligations verified | `K=10` and `K=100` failed; `K=1001` successful. Harness constrained `0 <= x <= 1000` |
| Max | Initially failed due to `a.length - 1` in code and loop invariant missing, and successfully verified after fixing | `-unwind 5 --max-nondet-array-length 4`: VERIFICATION SUCCESSFUL. Smaller unwind bounds failed due to insufficient loop unrolling |
| BinarySearch | Found the midpoint integer overflow through the postcondition and ArithmeticOperationRange checks on `(lo + hi)` | `--unwind 6 --max-nondet-array-length 5`: VERIFICATION SUCCESSFUL despite the overflow bug. A separate midpoint harness with `--unwind 1` found the counterexample |

Q: When would you use each tool?

A: When I have a concrete precondition/postcondition or loop invariant I will choose OpenJML, since it is useful for verifying general properties beyond a limited set of test cases. On the other hand, I would use JBMC when I want to automatically explore program executions and find concrete counterexamples. It is particularly useful for detecting errors such as integer overflow and assertion violations. But in general, both tools can be used at the same time to debugging and verifing.