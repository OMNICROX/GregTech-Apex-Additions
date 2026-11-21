package com.omnicrox.gtaa.common.infernal;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The 27 Infernal Mobs modifiers with the GT New Horizons fork's names, title prefixes / suffixes, mob blacklists
 * and "don't mix with" rules. GTNH ships with Ender, Lifesteal, Ninja, Rust and Sticky turned off (see
 * InfernalConfig.DISABLED_MODIFIERS). Behaviour lives in InfernalMobs.
 */
public enum InfernalModifier {
    ONE_UP("1UP", p("recurring", "undying", "twinlived"), p("ofRecurrence", "theUndying", "oftwinLives"), InfernalModifier::creeper),
    ALCHEMIST("Alchemist", p("witchkin", "brewing", "singed"), p("theWitchkin", "theBrewmaster", "theSinged"), null),
    BERSERK("Berserk", p("reckless", "raging", "smashing"), p("ofRecklessness", "theRaging", "ofSmashing"), InfernalModifier::creeper),
    BLASTOFF("Blastoff", p("thumping", "trolling", "byebye"), p("ofMissionControl", "theNASA", "ofWEE"), null),
    BULWARK("Bulwark", p("turtling", "defensive", "armoured"), p("ofTurtling", "theDefender", "ofeffingArmor"), null),
    CHOKE("Choke", p("Sith Lord", "Dark Lord", "Darth"), p("ofBreathlessness", "theAnaerobic", "ofDeprivation"), null),
    CLOAKING("Cloaking", p("stalking", "unseen", "hunting"), p("ofStalking", "theUnseen", "thePredator"), e -> e instanceof Spider),
    DARKNESS("Darkness", p("dark", "shadowkin", "eclipsed"), p("ofDarkness", "theShadow", "theEclipse"), null),
    ENDER("Ender", p("enderborn", "tricky"), p("theEnderborn", "theTrickster"), null),
    EXHAUST("Exhaust", p("exhausting", "draining"), p("ofFatigue", "theDrainer"), null),
    FIERY("Fiery", p("burning", "toasting"), p("ofConflagration", "thePhoenix", "ofCrispyness"), null),
    GHASTLY("Ghastly", p("bombing", "fireballsy"), p("OMFGFIREBALLS", "theBomber", "ofBallsofFire"), null),
    GRAVITY("Gravity", p("repulsing", "sproing"), p("ofRepulsion", "theFlipper"), null),
    LIFESTEAL("Lifesteal", p("vampiric", "transfusing", "bloodsucking"), p("theVampire", "ofTransfusion", "theBloodsucker"), InfernalModifier::creeper),
    NINJA("Ninja", p("totallyzen", "innerlypeaceful", "Ronin"), p("theZenMaster", "ofEquilibrium", "ofInnerPeace"), null),
    POISONOUS("Poisonous", p("poisonous", "stinging", "despoiling"), p("ofVenom", "thedeadlyChalice"), null),
    QUICKSAND("Quicksand", p("slowing", "Quicksand"), p("ofYouCantRun", "theSlowingB"), null),
    REGEN("Regen", p("regenerating", "healing", "nighunkillable"), p("ofWTFIMBA", "theCancerous", "ofFirstAid"), null),
    RUST("Rust", p("rusting", "decaying"), p("ofDecay", "theEquipmentHaunter"), null),
    SAPPER("Sapper", p("hungering", "starving"), p("ofHunger", "thePaleRider"), null),
    SPRINT("Sprint", p("sprinting", "swift", "charging"), p("ofBolting", "theSwiftOne", "ofbeinginyourFace"), null),
    STICKY("Sticky", p("thieving", "snagging", "quickfingered"), p("ofSnagging", "theQuickFingered", "ofPettyTheft", "yoink"), InfernalModifier::creeper),
    STORM("Storm", p("striking", "thundering", "electrified"), p("ofLightning", "theRaiden"), null),
    VENGEANCE("Vengeance", p("thorned", "thorny", "spiky"), p("ofRetribution", "theThorned", "ofStrikingBack"), null),
    WEAKNESS("Weakness", p("apathetic", "deceiving"), p("ofApathy", "theDeceiver"), null),
    WEBBER("Webber", p("ensnaring", "webbing"), p("ofTraps", "theMutated", "theSpider"), null),
    WITHER("Wither", p("withering"), p("ofDarkSkulls", "Doomskull"), null);

    /** Name shown in the boss-bar modifier list (and used in configs / commands, case-insensitive) */
    public final String modName;
    public final String[] prefixes, suffixes;
    private final Predicate<LivingEntity> blacklist;

    InfernalModifier(String modName, String[] prefixes, String[] suffixes, Predicate<LivingEntity> blacklist) {
        this.modName = modName;
        this.prefixes = prefixes;
        this.suffixes = suffixes;
        this.blacklist = blacklist;
    }

