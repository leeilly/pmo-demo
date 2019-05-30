package com.pulmuone.demo.api.search.analyzer;

public class KoreanChosungParser extends AbstractKoreanParser {

    @Override
    protected void processForKoreanChar(StringBuilder sb, char chosung, char jungsung, char jongsung) {
        sb.append(chosung);
    }

    @Override
    protected void processForOther(StringBuilder sb, char eachToken) {
        sb.append(eachToken);
    }
}
