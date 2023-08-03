package org.example.nativesummary.jimplebuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.nativesummary.ir.Function;
import org.example.nativesummary.ir.Instruction;
import org.example.nativesummary.ir.Module;
import org.example.nativesummary.ir.inst.Call;
import org.example.nativesummary.ir.inst.Phi;
import org.example.nativesummary.ir.utils.Use;
import org.example.nativesummary.ir.utils.Value;
import org.example.nativesummary.ir.value.Null;
import org.example.nativesummary.ir.value.Str;
import org.example.nativesummary.ir.value.Top;
import soot.Scene;
import soot.SootClass;

public class PreLoadAnalysis {
    final static Logger logger = LoggerFactory.getLogger(PreLoadAnalysis.class);
    Module ss;
    Set<String> clz_names = new HashSet<>();

    public PreLoadAnalysis(Module ss) {
        this.ss = ss;
    }

    public void do_analysis() {
        for (Function func: ss.funcs) {
            for (Instruction inst: func.insts()) {
                if (!(inst instanceof Call)) continue;
                Call call = (Call) inst;
                if (call.target == null) {continue;}
                if (call.target.equals("FindClass")) {
                    Value strv = call.operands.get(1).value;
                    // search Phi for a string constant.
                    if (strv instanceof Phi) {
                        Str searched = searchPhiForStr((Phi)strv);
                        if (searched != null) {
                            strv = searched;
                        }
                    }
                    if (!(strv instanceof Str)) {
                        if (strv instanceof Top || strv instanceof Null) {
                            logger.warn("FindClass not accurate: {}", call);
                        } else {
                            logger.error("FindClass not using string constant!!: {}", call);
                        }
                        continue;
                    }
                    Str str = (Str) strv;
                    if (str == null || str.val == null || str.val.length() == 0) {
                        logger.error("FindClass arg Str value is null!");
                        continue;
                    }
                    String clz_name = str.val.replace('/', '.');
                    while(clz_name.length() > 0 && clz_name.charAt(0) == '[') {
                        clz_name = clz_name.substring(1);
                    }
                    if (clz_name.charAt(0) == 'L' && clz_name.charAt(clz_name.length()-1) == ';') {
                        clz_name = clz_name.substring(1, clz_name.length()-1);
                    }
                    // still contains invalid char
                    if (clz_name.indexOf(';') != -1 || clz_name.indexOf('[') != -1 || clz_name.indexOf(']') != -1) {
                        logger.error("invalid classname: "+clz_name);
                        continue;
                    }
                    clz_names.add(clz_name);
                }
            }
        }
        for(String clz: clz_names) {
            Scene.v().addBasicClass(clz, SootClass.SIGNATURES);
        }
    }

    public static Str searchPhiForStr(Phi strv) {
        for (Use u: ((Phi)strv).operands) {
            if (u.value instanceof Str) {
                return (Str) u.value;
            }
        }
        for (Use u: ((Phi)strv).operands) {
            if (u.value instanceof Phi) {
                return searchPhiForStr((Phi)u.value);
            }
        }
        return null;
    }

}
