package ru.kuchanov.odnako;

import org.xml.sax.XMLReader;

import android.text.Editable;
import android.text.Spannable;
import android.text.Html.TagHandler;
import android.text.style.StrikethroughSpan;

public class MyHtmlTagHandler implements TagHandler {

	boolean first= true;
	String parent=null;
	int index=1;
	@Override
    public void handleTag(boolean opening, String tag, Editable output,
            XMLReader xmlReader) {
        if(tag.equalsIgnoreCase("strike") || tag.equals("s")) {
            processStrike(opening, output);
        }
        ////////
        if(tag.equals("ul")) parent="ul";
        else if(tag.equals("ol")) parent="ol";
        if(tag.equals("li")){
            if(parent.equals("ul")){
                if(first){
                    output.append("\n\t•");
                    first= false;
                }else{
                    first = true;
                }
            }
            else{
                if(first){
                    output.append("\n\t"+index+". ");
                    first= false;
                    index++;
                }else{
                    first = true;
                }
            }   
        }
        ///////
    }

    private void processStrike(boolean opening, Editable output) {
        int len = output.length();
        if(opening) {
            output.setSpan(new StrikethroughSpan(), len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Object obj = getLast(output, StrikethroughSpan.class);
            int where = output.getSpanStart(obj);

            output.removeSpan(obj);

            if (where != len) {
                output.setSpan(new StrikethroughSpan(), where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private Object getLast(Editable text, Class<?> kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            for(int i = objs.length;i>0;i--) {
                if(text.getSpanFlags(objs[i-1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i-1];
                }
            }
            return null;
        }
    }

	


}