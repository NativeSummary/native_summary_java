package org.example.nativesummary.jimplebuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.nativesummary.ir.Module;
import org.example.nativesummary.ir.Function;
import org.example.nativesummary.ir.Instruction;
import org.example.nativesummary.ir.inst.Phi;
import org.example.nativesummary.ir.utils.Use;
import org.example.nativesummary.ir.utils.Value;
import org.example.nativesummary.ir.value.Number;

public class ConstantPreferInt {
    public static void process(Module summary) {
        for (Function func: summary.funcs) {
            for(int i=0;i<func.insts().size();i++) {
                Instruction inst = func.insts().get(i);
                for (Use u: inst.operands) {
                    convertInteger(u);
                }
            }
        }
    }

    public static void convertInteger(Use u) {
        Value val = u.value;
        if (!(val instanceof Number)) {
            return;
        }
        Number num = (Number) val;
        if (num.val instanceof java.lang.Long) {
            try {
                num.val = Integer.valueOf(Math.toIntExact(num.val.longValue()));
            } catch (ArithmeticException e) {
                return;
            }
        }
	}
}
