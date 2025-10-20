package org.ses.gadgetbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authors {




    String[] value() default {};

    String FROHOFF = "frohoff";
    String BK = "brunoK";
    String PWNTESTER = "pwntester";
    String CSCHNEIDER4711 = "cschneider4711";
    String MBECHLER = "mbechler";
    String JACKOFMOSTTRADES = "JackOfMostTrades";
    String MATTHIASKAISER = "matthias_kaiser";
    String GEBL = "gebl" ;
    String JACOBAINES = "jacob-baines";
    String JASINNER = "jasinner";
    String KULLRICH = "kai_ullrich";
    String TINT0 = "_tint0";
    String SCRISTALLI = "scristalli";
    String HANYRAX = "hanyrax";
    String EDOARDOVIGNATI = "EdoardoVignati";
    String JANG = "Jang";
    String ARTSPLOIT = "artsploit";
    String NAVALORENZO = "navalorenzo";
    String K4n5ha0 = "k4n5ha0";
    String SCICCONE = "sciccone";
    String ZEROTHOUGHTS = "zerothoughts";
    String HUGOW = "hugow";
    String BofeiC = "BofeiC";
    String YKOSTER = "ykoster";
    String MEIZJM3I = "meizjm3i";
    String YAROCHER = "yarocher";
    String SSEELEY = "steven_seeley";
    String RCALVI = "rocco_calvi";
    String YONGTAO = "yongtao_wan";
    String LZHANG = "lucas_zhang";
    String KCHAI = "kunzhe_zhai";
    String FE1W0 = "fe1w0";
    String TABBY = "tabby";
    String PYN3RD = "pyn3rd";
    String JWU = "junjieWu";
    String JARIJ = "jarij";
    String YihengZhang = "yiheng_zhang";

    public static class Utils {
        public static String[] getAuthors(AnnotatedElement annotated) {
            Authors authors = annotated.getAnnotation(Authors.class);
            if (authors != null && authors.value() != null) {
                return authors.value();
            } else {
                return new String[0];
            }
        }
    }
}