    private static String[] p(String... s) {
        return s;
    }

    private static boolean creeper(LivingEntity e) {
        return e instanceof Creeper;
    }

    public String id() {
        return modName.toLowerCase(Locale.ROOT);
    }

    public boolean allowedOn(LivingEntity e) {
        return blacklist == null || !blacklist.test(e);
    }

    /** Pairs that never roll together: Webber with Gravity or Blastoff (webs + knockback) */
    public boolean mixesWith(InfernalModifier o) {
        return !((this == WEBBER && (o == GRAVITY || o == BLASTOFF)) || (o == WEBBER && (this == GRAVITY || this == BLASTOFF)));
    }

    public static InfernalModifier byName(String s) {
        for (InfernalModifier m : values())
            if (m.modName.equalsIgnoreCase(s) || m.name().equalsIgnoreCase(s)) return m;
        return null;
    }

    public Component displayName() {
        return Component.translatableWithFallback("gtapexadditions.infernal.mod." + modName, modName);
    }

    public static Component prefix(String key) {
        return Component.translatableWithFallback("gtapexadditions.infernal.prefix." + key, L.getOrDefault("prefix." + key, " " + key + " "));
    }

    public static Component suffix(String key) {
        return Component.translatableWithFallback("gtapexadditions.infernal.suffix." + key, L.getOrDefault("suffix." + key, " " + key));
    }

    private static final Map<String, String> L = new HashMap<>();

