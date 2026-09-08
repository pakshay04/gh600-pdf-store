package com.example.pdfpay.service;
import org.springframework.stereotype.Service; import java.text.Normalizer; import java.util.*; import java.util.regex.*;
@Service public class LocalWordFilter {
 private static final Set<String> BLOCKED=Set.of("asshole","bastard","bitch","bullshit","crap","dick","fuck","fucker","fucking","motherfucker","shit","slut","whore","idiot","stupid","dumbass");
 private static final Map<Character,String> LEET=Map.of('0',"o",'1',"i",'3',"e",'4',"a",'5',"s",'7',"t",'@',"a",'$',"s");
 public boolean containsBlockedWords(String text){if(text==null)return false;String n=normalize(text);for(String w:BLOCKED){if(n.matches(".*\\b"+Pattern.quote(w)+"\\b.*")||n.contains(w))return true;}return false;}
 public String cleanForStorage(String text,int max){if(text==null)return "";String cleaned=text.replaceAll("[\\u0000-\\u0008\\u000B\\u000C\\u000E-\\u001F]","").trim(); return cleaned.substring(0,Math.min(cleaned.length(),max));}
 private String normalize(String s){String n=Normalizer.normalize(s.toLowerCase(Locale.ROOT),Normalizer.Form.NFKD).replaceAll("\\p{M}+","");StringBuilder b=new StringBuilder();for(char c:n.toCharArray()){String r=LEET.get(c);if(r!=null)b.append(r);else if(Character.isLetterOrDigit(c))b.append(c);else b.append(' ');}String collapsed=b.toString().replaceAll("[^a-z0-9]","");String spaced=b.toString().replaceAll("[^a-z0-9]+"," ");return spaced+" "+collapsed;}
}
