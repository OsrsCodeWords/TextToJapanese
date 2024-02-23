package com.textToJapanese;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
enum Hiragana
{
    A("a", "a"),
    I("i", "i"),
    U("u", "u"),
    E("e", "e"),
    O("o", "o"),
    KA("ka", "ka"),
    KI("ki", "ki"),
    KU("ku", "ku"),
    KE("ke", "ke"),
    KO("ko", "ko"),
    SA("sa", "sa"),
    SHI("shi", "shi"),
    SU("su", "su"),
    SE("se", "se"),
    SO("so", "so"),
    TA("ta", "ta"),
    CHI("chi", "chi"),
    TSU("tsu", "tsu"),
    TE("te", "te"),
    TO("to", "to"),
    NA("na", "na"),
    NI("ni", "ni"),
    NU("nu", "nu"),
    NE("ne", "ne"),
    NO("no", "no"),
    HA("ha", "ha"),
    HI("hi", "hi"),
    FU("fu", "fu"),
    HE("he", "he"),
    HO("ho", "ho"),
    MA("ma", "ma"),
    MI("mi", "mi"),
    MU("mu", "mu"),
    ME("me", "me"),
    MO("mo", "mo"),
    YA("ya", "ya"),
    YU("yu", "yu"),
    YO("yo", "yo"),
    RA("ra", "ra"),
    RI("ri", "ri"),
    RU("ru", "ru"),
    RE("re", "re"),
    RO("ro", "ro"),
    WA("wa", "wa"),
    WO("wo", "wo"),
    NN("nn", "nn"),
    BA("ba", "ba"),
    PA("pa", "pa"),
    DE("de", "de"),
    DA("da", "da"),
    ZA("za", "za"),
    GA("ga", "ga"),
    PI("pi", "pi"),
    BI("bi", "bi"),
    DZI("dzi", "dzi"),
    JI("ji", "ji"),
    GI("gi", "gi"),
    PU("pu", "pu"),
    BE("be", "be"),
    ZE("ze", "ze"),
    GE("ge", "ge"),
    PO("po", "po"),
    BO("bo", "bo"),
    DO("do", "do"),
    zo("zo", "zo"),
    GO("go", "go"),
    LYO("lyo", "lyo"),
    LYA("lya", "lya"),
    LYU("lyu", "lyu"),
    BU("bu", "bu"),

    NYA("nya", "nya"),
    NYU("nyu", "nyu"),
    NYO("nyo", "nyo"),
    KYA("kya", "kya"),
    KYU("kyu", "kyu"),
    KYO("kyo", "kyo"),
    SHA("sha", "sha"),
    SHU("shu", "shu"),
    SHO("sho", "sho"),
    CHA("cha", "cha"),
    CHU("chu", "chu"),
    CHO("cho", "cho"),
    HYA("hya", "hya"),
    HYU("hyu", "hyu"),
    HYO("hyo", "hyo"),
    MYA("mya", "mya"),
    MYU("myu", "myu"),
    MYO("myo", "myo"),
    RYA("rya", "rya"),
    RYU("ryu", "ryu"),
    RYO("ryo", "ryo"),
    GYA("gya", "gya"),
    GYU("gyu", "gyu"),
    GYO("gyo", "gyo"),
    BYA("bya", "bya"),
    BYU("byu", "byu"),
    BYO("byo", "byo"),
    PYA("pya", "pya"),
    PYU("pyu", "pyu"),
    PYO("pyo", "pyo"),
    JYO("jyo", "jyo"),

    LTSU("ltsu", "ltsu"),

    ZU("zu", "zu")
    ;

    private static final Map<String, com.textToJapanese.Hiragana> HIRAGANAS;
    private static final ImmutableListMultimap<String, String> HIRAGANA_TRIGGERS;
    private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

    private final String triggerPhrase;

    private final String codepoint;

    static
    {
        ImmutableMap.Builder<String, com.textToJapanese.Hiragana> hiraganaBuilder = new ImmutableMap.Builder<>();
        ImmutableListMultimap.Builder<String, String> triggerBuilder = new ImmutableListMultimap.Builder<>();

        for (final com.textToJapanese.Hiragana hiragana : values())
        {
            String key = SPLITTER.splitToList(hiragana.triggerPhrase).get(0);
            triggerBuilder.put(key, hiragana.triggerPhrase);
            hiraganaBuilder.put(hiragana.triggerPhrase, hiragana);
        }

        HIRAGANAS = hiraganaBuilder.build();
        HIRAGANA_TRIGGERS = triggerBuilder.build();
    }

    Hiragana(String triggerPhrase, String codepoint) {
        this.triggerPhrase = triggerPhrase;
        this.codepoint = codepoint;
    }

    BufferedImage loadImage()
    {
        System.out.println("/" + codepoint + ".png");
        return ImageUtil.getResourceStreamFromClass(getClass(), "/" + codepoint + ".png");
    }

    /**
     * Gets a trigger phrase from a word
     * We prefer the longest phrase
     * @param key usually first word of a trigger phrase
     * @return sorted list of trigeger phrases
     */
    static List<List<String>> getTriggers(String key)
    {
        List<String> triggers = HIRAGANA_TRIGGERS.get(key);

        if (triggers == null)
        {
            return null;
        }

        List<List<String>> wordsToTry = new ArrayList<>();

        for (String trigger : triggers)
        {
            wordsToTry.add(SPLITTER.splitToList(trigger));
        }

        wordsToTry.sort((x,y) -> Integer.compare(y.size(), x.size()));

        return wordsToTry;
    }

    static com.textToJapanese.Hiragana getHiragana(String trigger)
    {
        return HIRAGANAS.get(trigger);
    }

    public String getTriggerPhrase() {
        return triggerPhrase;
    }

    public String getCodepoint() {
        return codepoint;
    }
}