    static {
        L.put("prefix.recurring", " recurring ");
        L.put("prefix.undying", " undying ");
        L.put("prefix.twinlived", " twinlived ");
        L.put("prefix.witchkin", " witchkin ");
        L.put("prefix.brewing", " brewing ");
        L.put("prefix.singed", " singed ");
        L.put("prefix.reckless", " reckless ");
        L.put("prefix.raging", " raging ");
        L.put("prefix.smashing", " smashing ");
        L.put("prefix.thumping", " thumping ");
        L.put("prefix.trolling", " trolling ");
        L.put("prefix.byebye", " byebye ");
        L.put("prefix.turtling", " turtling ");
        L.put("prefix.defensive", " defensive ");
        L.put("prefix.armoured", " armoured ");
        L.put("prefix.stalking", " stalking ");
        L.put("prefix.unseen", " unseen ");
        L.put("prefix.hunting", " hunting ");
        L.put("prefix.dark", " dark ");
        L.put("prefix.shadowkin", " shadowkin ");
        L.put("prefix.eclipsed", " eclipsed ");
        L.put("prefix.enderborn", " enderborn ");
        L.put("prefix.tricky", " tricky ");
        L.put("prefix.exhausting", " exhausting ");
        L.put("prefix.draining", " draining ");
        L.put("prefix.burning", " burning ");
        L.put("prefix.toasting", " toasting ");
        L.put("prefix.bombing", " bombing ");
        L.put("prefix.fireballsy", " fireballsy ");
        L.put("prefix.repulsing", " repulsing ");
        L.put("prefix.sproing", " sproing ");
        L.put("prefix.vampiric", " vampiric ");
        L.put("prefix.transfusing", " transfusing ");
        L.put("prefix.bloodsucking", " bloodsucking ");
        L.put("prefix.totallyzen", " totally zen ");
        L.put("prefix.innerlypeaceful", " innerly peaceful ");
        L.put("prefix.Ronin", " Ronin ");
        L.put("prefix.poisonous", " poisonous ");
        L.put("prefix.stinging", " stinging ");
        L.put("prefix.despoiling", " despoiling ");
        L.put("prefix.slowing", " slowing ");
        L.put("prefix.Quicksand", " Quicksand ");
        L.put("prefix.regenerating", " regenerating ");
        L.put("prefix.healing", " healing ");
        L.put("prefix.nighunkillable", " nigh unkillable ");
        L.put("prefix.rusting", " rusting ");
        L.put("prefix.decaying", " decaying ");
        L.put("prefix.hungering", " hungering ");
        L.put("prefix.starving", " starving ");
        L.put("prefix.sprinting", " sprinting ");
        L.put("prefix.swift", " swift ");
        L.put("prefix.charging", " charging ");
        L.put("prefix.thieving", " thieving ");
        L.put("prefix.snagging", " snagging ");
        L.put("prefix.quickfingered", " quick fingered ");
        L.put("prefix.striking", " striking ");
        L.put("prefix.thundering", " thundering ");
        L.put("prefix.electrified", " electrified ");
        L.put("prefix.thorned", " thorned ");
        L.put("prefix.thorny", " thorny ");
        L.put("prefix.spiky", " spiky ");
        L.put("prefix.apathetic", " apathetic ");
        L.put("prefix.deceiving", " deceiving ");
        L.put("prefix.ensnaring", " ensnaring ");
        L.put("prefix.webbing", " webbing ");
        L.put("prefix.withering", " withering ");
        L.put("prefix.Sith Lord", " Sith Lord ");
        L.put("prefix.Dark Lord", " Dark Lord ");
        L.put("prefix.Darth", " Darth ");
        L.put("suffix.ofRecurrence", " of Recurrence");
        L.put("suffix.theUndying", " the Undying");
        L.put("suffix.oftwinLives", " of twin Lives");
        L.put("suffix.theWitchkin", " the Witchkin");
        L.put("suffix.theBrewmaster", " the Brewmaster");
        L.put("suffix.theSinged", " the Singed");
        L.put("suffix.ofRecklessness", " of Recklessness");
        L.put("suffix.theRaging", " the Raging");
        L.put("suffix.ofSmashing", " of Smashing");
        L.put("suffix.ofMissionControl", " of Mission Control");
        L.put("suffix.theNASA", " the NASA");
        L.put("suffix.ofWEE", " of WEEEEE");
        L.put("suffix.ofTurtling", " of Turtling");
        L.put("suffix.theDefender", " the Defender");
        L.put("suffix.ofeffingArmor", " of effing Armor");
        L.put("suffix.ofStalking", " of Stalking");
        L.put("suffix.theUnseen", " the Unseen");
        L.put("suffix.thePredator", " the Predator");
        L.put("suffix.ofDarkness", " of Darkness");
        L.put("suffix.theShadow", " the Shadow");
        L.put("suffix.theEclipse", " the Eclipse");
        L.put("suffix.theEnderborn", " the EnderBorn");
        L.put("suffix.theTrickster", " the Trickster");
        L.put("suffix.ofFatigue", " of Fatigue");
        L.put("suffix.theDrainer", " the Drainer");
        L.put("suffix.ofConflagration", " of Conflagration");
        L.put("suffix.thePhoenix", " the Phoenix");
        L.put("suffix.ofCrispyness", " of Crispyness");
        L.put("suffix.OMFGFIREBALLS", " OMFGFIREBALLS");
        L.put("suffix.theBomber", " the Bomber");
        L.put("suffix.ofBallsofFire", " of Balls of Fire");
        L.put("suffix.ofRepulsion", " of Repulsion");
        L.put("suffix.theFlipper", " the Flipper");
        L.put("suffix.theVampire", " the Vampire");
        L.put("suffix.ofTransfusion", " of Transfusion");
        L.put("suffix.theBloodsucker", " the Bloodsucker");
        L.put("suffix.theZenMaster", " the Zen Master");
        L.put("suffix.ofEquilibrium", " of Equilibrium");
        L.put("suffix.ofInnerPeace", " of Inner Peace");
        L.put("suffix.ofVenom", " of Venom");
        L.put("suffix.thedeadlyChalice", " the Deadly Chalice");
        L.put("suffix.ofYouCantRun", " of You-Cant-Run");
        L.put("suffix.theSlowingB", " the slowing B****");
        L.put("suffix.ofWTFIMBA", " of WTF Imba");
        L.put("suffix.theCancerous", " the Cancerous");
        L.put("suffix.ofFirstAid", " of First Aid");
        L.put("suffix.ofDecay", " of Decay");
        L.put("suffix.theEquipmentHaunter", " the Equipment Haunter");
        L.put("suffix.ofHunger", " of Hunger");
        L.put("suffix.thePaleRider", " the Pale Rider");
        L.put("suffix.ofBolting", " of Bolting");
        L.put("suffix.theSwiftOne", " the Swift One");
        L.put("suffix.ofbeinginyourFace", " of being in your Face");
        L.put("suffix.ofSnagging", " of Snagging");
        L.put("suffix.theQuickFingered", " the Quick Fingered");
        L.put("suffix.ofPettyTheft", " of Petty Theft");
        L.put("suffix.yoink", " .. YOINK");
        L.put("suffix.ofLightning", " of Lightning");
        L.put("suffix.theRaiden", " the Raiden");
        L.put("suffix.ofRetribution", " of Retribution");
        L.put("suffix.theThorned", " the Thorned");
        L.put("suffix.ofStrikingBack", " of Striking Back");
        L.put("suffix.ofStriking", " of Striking");
        L.put("suffix.ofApathy", " of Apathy");
        L.put("suffix.theDeceiver", " the Deceiver");
        L.put("suffix.ofTraps", " of Traps");
        L.put("suffix.theMutated", " the Mutated");
        L.put("suffix.theSpider", " the Spider");
        L.put("suffix.ofDarkSkulls", " of Dark Skulls");
        L.put("suffix.Doomskull", " Doomskull");
        L.put("suffix.ofBreathlessness", " of Breathlessness");
        L.put("suffix.theAnaerobic", " the Anaerobic");
        L.put("suffix.ofDeprivation", " of Deprivation");
    }
}
